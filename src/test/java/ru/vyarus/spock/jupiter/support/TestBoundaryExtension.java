package ru.vyarus.spock.jupiter.support;

import org.junit.jupiter.api.extension.AfterTestExecutionCallback;
import org.junit.jupiter.api.extension.BeforeTestExecutionCallback;
import org.junit.jupiter.api.extension.ExtensionContext;

/**
 * @author Vyacheslav Rusakov
 * @since 27.12.2021
 */
public class TestBoundaryExtension implements BeforeTestExecutionCallback, AfterTestExecutionCallback {

    @Override
    public void afterTestExecution(ExtensionContext context) throws Exception {
        ActionHolder.add("AfterTestExecutionCallback");
    }

    @Override
    public void beforeTestExecution(ExtensionContext context) throws Exception {
        ActionHolder.add("BeforeTestExecutionCallback");
    }
}
