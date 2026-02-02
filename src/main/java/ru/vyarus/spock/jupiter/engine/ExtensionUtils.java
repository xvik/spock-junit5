package ru.vyarus.spock.jupiter.engine;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
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
import org.junit.jupiter.api.extension.LifecycleMethodExecutionExceptionHandler;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.api.extension.ParameterResolutionException;
import org.junit.jupiter.api.extension.ParameterResolver;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.junit.jupiter.api.extension.TestExecutionExceptionHandler;
import org.junit.jupiter.api.extension.TestInstanceFactory;
import org.junit.jupiter.api.extension.TestInstancePostProcessor;
import org.junit.jupiter.api.extension.TestInstancePreConstructCallback;
import org.junit.jupiter.api.extension.TestInstancePreDestroyCallback;
import org.junit.jupiter.api.extension.TestTemplateInvocationContextProvider;
import org.junit.jupiter.api.extension.TestWatcher;
import org.junit.platform.commons.PreconditionViolationException;
import org.junit.platform.commons.logging.Logger;
import org.junit.platform.commons.logging.LoggerFactory;
import org.junit.platform.commons.util.Preconditions;
import org.junit.platform.commons.util.ReflectionUtils;
import org.junit.platform.commons.util.StringUtils;
import org.junit.platform.commons.util.UnrecoverableExceptions;
import org.spockframework.runtime.model.MethodInfo;
import ru.vyarus.spock.jupiter.engine.context.AbstractContext;

import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Constructor;
import java.lang.reflect.Executable;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Predicate;
import java.util.stream.Stream;

import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toList;
import static org.junit.platform.commons.util.AnnotationUtils.findAnnotation;
import static org.junit.platform.commons.util.AnnotationUtils.findRepeatableAnnotations;
import static org.junit.platform.commons.util.AnnotationUtils.isAnnotated;
import static org.junit.platform.commons.util.ReflectionUtils.HierarchyTraversalMode.TOP_DOWN;
import static org.junit.platform.commons.util.ReflectionUtils.isAssignableTo;
import static org.junit.platform.commons.util.ReflectionUtils.streamFields;
import static org.junit.platform.commons.util.ReflectionUtils.tryToReadFieldValue;

/**
 * Extensions recognition logic. Mostly copy of jupiter implementation methods (with slight adoptions) to preserve
 * exactly the same behavior. Started as a subset of {@code org.junit.jupiter.engine.descriptor.ExtensionUtils},
 * but also include some descriptors logic (descriptors concept itself is not required in spock context).
 *
 * @author Vyacheslav Rusakov
 * @since 30.11.2021
 */
@SuppressWarnings({"checkstyle:MultipleStringLiterals", "PMD.ExcessiveImports"})
@SuppressFBWarnings("MS_MUTABLE_COLLECTION_PKGPROTECT")
public final class ExtensionUtils {

    // see full list in org.junit.jupiter.api.extension.RegisterExtension
    public static final List<Class<? extends Extension>> SUPPORTED_EXTENSIONS = Arrays.asList(
            ExecutionCondition.class,
            BeforeAllCallback.class,
            AfterAllCallback.class,
            BeforeEachCallback.class,
            AfterEachCallback.class,
            BeforeTestExecutionCallback.class,
            AfterTestExecutionCallback.class,
            ParameterResolver.class,
            TestInstancePostProcessor.class,
            TestInstancePreDestroyCallback.class,
            TestExecutionExceptionHandler.class
    );

    public static final List<Class<? extends Extension>> UNSUPPORTED_EXTENSIONS = Arrays.asList(
            // not included because it doesn't matter in context of spock
            TestTemplateInvocationContextProvider.class,

            // impossible to add (spock does not allow this)
            TestInstanceFactory.class,
            TestInstancePreConstructCallback.class,
            // could be supported, but what for?
            LifecycleMethodExecutionExceptionHandler.class,
            InvocationInterceptor.class,
            // support could be added, but this is too specific (will never be required)
            TestWatcher.class
    );

    private static final Logger LOGGER = LoggerFactory.getLogger(ExtensionUtils.class);

    private static final Comparator<Field> ORDER_COMPARATOR = Comparator.comparingInt(ExtensionUtils::getOrder);

    private static final Predicate<Field> FIELD_EXTENSION =
            field -> isAnnotated(field, RegisterExtension.class)
                    || !findRepeatableAnnotations(field, ExtendWith.class).isEmpty();

    private ExtensionUtils() {
    }

    public static ExtensionRegistry createRegistry(final Class<?> testClass) {
        final ExtensionRegistry registry = new ExtensionRegistry(null);
        findClassExtensions(testClass).forEach(registry::registerExtension);
        return registry;
    }

    // source: org.junit.jupiter.engine.descriptor.TestMethodTestDescriptor.populateNewExtensionRegistry
    // (aggregates several deeper methods)
    public static ExtensionRegistry createMethodRegistry(final ExtensionRegistry root, final Method method) {
        final Stream<Class<? extends Extension>> extensions = streamDeclarativeExtensionTypes(method);
        final ExtensionRegistry registry = new ExtensionRegistry(root);
        extensions.forEach(registry::registerExtension);

        // extensions from method parameters
        registerExtensionsFromExecutableParameters(registry, method);
        return registry;
    }

    public static Stream<Class<? extends Extension>> findClassExtensions(final Class<?> testClass) {
        return streamDeclarativeExtensionTypes(findRepeatableAnnotations(testClass, ExtendWith.class));
    }

    /**
     * Register extensions using the supplied registrar from static fields in
     * the supplied class that are annotated with {@link ExtendWith @ExtendWith}
     * or {@link RegisterExtension @RegisterExtension}.
     *
     * <p>The extensions will be sorted according to {@link Order @Order} semantics
     * prior to registration.
     *
     * @param registrar the registrar with which to register the extensions; never {@code null}
     * @param clazz     the class or interface in which to find the fields; never {@code null}
     * @since 5.11
     */
    // based on org.junit.jupiter.engine.descriptor.ExtensionUtils.registerExtensionsFromStaticFields
    public static void registerExtensionsFromStaticFields(final ExtensionRegistry registrar, final Class<?> clazz) {
        streamExtensionRegisteringFields(clazz, ReflectionUtils::isStatic)
                .forEach(field -> {
                    final List<Class<? extends Extension>> extensionTypes = streamDeclarativeExtensionTypes(field)
                            .collect(toList());
                    final boolean isExtendWithPresent = !extensionTypes.isEmpty();

                    if (isExtendWithPresent) {
                        extensionTypes.forEach(registrar::registerExtension);
                    }
                    if (isAnnotated(field, RegisterExtension.class)) {
                        final Extension extension = readAndValidateExtensionFromField(field, null, extensionTypes);
                        registrar.registerExtension(extension, field);
                    }
                });
    }

    /**
     * Register extensions using the supplied registrar from instance fields in
     * the supplied class that are annotated with {@link ExtendWith @ExtendWith}
     * or {@link RegisterExtension @RegisterExtension}.
     *
     * <p>The extensions will be sorted according to {@link Order @Order} semantics
     * prior to registration.
     *
     * @param registrar the registrar with which to register the extensions; never {@code null}
     * @param clazz     the class or interface in which to find the fields; never {@code null}
     * @since 5.11
     */
    // based on org.junit.jupiter.engine.descriptor.ExtensionUtils.registerExtensionsFromInstanceFields
    public static void registerExtensionsFromInstanceFields(final ExtensionRegistry registrar, final Class<?> clazz) {
        streamExtensionRegisteringFields(clazz, ReflectionUtils::isNotStatic)
                .forEach(field -> {
                    final List<Class<? extends Extension>> extensionTypes = streamDeclarativeExtensionTypes(field)
                            .collect(toList());
                    final boolean isExtendWithPresent = !extensionTypes.isEmpty();

                    if (isExtendWithPresent) {
                        extensionTypes.forEach(registrar::registerExtension);
                    }
                    if (isAnnotated(field, RegisterExtension.class)) {
                        registrar.registerUninitializedExtension(clazz, field,
                                instance -> readAndValidateExtensionFromField(field, instance, extensionTypes));
                    }
                });
    }

    /**
     * Register extensions using the supplied registrar from parameters in the
     * supplied {@link Executable} (i.e., a {@link java.lang.reflect.Constructor}
     * or {@link java.lang.reflect.Method}) that are annotated with
     * {@link ExtendWith @ExtendWith}.
     *
     * @param registrar  the registrar with which to register the extensions; never {@code null}
     * @param executable the constructor or method whose parameters should be searched; never {@code null}
     */
    // based on org.junit.jupiter.engine.descriptor.ExtensionUtils.registerExtensionsFromExecutableParameters
    public static void registerExtensionsFromExecutableParameters(final ExtensionRegistry registrar,
                                                                  final Executable executable) {
        Preconditions.notNull(registrar, "ExtensionRegistry must not be null");
        Preconditions.notNull(executable, "Executable must not be null");

        final AtomicInteger index = new AtomicInteger();

        Arrays.stream(executable.getParameters())
                .map(parameter -> findRepeatableAnnotations(parameter, index.getAndIncrement(), ExtendWith.class))
                .flatMap(ExtensionUtils::streamDeclarativeExtensionTypes)
                .forEach(registrar::registerExtension);
    }

    // based on org.junit.jupiter.engine.execution.ExecutableInvoker.resolveParameter
    @SuppressWarnings("PMD.AvoidRethrowingException")
    public static Object resolveParameter(final ParameterContext parameterContext,
                                          final Executable executable,
                                          final AbstractContext context) {
        try {
            final List<ParameterResolver> exts = context.getRegistry().stream(ParameterResolver.class)
                    .filter(resolver -> resolver.supportsParameter(parameterContext, context))
                    .collect(toList());

            if (exts.isEmpty()) {
                // no problem - assume other spock extension  supposed to proceed with this parameter
                return MethodInfo.MISSING_ARGUMENT;
            }

            if (exts.size() > 1) {
                final String resolvers = exts.stream()
                        .map(parameterResolver -> parameterResolver.getClass().getSimpleName())
                        .collect(joining(", "));
                throw new ParameterResolutionException(
                        String.format("Discovered multiple competing ParameterResolvers for parameter [%s] in "
                                        + "%s [%s]: %s",
                                parameterContext.getParameter(), asLabel(executable), executable.toGenericString(),
                                resolvers));
            }

            final ParameterResolver resolver = exts.get(0);
            final Object value = resolver.resolveParameter(parameterContext, context);
            validateResolvedType(parameterContext.getParameter(), value, executable, resolver);

            LOGGER.debug(() -> String.format(
                    "ParameterResolver [%s] resolved a value of type [%s] for parameter [%s] in %s [%s].",
                    resolver.getClass().getName(), (value != null ? value.getClass().getName() : null),
                    parameterContext.getParameter(), asLabel(executable), executable.toGenericString()));

            return value;
        } catch (ParameterResolutionException ex) {
            throw ex;
        } catch (Throwable throwable) {
            UnrecoverableExceptions.rethrowIfUnrecoverable(throwable);

            String message = String.format("Failed to resolve parameter [%s] in %s [%s]",
                    parameterContext.getParameter(), asLabel(executable), executable.toGenericString());

            if (StringUtils.isNotBlank(throwable.getMessage())) {
                message += ": " + throwable.getMessage();
            }

            throw new ParameterResolutionException(message, throwable);
        }
    }

    /**
     * @param executable executable
     * @return correct executable name
     */
    public static String asLabel(final Executable executable) {
        return executable instanceof Constructor ? "constructor" : "method";
    }

    private static Extension readAndValidateExtensionFromField(
            final Field field,
            final Object instance,
            final List<Class<? extends Extension>> declarativeExtensionTypes) {
        final Object value = tryToReadFieldValue(field, instance)
                .getOrThrow(e -> new PreconditionViolationException(
                        String.format("Failed to read @RegisterExtension field [%s]", field), e));

        Preconditions.condition(value instanceof Extension, () -> String.format(
                "Failed to register extension via @RegisterExtension field [%s]: field value's type [%s] "
                        + "must implement an [%s] API.",
                field, (value != null ? value.getClass().getName() : null), Extension.class.getName()));

        declarativeExtensionTypes.forEach(extensionType -> {
            final Class<?> valueType = value.getClass();
            Preconditions.condition(!extensionType.equals(valueType),
                    () -> String.format(
                            "Failed to register extension via field [%s]. "
                                    + "The field registers an extension of type [%s] via @RegisterExtension and "
                                    + "@ExtendWith, but only one registration of a given extension type is permitted.",
                            field, valueType.getName()));
        });

        return (Extension) value;
    }

    private static void validateResolvedType(final Parameter parameter,
                                             final Object value,
                                             final Executable executable,
                                             final ParameterResolver resolver) {

        final Class<?> type = parameter.getType();

        // Note: null is permissible as a resolved value but only for non-primitive types.
        if (!isAssignableTo(value, type)) {
            final String message;
            if (value == null && type.isPrimitive()) {
                message = String.format(
                        "ParameterResolver [%s] resolved a null value for parameter [%s] "
                                + "in method [%s], but a primitive of type [%s] is required.",
                        resolver.getClass().getName(), parameter, executable.toGenericString(), type.getName());
            } else {
                message = String.format(
                        "ParameterResolver [%s] resolved a value of type [%s] for parameter [%s] "
                                + "in method [%s], but a value assignment compatible with [%s] is required.",
                        resolver.getClass().getName(), (value != null ? value.getClass().getName() : null), parameter,
                        executable.toGenericString(), type.getName());
            }

            throw new ParameterResolutionException(message);
        }
    }

    private static int getOrder(final Field field) {
        return findAnnotation(field, Order.class).map(Order::value).orElse(Order.DEFAULT);
    }

    private static Stream<Field> streamExtensionRegisteringFields(final Class<?> clazz,
                                                                  final Predicate<Field> predicate) {
        return streamFields(clazz, predicate.and(FIELD_EXTENSION), TOP_DOWN)
                .sorted(ORDER_COMPARATOR);
    }

    private static Stream<Class<? extends Extension>> streamDeclarativeExtensionTypes(
            final AnnotatedElement annotatedElement) {
        return streamDeclarativeExtensionTypes(findRepeatableAnnotations(annotatedElement, ExtendWith.class));
    }

    private static Stream<Class<? extends Extension>> streamDeclarativeExtensionTypes(
            final List<ExtendWith> extendWithAnnotations) {
        return extendWithAnnotations.stream().map(ExtendWith::value).flatMap(Arrays::stream);
    }
}
