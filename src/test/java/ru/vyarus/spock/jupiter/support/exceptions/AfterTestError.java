package ru.vyarus.spock.jupiter.support.exceptions;

import org.junit.jupiter.api.extension.AfterTestExecutionCallback;
import org.junit.jupiter.api.extension.ExtensionContext;

/**
 * @author Vyacheslav Rusakov
 * @since 01.01.2022
 */
public class AfterTestError implements AfterTestExecutionCallback {

    @Override
    public void afterTestExecution(ExtensionContext context) throws Exception {
        throw new IllegalStateException("problem");
    }
}
