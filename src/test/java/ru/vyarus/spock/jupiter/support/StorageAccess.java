package ru.vyarus.spock.jupiter.support;

import org.junit.jupiter.api.extension.AfterAllCallback;
import org.junit.jupiter.api.extension.AfterEachCallback;
import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.BeforeTestExecutionCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.TestInstancePostProcessor;

/**
 * @author Vyacheslav Rusakov
 * @since 30.12.2021
 */
public class StorageAccess implements
        BeforeAllCallback,
        TestInstancePostProcessor,
        BeforeEachCallback,
        BeforeTestExecutionCallback,
        AfterEachCallback,
        AfterAllCallback {

    public static final String GLOBAL = "global_value";
    public static final String LOCAL = "value";

    @Override
    public void beforeAll(ExtensionContext context) throws Exception {
        ActionHolder.add("BeforeAllCallback " + getGlobalValue(context));
        getGlobalStore(context).put(GLOBAL, 12);
        // test auto closing
        getGlobalStore(context).put("test", new ClosableValue("global"));
    }

    @Override
    public void postProcessTestInstance(Object testInstance, ExtensionContext context) throws Exception {
        ActionHolder.add("TestInstancePostProcessor " + getGlobalValue(context));
        // method context not yet created
    }

    @Override
    public void beforeEach(ExtensionContext context) throws Exception {
        ActionHolder.add("BeforeEachCallback " + getGlobalValue(context) + " " + getLocalValue(context));
        getLocalStore(context).put(LOCAL, 11);
        // test auto closing
        getLocalStore(context).put("test", new ClosableValue("local"));
    }

    @Override
    public void beforeTestExecution(ExtensionContext context) throws Exception {
        ActionHolder.add("BeforeTestExecutionCallback " + getGlobalValue(context) + " " + getLocalValue(context));
    }

    @Override
    public void afterEach(ExtensionContext context) throws Exception {
        ActionHolder.add("AfterEachCallback " + getGlobalValue(context) + " " + getLocalValue(context));
    }

    @Override
    public void afterAll(ExtensionContext context) throws Exception {
        ActionHolder.add("AfterAllCallback " + getGlobalValue(context));
    }

    public static Object getGlobalValue(ExtensionContext context) {
        return getGlobalStore(context).get(GLOBAL);
    }

    public static Object getLocalValue(ExtensionContext context) {
        return getLocalStore(context).get(LOCAL);
    }

    public static ExtensionContext.Store getGlobalStore(ExtensionContext context) {
        return context.getStore(ExtensionContext.Namespace.create(context.getRequiredTestClass()));
    }

    public static ExtensionContext.Store getLocalStore(ExtensionContext context) {
        return context.getStore(ExtensionContext.Namespace.create(
                context.getRequiredTestClass(), context.getRequiredTestMethod()));
    }

    public static class ClosableValue implements ExtensionContext.Store.CloseableResource {

        private final String key;

        public ClosableValue(final String key) {
            this.key = key;
        }

        @Override
        public void close() throws Throwable {
            ActionHolder.add(key + " value closed");
        }
    }
}
