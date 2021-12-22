package ru.vyarus.spock.jupiter.interceptor;

import org.junit.jupiter.api.extension.AfterAllCallback;
import org.junit.jupiter.api.extension.AfterEachCallback;
import org.junit.jupiter.api.extension.AfterTestExecutionCallback;
import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.BeforeTestExecutionCallback;
import org.junit.platform.commons.logging.Logger;
import org.junit.platform.commons.logging.LoggerFactory;
import org.junit.platform.engine.support.hierarchical.OpenTest4JAwareThrowableCollector;
import org.junit.platform.engine.support.hierarchical.ThrowableCollector;
import org.spockframework.runtime.extension.AbstractMethodInterceptor;
import org.spockframework.runtime.extension.IMethodInvocation;
import ru.vyarus.spock.jupiter.engine.context.ClassContext;
import ru.vyarus.spock.jupiter.engine.context.DefaultTestInstances;
import ru.vyarus.spock.jupiter.engine.context.MethodContext;

import java.lang.reflect.AnnotatedElement;
import java.util.List;
import java.util.Map;

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


    public ExtensionLifecycleMerger(final ClassContext context,
                                    final Map<AnnotatedElement, MethodContext> methods) {
        this.context = context;
        this.methods = methods;
    }

    @Override
    public void interceptSetupSpecMethod(IMethodInvocation invocation) throws Throwable {
        context.setInstances(DefaultTestInstances.of(invocation.getInstance()));

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
        invocation.proceed();
    }

    @Override
    public void interceptSetupMethod(IMethodInvocation invocation) throws Throwable {
        // org.junit.jupiter.engine.descriptor.TestMethodTestDescriptor.invokeBeforeEachCallbacks
        final MethodContext mcontext = methods.get(invocation.getMethod().getReflection());
        final List<BeforeEachCallback> exts = context.getRegistry().getExtensions(BeforeEachCallback.class);
        if (!exts.isEmpty()) {
            logger.debug(() -> "Junit " + context.getSpec().getReflection().getSimpleName() + ".BeforeEachCallback: " + exts);
        }
        for (BeforeEachCallback callback : exts) {
            collector.execute(() -> callback.beforeEach(mcontext));
            if (collector.isNotEmpty()) {
                break;
            }
        }
        logger.debug(() -> "Spock  " + context.getSpec().getReflection().getSimpleName() + ".setup");
        invocation.proceed();
    }

    @Override
    public void interceptFeatureMethod(IMethodInvocation invocation) throws Throwable {
        logger.debug(() -> "Spock " + context.getSpec().getReflection().getSimpleName()
                + ".'" + invocation.getFeature().getDisplayName() + "' execution");
        final MethodContext mcontext = methods.get(invocation.getMethod().getReflection());
        try {
            // org.junit.jupiter.engine.descriptor.TestMethodTestDescriptor.invokeBeforeTestExecutionCallbacks()
            final List<BeforeTestExecutionCallback> exts = context.getRegistry()
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

            invocation.proceed();
        } finally {
            // org.junit.jupiter.engine.descriptor.TestMethodTestDescriptor.invokeAfterTestExecutionCallbacks()
            final List<AfterTestExecutionCallback> exts = context.getRegistry()
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
        invocation.proceed();
        final MethodContext mcontext = methods.get(invocation.getMethod().getReflection());
        final List<AfterEachCallback> exts = context.getRegistry().getReversedExtensions(AfterEachCallback.class);
        if (!exts.isEmpty()) {
            logger.debug(() -> "Junit " + context.getSpec().getReflection().getSimpleName()
                    + ".AfterEachCallback: " + exts);
        }
        exts.forEach(callback -> collector.execute(() -> callback.afterEach(mcontext)));
    }

    @Override
    public void interceptCleanupSpecMethod(IMethodInvocation invocation) throws Throwable {
        logger.debug(() -> "Spock " + context.getSpec().getReflection().getSimpleName() + ".cleanupSpec");
        // org.junit.jupiter.engine.descriptor.ClassBasedTestDescriptor.invokeAfterAllCallbacks
        invocation.proceed();
        final List<AfterAllCallback> exts = context.getRegistry().getReversedExtensions(AfterAllCallback.class);
        if (!exts.isEmpty()) {
            logger.debug(() -> "Junit " + context.getSpec().getReflection().getSimpleName()
                    + ".AfterAllCallback: " + exts);
        }
        exts.forEach(extension -> collector.execute(() -> extension.afterAll(context)));

        // flushing context instance
        context.setInstances(null);
    }
}
