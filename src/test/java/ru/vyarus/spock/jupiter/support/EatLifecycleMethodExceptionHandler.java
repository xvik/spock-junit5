package ru.vyarus.spock.jupiter.support;

import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.LifecycleMethodExecutionExceptionHandler;

/**
 * @author Vyacheslav Rusakov
 * @since 03.02.2026
 */
public class EatLifecycleMethodExceptionHandler implements LifecycleMethodExecutionExceptionHandler {

    @Override
    public void handleBeforeAllMethodExecutionException(ExtensionContext context, Throwable throwable) throws Throwable {
        ActionHolder.add("BeforeAllEatExceptionHandler " + throwable.getMessage());
    }

    @Override
    public void handleBeforeEachMethodExecutionException(ExtensionContext context, Throwable throwable) throws Throwable {
        ActionHolder.add("BeforeEachEatExceptionHandler " + throwable.getMessage());
    }

    @Override
    public void handleAfterEachMethodExecutionException(ExtensionContext context, Throwable throwable) throws Throwable {
        ActionHolder.add("AfterEachEatExceptionHandler " + throwable.getMessage());
    }

    @Override
    public void handleAfterAllMethodExecutionException(ExtensionContext context, Throwable throwable) throws Throwable {
        ActionHolder.add("AfterAllEatExceptionHandler " + throwable.getMessage());
    }
}
