package ru.vyarus.spock.jupiter.interceptor;

import org.junit.jupiter.api.extension.AfterAllCallback;
import org.junit.jupiter.api.extension.AfterEachCallback;
import org.junit.jupiter.api.extension.AfterTestExecutionCallback;
import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.BeforeTestExecutionCallback;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.platform.commons.logging.Logger;
import org.junit.platform.commons.logging.LoggerFactory;
import org.junit.platform.engine.support.hierarchical.OpenTest4JAwareThrowableCollector;
import org.junit.platform.engine.support.hierarchical.ThrowableCollector;
import org.spockframework.runtime.extension.AbstractMethodInterceptor;
import org.spockframework.runtime.extension.IMethodInterceptor;
import org.spockframework.runtime.extension.IMethodInvocation;
import org.spockframework.runtime.model.MethodInfo;
import ru.vyarus.spock.jupiter.engine.ExtensionUtils;
import ru.vyarus.spock.jupiter.engine.context.AbstractContext;
import ru.vyarus.spock.jupiter.engine.context.ClassContext;
import ru.vyarus.spock.jupiter.engine.context.DefaultParameterContext;
import ru.vyarus.spock.jupiter.engine.context.DefaultTestInstances;
import ru.vyarus.spock.jupiter.engine.context.MethodContext;

import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Implementation based on {@code org.junit.jupiter.engine.descriptor.ClassBasedTestDescriptor} and
 * {@code org.junit.jupiter.engine.descriptor.TestMethodTestDescriptor} from junit-jupiter-engine.
 *
 * @author Vyacheslav Rusakov
 * @since 30.11.2021
 */
public class ExtensionLifecycleMerger extends AbstractMethodInterceptor {

    private final Logger logger = LoggerFactory.getLogger(ExtensionLifecycleMerger.class);

    private final ClassContext context;
    private final Map<AnnotatedElement, MethodContext> methods;
    private final ThrowableCollector collector = new OpenTest4JAwareThrowableCollector();

    private final IMethodInterceptor fixtureMethodsInterceptor;


    public ExtensionLifecycleMerger(final ClassContext context,
                                    final Map<AnnotatedElement, MethodContext> methods) {
        this.context = context;
        this.methods = methods;

        fixtureMethodsInterceptor = invocation -> {
            AbstractContext ctx = context;
            // setup/cleanup methods must use method context
            if (invocation.getFeature() != null) {
                ctx = methods.get(invocation.getFeature().getFeatureMethod().getReflection());
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
        logger.debug(() -> "Spock " + context.getSpec().getReflection().getSimpleName() + ".setupSpec");
        // no real method call here
        invocation.proceed();
    }

    @Override
    public void interceptSetupMethod(IMethodInvocation invocation) throws Throwable {
        // org.junit.jupiter.engine.descriptor.TestMethodTestDescriptor.invokeBeforeEachCallbacks
        final MethodContext mcontext = methods.get(invocation.getFeature().getFeatureMethod().getReflection());
        mcontext.setInstances(DefaultTestInstances.of(invocation.getInstance()));
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
        logger.debug(() -> "Spock " + context.getSpec().getReflection().getSimpleName() + ".setup");
        // no real method call here
        invocation.proceed();
    }

    @Override
    public void interceptFeatureMethod(IMethodInvocation invocation) throws Throwable {
        logger.debug(() -> "Spock " + context.getSpec().getReflection().getSimpleName()
                + ".'" + invocation.getFeature().getDisplayName() + "' execution");
        final MethodContext mcontext = methods.get(invocation.getFeature().getFeatureMethod().getReflection());
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
        }
    }

    @Override
    public void interceptCleanupMethod(IMethodInvocation invocation) throws Throwable {
        logger.debug(() -> "Spock " + context.getSpec().getReflection().getSimpleName() + ".cleanup");
        // org.junit.jupiter.engine.descriptor.TestMethodTestDescriptor.invokeAfterEachCallbacks
        final MethodContext mcontext = methods.get(invocation.getFeature().getFeatureMethod().getReflection());
        // no real method call here
        invocation.proceed();
        final List<AfterEachCallback> exts = mcontext.getRegistry().getReversedExtensions(AfterEachCallback.class);
        if (!exts.isEmpty()) {
            logger.debug(() -> "Junit " + context.getSpec().getReflection().getSimpleName()
                    + ".AfterEachCallback: " + exts);
        }
        exts.forEach(callback -> collector.execute(() -> callback.afterEach(mcontext)));
        // flushing context instance
        mcontext.setInstances(null);
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
