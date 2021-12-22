package ru.vyarus.spock.jupiter.support;

import org.junit.jupiter.api.extension.AfterAllCallback;
import org.junit.jupiter.api.extension.AfterEachCallback;
import org.junit.jupiter.api.extension.AfterTestExecutionCallback;
import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.BeforeTestExecutionCallback;
import org.junit.jupiter.api.extension.ExtensionContext;

/**
 * @author Vyacheslav Rusakov
 * @since 26.11.2021
 */
public class LifecycleExtension2 implements BeforeAllCallback,
        BeforeEachCallback,
        BeforeTestExecutionCallback,
        AfterTestExecutionCallback,
        AfterEachCallback,
        AfterAllCallback {

    @Override
    public void afterAll(ExtensionContext context) throws Exception {
        ActionHolder.add("AfterAllCallback-2");
    }

    @Override
    public void afterEach(ExtensionContext context) throws Exception {
        ActionHolder.add("AfterEachCallback-2");
    }

    @Override
    public void afterTestExecution(ExtensionContext context) throws Exception {
        ActionHolder.add("AfterTestExecutionCallback-2");
    }

    @Override
    public void beforeAll(ExtensionContext context) throws Exception {
        ActionHolder.add("BeforeAllCallback-2");
    }

    @Override
    public void beforeEach(ExtensionContext context) throws Exception {
        ActionHolder.add("BeforeEachCallback-2");
    }

    @Override
    public void beforeTestExecution(ExtensionContext context) throws Exception {
        ActionHolder.add("BeforeTestExecutionCallback-2");
    }
}
