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
 * @since 26.12.2021
 */
public abstract class LifecycleExtensionN implements BeforeAllCallback,
        BeforeEachCallback,
        BeforeTestExecutionCallback,
        AfterTestExecutionCallback,
        AfterEachCallback,
        AfterAllCallback {

    private final int n;

    public LifecycleExtensionN(int n) {
        this.n = n;
    }

    @Override
    public void afterAll(ExtensionContext context) throws Exception {
        ActionHolder.add("AfterAllCallback-" + n);
    }

    @Override
    public void afterEach(ExtensionContext context) throws Exception {
        ActionHolder.add("AfterEachCallback-" + n);
    }

    @Override
    public void afterTestExecution(ExtensionContext context) throws Exception {
        ActionHolder.add("AfterTestExecutionCallback-" + n);
    }

    @Override
    public void beforeAll(ExtensionContext context) throws Exception {
        ActionHolder.add("BeforeAllCallback-" + n);
    }

    @Override
    public void beforeEach(ExtensionContext context) throws Exception {
        ActionHolder.add("BeforeEachCallback-" + n);
    }

    @Override
    public void beforeTestExecution(ExtensionContext context) throws Exception {
        ActionHolder.add("BeforeTestExecutionCallback-" + n);
    }
}
