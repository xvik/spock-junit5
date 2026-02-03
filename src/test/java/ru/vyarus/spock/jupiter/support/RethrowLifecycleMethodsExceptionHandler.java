package ru.vyarus.spock.jupiter.support;

import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.LifecycleMethodExecutionExceptionHandler;

/**
 * @author Vyacheslav Rusakov
 * @since 03.02.2026
 */
public class RethrowLifecycleMethodsExceptionHandler implements LifecycleMethodExecutionExceptionHandler {

    @Override
    public void handleBeforeAllMethodExecutionException(ExtensionContext context, Throwable throwable) throws Throwable {
        ActionHolder.add("BeforeAllExceptionHandler " + throwable.getMessage());
        throw throwable;
    }

    @Override
    public void handleBeforeEachMethodExecutionException(ExtensionContext context, Throwable throwable) throws Throwable {
        ActionHolder.add("BeforeEachExceptionHandler " + throwable.getMessage());
        throw throwable;
    }

    @Override
    public void handleAfterEachMethodExecutionException(ExtensionContext context, Throwable throwable) throws Throwable {
        ActionHolder.add("AfterEachExceptionHandler " + throwable.getMessage());
        throw throwable;
    }

    @Override
    public void handleAfterAllMethodExecutionException(ExtensionContext context, Throwable throwable) throws Throwable {
        ActionHolder.add("AfterAllExceptionHandler " + throwable.getMessage());
        throw throwable;
    }
}
