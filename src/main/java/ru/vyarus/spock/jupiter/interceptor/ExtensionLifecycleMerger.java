package ru.vyarus.spock.jupiter.interceptor;

import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.extension.AfterAllCallback;
import org.junit.jupiter.api.extension.AfterEachCallback;
import org.junit.jupiter.api.extension.AfterTestExecutionCallback;
import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.BeforeTestExecutionCallback;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.api.extension.TestInstancePostProcessor;
import org.junit.jupiter.api.extension.TestInstancePreDestroyCallback;
import org.junit.jupiter.api.function.Executable;
import org.junit.platform.commons.logging.Logger;
import org.junit.platform.commons.logging.LoggerFactory;
import org.junit.platform.commons.util.ExceptionUtils;
import org.junit.platform.commons.util.Preconditions;
import org.junit.platform.engine.support.hierarchical.OpenTest4JAwareThrowableCollector;
import org.junit.platform.engine.support.hierarchical.ThrowableCollector;
import org.spockframework.runtime.extension.AbstractMethodInterceptor;
import org.spockframework.runtime.extension.IMethodInterceptor;
import org.spockframework.runtime.extension.IMethodInvocation;
import org.spockframework.runtime.model.MethodInfo;
import ru.vyarus.spock.jupiter.engine.ExtensionRegistry;
import ru.vyarus.spock.jupiter.engine.ExtensionUtils;
import ru.vyarus.spock.jupiter.engine.context.AbstractContext;
import ru.vyarus.spock.jupiter.engine.context.ClassContext;
import ru.vyarus.spock.jupiter.engine.context.DefaultParameterContext;
import ru.vyarus.spock.jupiter.engine.context.MethodContext;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Implementation based on {@code org.junit.jupiter.engine.descriptor.ClassBasedTestDescriptor} and
 * {@code org.junit.jupiter.engine.descriptor.TestMethodTestDescriptor} from junit-jupiter-engine.
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
    private final ThrowableCollector collector = new OpenTest4JAwareThrowableCollector();

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
            invocation.proceed();
        };
    }

    public IMethodInterceptor getFixtureMethodsInterceptor() {
        return fixtureMethodsInterceptor;
    }

    @Override
    public void interceptSetupSpecMethod(IMethodInvocation invocation) throws Throwable {
        // org.junit.jupiter.engine.descriptor.ClassBasedTestDescriptor.invokeBeforeAllCallbacks
        final List<BeforeAllCallback> exts = context.getRegistry().getExtensions(BeforeAllCallback.class);
        if (!exts.isEmpty()) {
            logger.debug(() -> "Junit " + context.getSpec().getReflection().getSimpleName() + ".BeforeAllCallback: " + exts);
        }
        for (BeforeAllCallback callback : exts) {
            collector.execute(() -> callback.beforeAll(context));
            if (collector.isNotEmpty()) {
                break;
            }
        }
        collector.assertEmpty();
        logger.debug(() -> "Spock " + context.getSpec().getReflection().getSimpleName() + ".setupSpec");
        // no real method call here
        invocation.proceed();
    }

    @Override
    public void interceptInitializerMethod(IMethodInvocation invocation) throws Throwable {
        logger.debug(() -> "Spock " + context.getSpec().getReflection().getSimpleName() + ".initialization");
        invocation.proceed();

        // post processors
        // org.junit.jupiter.engine.descriptor.ClassBasedTestDescriptor.invokeTestInstancePostProcessors
        final Object instance = Preconditions.notNull(invocation.getInstance(), "No spec instance");
        collector.execute(() ->
                context.getRegistry().stream(TestInstancePostProcessor.class).forEach(
                        extension -> executeAndMaskThrowable(() ->
                                extension.postProcessTestInstance(instance, context))));
        collector.assertEmpty();

        // in case of data iterations this analysis would be performed for each iteration
        // this is required because extension must be re-created (and field-based extensions change instance)
        final Method method = invocation.getFeature().getFeatureMethod().getReflection();
        final ExtensionRegistry methodRegistry = ExtensionUtils.createMethodRegistry(context.getRegistry(), method);
        ExtensionUtils.registerExtensionsFromExecutableParameters(methodRegistry, method);
        // register non-static @RegisterExtension annotated extensions
        ExtensionUtils.registerExtensionsFromFields(methodRegistry, context.getRequiredTestClass(), instance);
        final MethodContext methodContext =
                new MethodContext(context, methodRegistry, invocation.getFeature(), instance);
        methods.put(instance, methodContext);
        // todo instance post processor
    }

    @Override
    public void interceptSetupMethod(IMethodInvocation invocation) throws Throwable {
        // org.junit.jupiter.engine.descriptor.TestMethodTestDescriptor.invokeBeforeEachCallbacks
        final MethodContext mcontext = getMethodContext(invocation);
        final List<BeforeEachCallback> exts = mcontext.getRegistry().getExtensions(BeforeEachCallback.class);
        if (!exts.isEmpty()) {
            logger.debug(() -> "Junit " + context.getSpec().getReflection().getSimpleName() + ".BeforeEachCallback: " + exts);
        }
        for (BeforeEachCallback callback : exts) {
            collector.execute(() -> callback.beforeEach(mcontext));
            if (collector.isNotEmpty()) {
                break;
            }
        }
        collector.assertEmpty();
        logger.debug(() -> "Spock " + context.getSpec().getReflection().getSimpleName() + ".setup");
        // no real method call here
        invocation.proceed();
    }

    @Override
    public void interceptFeatureMethod(IMethodInvocation invocation) throws Throwable {
        logger.debug(() -> "Spock " + context.getSpec().getReflection().getSimpleName()
                + ".'" + invocation.getFeature().getDisplayName() + "' execution");
        final MethodContext mcontext = getMethodContext(invocation);
        try {
            // org.junit.jupiter.engine.descriptor.TestMethodTestDescriptor.invokeBeforeTestExecutionCallbacks()
            final List<BeforeTestExecutionCallback> exts = mcontext.getRegistry()
                    .getExtensions(BeforeTestExecutionCallback.class);
            if (!exts.isEmpty()) {
                logger.debug(() -> "Junit " + context.getSpec().getReflection().getSimpleName()
                        + ".BeforeTestExecutionCallback: " + exts);
            }
            for (BeforeTestExecutionCallback callback : exts) {
                collector.execute(() -> callback.beforeTestExecution(mcontext));
                if (collector.isNotEmpty()) {
                    break;
                }
            }
            collector.assertEmpty();

            injectArguments(invocation, mcontext);
            invocation.proceed();
        } finally {
            // org.junit.jupiter.engine.descriptor.TestMethodTestDescriptor.invokeAfterTestExecutionCallbacks()
            final List<AfterTestExecutionCallback> exts = mcontext.getRegistry()
                    .getReversedExtensions(AfterTestExecutionCallback.class);
            if (!exts.isEmpty()) {
                logger.debug(() -> "Junit " + context.getSpec().getReflection().getSimpleName()
                        + ".AfterTestExecutionCallback: " + exts);
            }
            exts.forEach(callback -> collector.execute(() -> callback.afterTestExecution(mcontext)));
            collector.assertEmpty();
        }
    }

    @Override
    public void interceptCleanupMethod(IMethodInvocation invocation) throws Throwable {
        logger.debug(() -> "Spock " + context.getSpec().getReflection().getSimpleName() + ".cleanup");
        // org.junit.jupiter.engine.descriptor.TestMethodTestDescriptor.invokeAfterEachCallbacks
        final MethodContext mcontext = getMethodContext(invocation);
        // no real method call here
        invocation.proceed();
        final List<AfterEachCallback> exts = mcontext.getRegistry().getReversedExtensions(AfterEachCallback.class);
        if (!exts.isEmpty()) {
            logger.debug(() -> "Junit " + context.getSpec().getReflection().getSimpleName()
                    + ".AfterEachCallback: " + exts);
        }
        exts.forEach(callback -> collector.execute(() -> callback.afterEach(mcontext)));
        collector.assertEmpty();
        // feature execution or single iteration done
        methods.remove(invocation.getInstance());

        // pre destroy callbacks support (could be registered on method level)
        mcontext.getRegistry().getReversedExtensions(TestInstancePreDestroyCallback.class).forEach(callback ->
                collector.execute(() -> callback.preDestroyTestInstance(mcontext)));
        collector.assertEmpty();
    }

    @Override
    public void interceptCleanupSpecMethod(IMethodInvocation invocation) throws Throwable {
        logger.debug(() -> "Spock " + context.getSpec().getReflection().getSimpleName() + ".cleanupSpec");
        // org.junit.jupiter.engine.descriptor.ClassBasedTestDescriptor.invokeAfterAllCallbacks
        // no real method call here
        invocation.proceed();
        final List<AfterAllCallback> exts = context.getRegistry().getReversedExtensions(AfterAllCallback.class);
        if (!exts.isEmpty()) {
            logger.debug(() -> "Junit " + context.getSpec().getReflection().getSimpleName()
                    + ".AfterAllCallback: " + exts);
        }
        exts.forEach(extension -> collector.execute(() -> extension.afterAll(context)));
        collector.assertEmpty();
    }

    @NotNull
    private MethodContext getMethodContext(final IMethodInvocation invocation) {
        return Preconditions.notNull(methods.get(invocation.getInstance()), () -> "Method context not found for '"
                + invocation.getFeature().getDisplayName() + "' feature");
    }

    private void executeAndMaskThrowable(Executable executable) {
        try {
            executable.execute();
        } catch (Throwable throwable) {
            ExceptionUtils.throwAsUncheckedException(throwable);
        }
    }

    private void injectArguments(IMethodInvocation invocation, AbstractContext context) {
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
}
