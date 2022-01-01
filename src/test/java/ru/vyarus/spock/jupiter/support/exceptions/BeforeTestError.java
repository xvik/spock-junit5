package ru.vyarus.spock.jupiter.support.exceptions;

import org.junit.jupiter.api.extension.BeforeTestExecutionCallback;
import org.junit.jupiter.api.extension.ExtensionContext;

/**
 * @author Vyacheslav Rusakov
 * @since 01.01.2022
 */
public class BeforeTestError implements BeforeTestExecutionCallback {

    @Override
    public void beforeTestExecution(ExtensionContext context) throws Exception {
        throw new IllegalStateException("problem");
    }
}
