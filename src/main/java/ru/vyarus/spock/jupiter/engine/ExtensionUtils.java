package ru.vyarus.spock.jupiter.engine;

import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.extension.AfterAllCallback;
import org.junit.jupiter.api.extension.AfterEachCallback;
import org.junit.jupiter.api.extension.AfterTestExecutionCallback;
import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.BeforeTestExecutionCallback;
import org.junit.jupiter.api.extension.ExecutionCondition;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.extension.Extension;
import org.junit.jupiter.api.extension.InvocationInterceptor;
import org.junit.jupiter.api.extension.ParameterResolver;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.junit.jupiter.api.extension.TestExecutionExceptionHandler;
import org.junit.jupiter.api.extension.TestInstanceFactory;
import org.junit.jupiter.api.extension.TestInstancePostProcessor;
import org.junit.jupiter.api.extension.TestInstancePreDestroyCallback;
import org.junit.jupiter.api.extension.TestWatcher;
import org.junit.platform.commons.util.AnnotationUtils;
import org.junit.platform.commons.util.Preconditions;
import org.junit.platform.commons.util.ReflectionUtils;

import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;
import static org.junit.platform.commons.util.AnnotationUtils.findAnnotation;
import static org.junit.platform.commons.util.AnnotationUtils.findRepeatableAnnotations;
import static org.junit.platform.commons.util.AnnotationUtils.isAnnotated;
import static org.junit.platform.commons.util.ReflectionUtils.HierarchyTraversalMode.TOP_DOWN;
import static org.junit.platform.commons.util.ReflectionUtils.findFields;
import static org.junit.platform.commons.util.ReflectionUtils.tryToReadFieldValue;

/**
 * @author Vyacheslav Rusakov
 * @since 30.11.2021
 */
public class ExtensionUtils {

    // see full list in org.junit.jupiter.api.extension.RegisterExtension
    public static final List<Class<? extends Extension>> SUPPORTED_EXTENSIONS = Arrays.asList(
            BeforeAllCallback.class,
            AfterAllCallback.class,
            BeforeEachCallback.class,
            AfterEachCallback.class,
            BeforeTestExecutionCallback.class,
            AfterTestExecutionCallback.class
    );

    public static final List<Class<? extends Extension>> UNSUPPORTED_EXTENSIONS = Arrays.asList(
            ExecutionCondition.class,
            InvocationInterceptor.class,
            TestInstanceFactory.class,
            TestInstancePostProcessor.class,
            TestInstancePreDestroyCallback.class,
            ParameterResolver.class,
            TestExecutionExceptionHandler.class,
            TestWatcher.class
            // TestTemplateInvocationContextProvider not included because it doesn't matter
    );

    public static ExtensionRegistry createRegistry(final Class<?> testClass) {
        final ExtensionRegistry registry = new ExtensionRegistry(null);
        findClassExtensions(testClass).forEach(registry::registerExtension);
        return registry;
    }

    // source: org.junit.jupiter.engine.descriptor.ExtensionUtils.populateNewExtensionRegistryFromExtendWithAnnotation
    public static ExtensionRegistry createMethodRegistry(final ExtensionRegistry root, final Method method) {
        final Stream<Class<? extends Extension>> extensions = streamExtensionTypes(method);
        final ExtensionRegistry registry = new ExtensionRegistry(root);
        extensions.forEach(registry::registerExtension);
        return registry;
    }

    public static Stream<Class<? extends Extension>> findClassExtensions(final Class<?> testClass) {
        return streamExtensionTypes(AnnotationUtils.findRepeatableAnnotations(testClass, ExtendWith.class));
    }

    /**
     * Register extensions using the supplied registrar from fields in the supplied
     * class that are annotated with {@link ExtendWith @ExtendWith} or
     * {@link RegisterExtension @RegisterExtension}.
     *
     * <p>The extensions will be sorted according to {@link Order @Order} semantics
     * prior to registration.
     *
     * @param registrar the registrar with which to register the extensions; never {@code null}
     * @param clazz     the class or interface in which to find the fields; never {@code null}
     * @param instance  the instance of the supplied class; may be {@code null}
     *                  when searching for {@code static} fields in the class
     */
    // based on org.junit.jupiter.engine.descriptor.ExtensionUtils from junit-jupiter-engine (5.8)
    public static void registerExtensionsFromFields(ExtensionRegistry registrar, Class<?> clazz, Object instance) {
        Preconditions.notNull(registrar, "ExtensionRegistrar must not be null");
        Preconditions.notNull(clazz, "Class must not be null");

        Predicate<Field> predicate = (instance == null ? ReflectionUtils::isStatic : ReflectionUtils::isNotStatic);

        findFields(clazz, predicate, TOP_DOWN).stream()
                .sorted(orderComparator)
                .forEach(field -> {
                    List<Class<? extends Extension>> extensionTypes = streamExtensionTypes(field).collect(toList());
                    boolean isExtendWithPresent = !extensionTypes.isEmpty();
                    boolean isRegisterExtensionPresent = isAnnotated(field, RegisterExtension.class);
                    if (isExtendWithPresent) {
                        extensionTypes.forEach(registrar::registerExtension);
                    }
                    if (isRegisterExtensionPresent) {
                        tryToReadFieldValue(field, instance).ifSuccess(value -> {
                            Preconditions.condition(value instanceof Extension, () -> String.format(
                                    "Failed to register extension via @RegisterExtension field [%s]: field value's type [%s] must implement an [%s] API.",
                                    field, (value != null ? value.getClass().getName() : null), Extension.class.getName()));

                            if (isExtendWithPresent) {
                                Class<?> valueType = value.getClass();
                                extensionTypes.forEach(extensionType -> {
                                    Preconditions.condition(!extensionType.equals(valueType),
                                            () -> String.format("Failed to register extension via field [%s]. "
                                                            + "The field registers an extension of type [%s] via @RegisterExtension and @ExtendWith, "
                                                            + "but only one registration of a given extension type is permitted.",
                                                    field, valueType.getName()));
                                });
                            }

                            registrar.registerExtension((Extension) value, field);
                        });
                    }
                });
    }

    private static final Comparator<Field> orderComparator = Comparator.comparingInt(ExtensionUtils::getOrder);

    private static int getOrder(Field field) {
        return findAnnotation(field, Order.class).map(Order::value).orElse(Order.DEFAULT);
    }

    private static Stream<Class<? extends Extension>> streamExtensionTypes(AnnotatedElement annotatedElement) {
        return streamExtensionTypes(findRepeatableAnnotations(annotatedElement, ExtendWith.class));
    }

    private static Stream<Class<? extends Extension>> streamExtensionTypes(
            final List<ExtendWith> extendWithAnnotations) {
        return extendWithAnnotations.stream().map(ExtendWith::value).flatMap(Arrays::stream);
    }
}
