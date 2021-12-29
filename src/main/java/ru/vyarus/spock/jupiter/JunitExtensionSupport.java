package ru.vyarus.spock.jupiter;

import org.spockframework.runtime.extension.IGlobalExtension;
import org.spockframework.runtime.model.SpecInfo;
import ru.vyarus.spock.jupiter.engine.ExtensionRegistry;
import ru.vyarus.spock.jupiter.engine.ExtensionUtils;
import ru.vyarus.spock.jupiter.engine.context.ClassContext;
import ru.vyarus.spock.jupiter.engine.execution.ConditionEvaluator;
import ru.vyarus.spock.jupiter.interceptor.ExtensionLifecycleMerger;

/**
 * Global extension applied to all specs. Searches for declared junit extensions and calls them simulating junit
 * extensions lifecycle. Supported all the same declaration methods as with junit:
 * <ul>
 *     <li>{@code @ExtendsWith} declaration on class, method or parameter</li>
 *     <li>Custom annotations on class method or parameter</li>
 *     <li>{@code @RegisterExtension} on static or non-static fields</li>
 *     <li>Parameters injection into fixture and test methods</li>
 *     <li>{@code ExecutionConditions} on class or method (so, for example, {@link org.junit.jupiter.api.Disabled}
 *     will force skipping tests.</li>
 * </ul>
 * <p>
 * Junit extensions are executed before spock extensions. For example, {@code BeforeAllCallback} extension would be
 * executed before spock extensions using {@code interceptSetupSpecMethod} (because extension is global it would
 * be executed before all other spock extensions and that's why junit extensions will work in priority).
 * <p>
 * Supported almost all junit extension types, except {@link ExtensionUtils#UNSUPPORTED_EXTENSIONS} (exception
 * handling, test watching, invocation interceptor and of course test instance factory). If not supported  extension
 * type would be detected in extension, warning will be logged.
 * <p>
 * Auto-detected (service loader declared extensions), default and synthetic junit extensions are not supported:
 * auto-detection would be not obvious (such extensions could always be registered manually), and the other two
 * are too jupiter specific.
 * <p>
 * Implementation copies and re-use many jupiter-engine mechanisms (junit-jupiter-engine artifact) and so works
 * (mostly) exactly the same as in jupiter (5.8). There are comments all over the code for jupiter-engine reference
 * implementations.
 * <p>
 * Overall workflow:
 * <ul>
 *     <li>for each spec extension registry ({@link ExtensionRegistry} is always created, containing all found
 *     extensions.</li>
 *     <li>{@link ClassContext} created to represent spec-level context (it will be used as parameter for
 *     before/after all and instance post processor extensions)</li>
 *     <li>Before feature method execution, extended {@link ExtensionRegistry} is created</li>
 *     <li>{@link ru.vyarus.spock.jupiter.engine.context.MethodContext} created for feature level (it will be used as
 *     parameter for all other extensions)</li>
 *     <li>Note that fresh method context is created for each method execution: in case of data-driven methods,
 *     each iteration will have its own context (required because method extensions must be renewed for each
 *     execution)</li>
 *     <li>Pre destroy extension is called after cleanup (after cleanup methods and cleanup of spock extensions)</li>
 * </ul>
 * <p>
 * Spock's shared state is not used: don't mark {@link org.junit.jupiter.api.extension.RegisterExtension}
 * extensions with {@code @Shared} - they will still be processed as instance-level extensions. Use static fields
 * to declare spec-wide extensions (same as in jupiter).
 *
 * @author Vyacheslav Rusakov
 * @see ExtensionLifecycleMerger for lifecycle details
 * @since 25.11.2021
 */
public class JunitExtensionSupport implements IGlobalExtension {

    @Override
    public void visitSpec(final SpecInfo spec) {
        // org.junit.jupiter.engine.descriptor.ClassBasedTestDescriptor.prepare
        final Class<?> testClass = spec.getReflection();
        final ExtensionRegistry registry = ExtensionUtils.createRegistry(testClass);

        // Register extensions from static fields here, at the class level but
        // after extensions registered via @ExtendWith.
        ExtensionUtils.registerExtensionsFromFields(registry, testClass, null);

        // register extensions from setupSpec, setup, cleanup, cleanupSpec method parameters
        spec.getAllFixtureMethods().forEach(methodInfo -> ExtensionUtils
                .registerExtensionsFromExecutableParameters(registry, methodInfo.getReflection()));

        final ClassContext specContext = new ClassContext(registry, spec);

        if (ConditionEvaluator.skip(spec, specContext)) {
            // skip execution based on junit's ExecutionCondition
            return;
        }

        // note: method-level (feature) extensions are collected just before feature execution because
        // in case of data-providers same feature would be executed several times and each time extensions
        // must be renewed (including field-based instances registered with @RegisterExtensions).

        // https://spockframework.org/spock/docs/2.0/extensions.html
        final ExtensionLifecycleMerger interceptor = new ExtensionLifecycleMerger(specContext);
        spec.addSetupSpecInterceptor(interceptor);
        spec.addInitializerInterceptor(interceptor);
        spec.addSetupInterceptor(interceptor);
        spec.addCleanupInterceptor(interceptor);
        spec.addCleanupSpecInterceptor(interceptor);

        // add support for custom parameters on setup(spec)/cleanup(spec) methods
        spec.getAllFixtureMethods().forEach(methodInfo ->
                methodInfo.addInterceptor(interceptor.getFixtureMethodsInterceptor()));

        // intercept test methods (inject parameters and before/after execution hooks)
        spec.getAllFeatures().forEach(featureInfo -> featureInfo.getFeatureMethod().addInterceptor(interceptor));
    }
}
