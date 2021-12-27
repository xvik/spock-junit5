package ru.vyarus.spock.jupiter.interceptor;

import org.junit.jupiter.api.extension.AfterAllCallback;
import org.junit.jupiter.api.extension.AfterEachCallback;
import org.junit.jupiter.api.extension.AfterTestExecutionCallback;
import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.BeforeTestExecutionCallback;
import org.junit.jupiter.api.extension.Extension;
import org.junit.jupiter.api.extension.TestInstancePostProcessor;
import org.junit.jupiter.api.extension.TestInstancePreDestroyCallback;
import org.junit.jupiter.api.function.Executable;
import org.junit.platform.commons.logging.Logger;
import org.junit.platform.commons.logging.LoggerFactory;
import org.junit.platform.commons.util.ExceptionUtils;
import org.junit.platform.engine.support.hierarchical.ThrowableCollector;
import ru.vyarus.spock.jupiter.engine.context.AbstractContext;
import ru.vyarus.spock.jupiter.engine.context.ClassContext;
import ru.vyarus.spock.jupiter.engine.context.MethodContext;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Vyacheslav Rusakov
 * @since 28.12.2021
 */
public class JunitApiExecutor {
    private final Logger logger = LoggerFactory.getLogger(JunitApiExecutor.class);

    // org.junit.jupiter.engine.descriptor.ClassBasedTestDescriptor.invokeBeforeAllCallbacks
    public void beforeAll(final ClassContext context) {
        for (BeforeAllCallback callback : getExtensions(context, BeforeAllCallback.class)) {
            context.getCollector().execute(() -> callback.beforeAll(context));
            if (context.getCollector().isNotEmpty()) {
                break;
            }
        }
        context.getCollector().assertEmpty();
    }

    // org.junit.jupiter.engine.descriptor.ClassBasedTestDescriptor.invokeTestInstancePostProcessors
    public void instancePostProcessors(ClassContext context, Object instance) {
        context.getCollector().execute(() ->
                getExtensions(context, TestInstancePostProcessor.class).forEach(
                        extension -> executeAndMaskThrowable(() ->
                                extension.postProcessTestInstance(instance, context))));
        context.getCollector().assertEmpty();

    }

    // org.junit.jupiter.engine.descriptor.TestMethodTestDescriptor.invokeBeforeEachCallbacks
    public void beforeEach(final MethodContext context) {
        final ThrowableCollector collector = context.getCollector();
        for (BeforeEachCallback callback : getExtensions(context, BeforeEachCallback.class)) {
            collector.execute(() -> callback.beforeEach(context));
            if (collector.isNotEmpty()) {
                break;
            }
        }
        collector.assertEmpty();
    }

    // org.junit.jupiter.engine.descriptor.TestMethodTestDescriptor.invokeBeforeTestExecutionCallbacks()
    public void beforeTestExecution(final MethodContext context) {
        final ThrowableCollector collector = context.getCollector();
        for (BeforeTestExecutionCallback callback : getExtensions(context, BeforeTestExecutionCallback.class)) {
            collector.execute(() -> callback.beforeTestExecution(context));
            if (collector.isNotEmpty()) {
                break;
            }
        }
        collector.assertEmpty();
    }

    // org.junit.jupiter.engine.descriptor.TestMethodTestDescriptor.invokeAfterTestExecutionCallbacks()
    public void afterTestExecution(final MethodContext context) {
        final ThrowableCollector collector = context.getCollector();
        getReversedExtensions(context, AfterTestExecutionCallback.class).forEach(
                callback -> collector.execute(() -> callback.afterTestExecution(context)));
        collector.assertEmpty();
    }

    // org.junit.jupiter.engine.descriptor.TestMethodTestDescriptor.invokeAfterEachCallbacks
    public void afterEach(final MethodContext context) {
        final ThrowableCollector collector = context.getCollector();
        getReversedExtensions(context, AfterEachCallback.class).forEach(
                callback -> collector.execute(() -> callback.afterEach(context)));
        collector.assertEmpty();
    }

    // org.junit.jupiter.engine.descriptor.TestMethodTestDescriptor.invokeTestInstancePreDestroyCallbacks
    public void instancePreDestroy(final MethodContext context) {
        final ThrowableCollector collector = context.getCollector();
        getReversedExtensions(context, TestInstancePreDestroyCallback.class).forEach(callback ->
                collector.execute(() -> callback.preDestroyTestInstance(context)));
        collector.assertEmpty();
    }

    // org.junit.jupiter.engine.descriptor.ClassBasedTestDescriptor.invokeAfterAllCallbacks
    public void afterAll(final ClassContext context) {
        final ThrowableCollector collector = context.getCollector();
        getReversedExtensions(context, AfterAllCallback.class).forEach(
                extension -> collector.execute(() -> extension.afterAll(context)));
        collector.assertEmpty();
    }

    private <T extends Extension> List<T> getExtensions(final AbstractContext context, final Class<T> type) {
        return getExtensions(context, type, false);
    }

    private <T extends Extension> List<T> getReversedExtensions(final AbstractContext context, final Class<T> type) {
        return getExtensions(context, type, true);
    }

    private <T extends Extension> List<T> getExtensions(final AbstractContext context,
                                                        final Class<T> type,
                                                        final boolean reversed) {
        final List<T> exts = reversed ? context.getRegistry().getReversedExtensions(type)
                : context.getRegistry().getExtensions(type);
        if (!exts.isEmpty()) {
            logger.debug(() -> "Junit " + context.getSpec().getReflection().getSimpleName() + "."
                    + type.getSimpleName() + ": "
                    + exts.stream()
                    .map(t -> t.getClass().getSimpleName())
                    .collect(Collectors.joining(", ")));
        }
        return exts;
    }

    private void executeAndMaskThrowable(Executable executable) {
        try {
            executable.execute();
        } catch (Throwable throwable) {
            ExceptionUtils.throwAsUncheckedException(throwable);
        }
    }
}
