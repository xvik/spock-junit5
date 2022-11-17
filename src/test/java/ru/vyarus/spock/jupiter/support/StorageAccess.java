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

    public static final String ROOT = "root_value";
    public static final String GLOBAL = "global_value";
    public static final String LOCAL = "value";

    @Override
    public void beforeAll(ExtensionContext context) throws Exception {
        ActionHolder.add("BeforeAllCallback " + getRootValue(context) + " " + getGlobalValue(context));
        getRootStore(context).put(ROOT, 42);
        getGlobalStore(context).put(GLOBAL, 12);
        // test auto closing
        getRootStore(context).put("root_test", new ClosableValue("root"));
        getGlobalStore(context).put("test", new ClosableValue("global"));
    }

    @Override
    public void postProcessTestInstance(Object testInstance, ExtensionContext context) throws Exception {
        ActionHolder.add("TestInstancePostProcessor " + getRootValue(context) + " " + getGlobalValue(context));
        // method context not yet created
    }

    @Override
    public void beforeEach(ExtensionContext context) throws Exception {
        ActionHolder.add("BeforeEachCallback " + getRootValue(context) + " " + getGlobalValue(context) + " " + getLocalValue(context));
        getLocalStore(context).put(LOCAL, 11);
        // test auto closing
        getLocalStore(context).put("test", new ClosableValue("local"));
    }

    @Override
    public void beforeTestExecution(ExtensionContext context) throws Exception {
        ActionHolder.add("BeforeTestExecutionCallback " + getRootValue(context) + " " + getGlobalValue(context) + " " + getLocalValue(context));
    }

    @Override
    public void afterEach(ExtensionContext context) throws Exception {
        ActionHolder.add("AfterEachCallback " + getRootValue(context) + " " + getGlobalValue(context) + " " + getLocalValue(context));
    }

    @Override
    public void afterAll(ExtensionContext context) throws Exception {
        ActionHolder.add("AfterAllCallback " + getRootValue(context) + " " + getGlobalValue(context));
    }

    public static Object getRootValue(ExtensionContext context) {
        return getRootStore(context).get(ROOT);
    }

    public static Object getGlobalValue(ExtensionContext context) {
        return getGlobalStore(context).get(GLOBAL);
    }

    public static Object getLocalValue(ExtensionContext context) {
        return getLocalStore(context).get(LOCAL);
    }

    public static ExtensionContext.Store getRootStore(ExtensionContext context) {
        ExtensionContext rootContext = context;
        do {
            rootContext = rootContext.getRoot();
        } while (rootContext.getParent().isPresent());
        return rootContext.getStore(ExtensionContext.Namespace.create(rootContext.getUniqueId()));
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
