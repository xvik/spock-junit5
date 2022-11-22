package ru.vyarus.spock.jupiter;

import org.junit.jupiter.api.extension.ExtensionContext.Namespace;
import org.junit.jupiter.api.extension.ExtensionContext.Store;
import org.spockframework.runtime.extension.IGlobalExtension;
import org.spockframework.runtime.extension.IMethodInvocation;
import org.spockframework.runtime.model.MethodKind;
import org.spockframework.runtime.model.SpecInfo;
import ru.vyarus.spock.jupiter.engine.ExtensionRegistry;
import ru.vyarus.spock.jupiter.engine.ExtensionUtils;
import ru.vyarus.spock.jupiter.engine.context.ClassContext;
import ru.vyarus.spock.jupiter.engine.context.EngineContext;
import ru.vyarus.spock.jupiter.engine.execution.ConditionEvaluator;
import ru.vyarus.spock.jupiter.interceptor.ExtensionLifecycleMerger;

/**
 * Global extension applied to all specs. Searches for declared junit extensions and calls them simulating junit
 * extensions lifecycle. Supported all the same declaration methods as with junit:
 * <ul>
 *     <li>{@code @ExtendsWith} declaration on class, method, field or parameter</li>
 *     <li>Custom annotations on class, method, field or parameter</li>
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
 * extensions with {@code @Shared} - they will always be null as spock manage them on different instance. Use static
 * fields to declare spec-wide extensions (same as in jupiter).
 * <p>
 * Special API added to allow SPOCK extensions accessing junit shared state (used by all extensions to store values):
 * {@link #getStore(SpecInfo, Namespace)} and {@link #getStore(IMethodInvocation, Namespace)}. This might be used
 * by spock extension authors to access junit state values or to simply using junit state as there is no
 * alternative feature in spock itself.
 *
 * @author Vyacheslav Rusakov
 * @see ExtensionLifecycleMerger for lifecycle details
 * @since 25.11.2021
 */
public class JunitExtensionSupport implements IGlobalExtension {

    private EngineContext engineContext;

    @Override
    public void start() {
        engineContext = new EngineContext();
    }

    @Override
    public void stop() {
        if (engineContext != null) {
            engineContext.close();
            engineContext = null;
        }
    }

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

        final ClassContext specContext = new ClassContext(engineContext, registry, spec);

        // condition check must be delayed to let spock extensions to work first because otherwise it is impossible
        // to prevent condition exception (required for tests)
        spec.addInterceptor(invocation -> {
            if (ConditionEvaluator.skip(spec, specContext)) {
                // skip execution based on junit's ExecutionCondition
                return;
            }
            invocation.proceed();
        });


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

    // SPOCK extensions API (allow easy access to junit storage)

    /**
     * Storage used by junit extensions to keep local state
     * (<a href="https://junit.org/junit5/docs/current/user-guide/#extensions-keeping-state">see docs</a>). Spock does
     * not have anything like this. This method allows spock extension authors to access storage used by junit
     * extensions and so access contained values. May be also used for storing values by spock extensions.
     * <p>
     * Storage is hierarchical: there are spec level storage (used by
     * {@link ConditionEvaluator}, {@link org.junit.jupiter.api.extension.BeforeAllCallback},
     * {@link org.junit.jupiter.api.extension.AfterAllCallback} and
     * {@link org.junit.jupiter.api.extension.TestInstancePreDestroyCallback} when no test instance is available).
     * For each test instance created for feature (test method or test method data-iteration) new storage created
     * (see {@link #getStore(IMethodInvocation, Namespace)} for accessing). Child storage level could see all parent
     * values, but not modify them.
     * <p>
     * In short: this method provides class-wide (spec-wide) storage, which values are visible in all test methods.
     * This is the same call as {@link org.junit.jupiter.api.extension.ExtensionContext#getStore(Namespace)}.
     * Method might be called at any time because extension is global and so will work before any other custom
     * spock extension.
     *
     * @param spec      specification instance
     * @param namespace target namespace
     * @return namespaced storage instance
     * @throws NullPointerException if junit extension interceptor could not be found
     */
    public static Store getStore(final SpecInfo spec, final Namespace namespace) {
        return findInterceptor(spec).getSpecContext().getStore(namespace);
    }

    /**
     * In contrast to {@link #getStore(SpecInfo, Namespace)} provide either class-level or method-level context
     * (depends on test instance presence). In most cases simply use this method to get storage from the most
     * actual context level (but if you need only root level use spec-based method: for example, might be useful
     * if you need to modify root storage values).
     * <p>
     * Feature-wide storage context created on just after spock initialization event (not shared initialization!) and
     * destroyed after cleanup event. New instance created for EACH test method execution (in case of data-driven tests
     * for each iteration!).
     * <p>
     * Method level context is used for junit extensions: {@link org.junit.jupiter.api.extension.BeforeEachCallback},
     * {@link org.junit.jupiter.api.extension.BeforeTestExecutionCallback},
     * {@link org.junit.jupiter.api.extension.AfterTestExecutionCallback},
     * {@link org.junit.jupiter.api.extension.ParameterResolver},
     * {@link org.junit.jupiter.api.extension.AfterEachCallback} and
     * {@link org.junit.jupiter.api.extension.TestInstancePreDestroyCallback}.
     * <p>
     * This is the same call as {@link org.junit.jupiter.api.extension.ExtensionContext#getStore(Namespace)}.
     * Method context will see all values from spec-level, but will not be able to modify them (if you try to set value
     * it would modify method level only; same for remove).
     *
     * @param invocation spock extension parameter
     * @param namespace  target namespace
     * @return namespaced storage instance (method or class level)
     * @throws NullPointerException if junit extension interception not found, test instance is not yet available
     *                              or method context not found when should be
     */
    public static Store getStore(final IMethodInvocation invocation, final Namespace namespace) {
        // method context created AFTER complete initialization, so it would be impossible to have method context here
        return invocation.getMethod().getKind() == MethodKind.INITIALIZER
                // filter shared init case (instance present, but method context would not be ready)
                || invocation.getFeature() == null
                || invocation.getInstance() == null
                ? getStore(invocation.getSpec(), namespace)
                : findInterceptor(invocation.getSpec()).getMethodContext(invocation).getStore(namespace);
    }

    private static ExtensionLifecycleMerger findInterceptor(final SpecInfo spec) {
        return (ExtensionLifecycleMerger) spec.getSetupSpecInterceptors().stream()
                .filter(interceptor -> interceptor instanceof ExtensionLifecycleMerger)
                .findFirst().orElseThrow(() ->
                        new NullPointerException("Junit support not found in spec: " + spec.getDisplayName()));
    }
}
