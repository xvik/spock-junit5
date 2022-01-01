package ru.vyarus.spock.jupiter.support;

import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.TestExecutionExceptionHandler;

/**
 * @author Vyacheslav Rusakov
 * @since 02.01.2022
 */
public class SwallowExceptionHandler implements TestExecutionExceptionHandler {

    @Override
    public void handleTestExecutionException(ExtensionContext context, Throwable throwable) throws Throwable {
        ActionHolder.add("SwallowExceptionHandler " + throwable.getMessage());
    }
}
