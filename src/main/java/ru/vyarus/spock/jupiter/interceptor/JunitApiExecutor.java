package ru.vyarus.spock.jupiter.interceptor;

import org.junit.jupiter.api.extension.AfterAllCallback;
import org.junit.jupiter.api.extension.AfterEachCallback;
import org.junit.jupiter.api.extension.AfterTestExecutionCallback;
import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.BeforeTestExecutionCallback;
import org.junit.jupiter.api.extension.Extension;
import org.junit.jupiter.api.extension.TestExecutionExceptionHandler;
import org.junit.jupiter.api.extension.TestInstancePostProcessor;
import org.junit.jupiter.api.extension.TestInstancePreDestroyCallback;
import org.junit.jupiter.api.function.Executable;
import org.junit.platform.commons.logging.Logger;
import org.junit.platform.commons.logging.LoggerFactory;
import org.junit.platform.commons.util.ExceptionUtils;
import org.junit.platform.commons.util.UnrecoverableExceptions;
import org.junit.platform.engine.support.hierarchical.ThrowableCollector;
import ru.vyarus.spock.jupiter.engine.context.AbstractContext;
import ru.vyarus.spock.jupiter.engine.context.ClassContext;
import ru.vyarus.spock.jupiter.engine.context.MethodContext;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Executes registered extensions.
 * <p>
 * Mostly a copy of {@code ClassBasedTestDescriptor} and {@code TestMethodTestDescriptor} logic.
 * <p>
 * Note that in jupiter all such logic is aggregated in descriptors because they have to do all the work of methods
 * execution. But spock do most of it already and that's why descriptors concept wasn't used at all. Essentially,
 * this class contains all required code (plus {@link ru.vyarus.spock.jupiter.engine.ExtensionUtils}).
 * <p>
 * Contexts are more equal to jupiter analog, but in context of spock much less context types is required (and
 * overall amount of possible execution scenarios is less than in jupiter because there is no templates and
 * nested tests).
 * <p>
 * {@link ThrowableCollector} concept is very limited comparing to jupiter: there it used for everything, because
 * descriptors control all flow, but here, collectors used only for exceptions detection in extensions
 * (and to make callbacks processing logic more equal to the original source). Also, that's why exception handler
 * extensions are not supported (it is possible, but not so important).
 *
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
    public void instancePostProcessors(final ClassContext context, final Object instance) {
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

    // org.junit.jupiter.engine.descriptor.TestMethodTestDescriptor.invokeTestExecutionExceptionHandlers
    public void handleTestException(final MethodContext context, final Throwable error) {
        processTestException(context, getReversedExtensions(context, TestExecutionExceptionHandler.class), error);
    }

    private <T extends Extension> List<T> getReversedExtensions(final AbstractContext context, final Class<T> type) {
        return getExtensions(context, type, true);
    }

    private <T extends Extension> List<T> getExtensions(final AbstractContext context, final Class<T> type) {
        return getExtensions(context, type, false);
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

    private void executeAndMaskThrowable(final Executable executable) {
        try {
            executable.execute();
        } catch (Throwable throwable) {
            ExceptionUtils.throwAsUncheckedException(throwable);
        }
    }

    // org.junit.jupiter.engine.descriptor.JupiterTestDescriptor.invokeExecutionExceptionHandlers
    private void processTestException(final MethodContext context,
                                      final List<TestExecutionExceptionHandler> handlers,
                                      final Throwable error) {
        // No handlers left?
        if (handlers.isEmpty()) {
            ExceptionUtils.throwAsUncheckedException(error);
        }

        try {
            // Invoke next available handler
            handlers.remove(0).handleTestExecutionException(context, error);
        } catch (Throwable handledThrowable) {
            UnrecoverableExceptions.rethrowIfUnrecoverable(handledThrowable);
            processTestException(context, handlers, handledThrowable);
        }
    }
}
