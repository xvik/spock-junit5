package ru.vyarus.spock.jupiter.interceptor;

import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.platform.commons.logging.Logger;
import org.junit.platform.commons.logging.LoggerFactory;
import org.junit.platform.commons.util.Preconditions;
import org.junit.platform.commons.util.UnrecoverableExceptions;
import org.spockframework.runtime.extension.AbstractMethodInterceptor;
import org.spockframework.runtime.extension.IMethodInterceptor;
import org.spockframework.runtime.extension.IMethodInvocation;
import org.spockframework.runtime.model.FeatureInfo;
import org.spockframework.runtime.model.MethodInfo;
import ru.vyarus.spock.jupiter.engine.ExtensionRegistry;
import ru.vyarus.spock.jupiter.engine.ExtensionUtils;
import ru.vyarus.spock.jupiter.engine.context.AbstractContext;
import ru.vyarus.spock.jupiter.engine.context.ClassContext;
import ru.vyarus.spock.jupiter.engine.context.DefaultParameterContext;
import ru.vyarus.spock.jupiter.engine.context.MethodContext;
import ru.vyarus.spock.jupiter.engine.execution.ConditionEvaluator;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Merges junit extensions lifecycle into spock. Junit extensions executed before any other spock extensions
 * (only other global spock extensions may be executed before, but they are almost never used).
 * <p>
 * Implementation based on {@code org.junit.jupiter.engine.descriptor.ClassBasedTestDescriptor} and
 * {@code org.junit.jupiter.engine.descriptor.TestMethodTestDescriptor} from junit-jupiter-engine.
 * <p>
 * NOTE: spock {@code @Shared} fields could not be supported as it would require duplicate beforeEach execution
 * with different instances, which would probably break many junit extensions. As a result, {@code @Shared} fields
 * would ALWAYS be null (because they are managed in a different instance - extension would not be able to initialize
 * it in any case). This would produce ambiguous situations I can't detect. And so there is only one rule:
 * nothing junit-specific should use {@code @Shared} annotation.
 *
 * @author Vyacheslav Rusakov
 * @since 30.11.2021
 */
public class ExtensionLifecycleMerger extends AbstractMethodInterceptor {

    private final Logger logger = LoggerFactory.getLogger(ExtensionLifecycleMerger.class);

    // map bounds test instance to its context
    // thread local is not an option due to testKit tests (spock test calling spock test)
    private final Map<Object, MethodContext> methods = new ConcurrentHashMap<>();

    private final ClassContext context;
    private final JunitApiExecutor junit = new JunitApiExecutor();
    private final IMethodInterceptor fixtureMethodsInterceptor;

    public ExtensionLifecycleMerger(final ClassContext context) {
        this.context = context;

        fixtureMethodsInterceptor = invocation -> {
            AbstractContext ctx = context;
            // setup/cleanup methods must use method context
            if (invocation.getFeature() != null) {
                ctx = getMethodContext(invocation);
            }
            injectArguments(invocation, ctx);
            // intercepting here because the main interceptor does not receive exceptions (they are already eaten)
            try {
                invocation.proceed();
            } catch (Throwable ex) {
                handleFixtureMethodException(invocation, ctx, ex);
            }
        };
    }

    /**
     * Interceptor externalized in order to unify all interceptor registration in one place.
     *
     * @return interceptor for fixture methods (setup/cleanup)
     */
    public IMethodInterceptor getFixtureMethodsInterceptor() {
        return fixtureMethodsInterceptor;
    }

    /**
     * @return spec-level context
     */
    public ClassContext getSpecContext() {
        return context;
    }

    /**
     * @param invocation spock event
     * @return method context
     * @throws NullPointerException when context not found
     */
    public MethodContext getMethodContext(final IMethodInvocation invocation) {
        return Preconditions.notNull(methods.get(invocation.getInstance()), () -> "Method context not found for '"
                + invocation.getSpec().getDisplayName() + "/" + invocation.getFeature().getDisplayName()
                + "' feature (spock " + invocation.getMethod().getKind() + " phase)");
    }

    @Override
    public void interceptSetupSpecMethod(final IMethodInvocation invocation) throws Throwable {
        junit.beforeAll(context);
        spockLifecycle("setupSpec");
        // no real method call here
        invocation.proceed();
    }

    @Override
    public void interceptInitializerMethod(final IMethodInvocation invocation) throws Throwable {
        // note that shared init phase is ignored
        spockLifecycle("initialization");
        // important to call before because otherwise @RegisterExtension would not work (fields are null)
        invocation.proceed();

        final Object instance = Preconditions.notNull(invocation.getInstance(), "No spec instance");
        junit.instancePostProcessors(context, instance);
        // find method (and parameters) extensions
        final MethodContext methodContext = createMethodContext(invocation.getFeature(), instance);

        if (ConditionEvaluator.skip(invocation.getFeature(), methodContext)) {
            // skip execution based on junit's ExecutionCondition (possibly applied on method)
            return;
        }

        // context stored by instance for simplicity (all later hooks would easily resolve it)
        methods.put(instance, methodContext);
    }

    @Override
    public void interceptSetupMethod(final IMethodInvocation invocation) throws Throwable {
        junit.beforeEach(getMethodContext(invocation));
        spockLifecycle("setup");
        // no real method call here
        invocation.proceed();
    }

    @Override
    public void interceptFeatureMethod(final IMethodInvocation invocation) throws Throwable {
        spockLifecycle("'" + invocation.getFeature().getDisplayName() + "' execution");
        final MethodContext mcontext = getMethodContext(invocation);
        try {
            junit.beforeTestExecution(mcontext);

            injectArguments(invocation, mcontext);
            // org.junit.jupiter.engine.descriptor.TestMethodTestDescriptor.invokeTestMethod
            mcontext.getCollector().execute(() -> {
                try {
                    invocation.proceed();
                } catch (Throwable throwable) {
                    UnrecoverableExceptions.rethrowIfUnrecoverable(throwable);
                    // note: will also handle assertion errors! jupiter works the same way
                    junit.handleTestException(mcontext, throwable);
                }
            });
        } finally {
            junit.afterTestExecution(mcontext);
        }
    }

    @Override
    public void interceptCleanupMethod(final IMethodInvocation invocation) throws Throwable {
        spockLifecycle("cleanup");

        final MethodContext mcontext = getMethodContext(invocation);
        // no real method call here
        invocation.proceed();
        junit.afterEach(mcontext);

        // feature execution or single iteration done
        methods.remove(invocation.getInstance());

        // pre destroy callbacks support (could be registered on method level)
        junit.instancePreDestroy(mcontext);

        // process closable values in storage
        mcontext.close();
    }

    @Override
    public void interceptCleanupSpecMethod(final IMethodInvocation invocation) throws Throwable {
        spockLifecycle("cleanupSpec");
        // no real method call here
        invocation.proceed();
        junit.afterAll(context);

        // process closable values in storage
        context.close();
    }

    private MethodContext createMethodContext(final FeatureInfo featureInfo, final Object instance) {
        final Method method = featureInfo.getFeatureMethod().getReflection();
        final ExtensionRegistry methodRegistry = ExtensionUtils.createMethodRegistry(context.getRegistry(), method);
        ExtensionUtils.registerExtensionsFromExecutableParameters(methodRegistry, method);
        return new MethodContext(context, methodRegistry, featureInfo, instance);
    }

    private void spockLifecycle(final String name) {
        logger.debug(() -> "Spock " + context.getSpec().getReflection().getSimpleName() + "." + name);
    }

    private void injectArguments(final IMethodInvocation invocation, final AbstractContext context) {
        final Method method = invocation.getMethod().getReflection();
        final Object[] arguments = invocation.getArguments();
        if (method == null || arguments.length == 0) {
            // null is a case when there is no real method for setup/cleanup (nothing to inject then)
            return;
        }
        final Parameter[] parameters = method.getParameters();
        for (int i = 0; i < arguments.length; i++) {
            // look only arguments not processed by spock (e.g data providers or other extensions)
            if (arguments[i] == MethodInfo.MISSING_ARGUMENT) {
                // based on org.junit.jupiter.engine.execution.ExecutableInvoker.resolveParameter
                final Parameter param = parameters[i];
                final ParameterContext parameterContext = new DefaultParameterContext(
                        param, i, Optional.ofNullable(invocation.getTarget()));
                // if parameter provider would not be found, value would remain as MISSING_ARGUMENT
                // (assuming native spock extension would process this parameter)
                arguments[i] = ExtensionUtils.resolveParameter(parameterContext, method, context);
            }
        }
    }

    private void handleFixtureMethodException(final IMethodInvocation invocation,
                                              final AbstractContext context,
                                              final Throwable throwable) {
        switch (invocation.getMethod().getKind()) {
            case SETUP_SPEC:
                junit.handleSetupSpecMethodException(context, throwable);
                break;
            case SETUP:
                junit.handleSetupMethodException(context, throwable);
                break;
            case CLEANUP:
                junit.handleCleanupMethodException(context, throwable);
                break;
            case CLEANUP_SPEC:
                junit.handleCleanupSpecMethodException(context, throwable);
                break;
            default:
                throw new IllegalStateException("Unexpected method kind: " + invocation.getMethod().getKind());
        }
    }
}
