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
    public static final String CLASS = "class_value";
    public static final String METHOD = "value";

    @Override
    public void beforeAll(ExtensionContext context) throws Exception {
        ActionHolder.add("BeforeAllCallback " + getRootValue(context) + " / " + getClassValue(context));
        getRootStore(context).put(ROOT, 42);
        getClassStore(context).put(CLASS, 12);
        // test auto closing
        getRootStore(context).put("root_test", new CloseableValue("root"));
        getRootStore(context).put("aroot_test", new AutoCloseableValue("aroot"));
        getClassStore(context).put("test", new CloseableValue("class"));
        getClassStore(context).put("atest", new AutoCloseableValue("aclass"));
    }

    @Override
    public void postProcessTestInstance(Object testInstance, ExtensionContext context) throws Exception {
        ActionHolder.add("TestInstancePostProcessor " + getRootValue(context) + " / " + getClassValue(context));
        // method context not yet created
    }

    @Override
    public void beforeEach(ExtensionContext context) throws Exception {
        ActionHolder.add("BeforeEachCallback " + getRootValue(context) + " / " + getClassValue(context) + " / " + getMethodValue(context));
        getLocalStore(context).put(METHOD, 11);
        // test auto closing
        getLocalStore(context).put("test", new CloseableValue("method"));
        getLocalStore(context).put("atest", new AutoCloseableValue("amethod"));
    }

    @Override
    public void beforeTestExecution(ExtensionContext context) throws Exception {
        ActionHolder.add("BeforeTestExecutionCallback " + getRootValue(context) + " / " + getClassValue(context) + " / " + getMethodValue(context));
    }

    @Override
    public void afterEach(ExtensionContext context) throws Exception {
        ActionHolder.add("AfterEachCallback " + getRootValue(context) + " / " + getClassValue(context) + " / " + getMethodValue(context));
    }

    @Override
    public void afterAll(ExtensionContext context) throws Exception {
        ActionHolder.add("AfterAllCallback " + getRootValue(context) + " / " + getClassValue(context));
    }

    public static Object getRootValue(ExtensionContext context) {
        return getRootStore(context).get(ROOT);
    }

    public static Object getClassValue(ExtensionContext context) {
        return getClassStore(context).get(CLASS);
    }

    public static Object getMethodValue(ExtensionContext context) {
        return getLocalStore(context).get(METHOD);
    }

    public static ExtensionContext.Store getRootStore(ExtensionContext context) {
        ExtensionContext rootContext = context.getRoot();
        return rootContext.getStore(ExtensionContext.Namespace.create(rootContext.getUniqueId()));
    }

    public static ExtensionContext.Store getClassStore(ExtensionContext context) {
        return context.getStore(ExtensionContext.Namespace.create(context.getRequiredTestClass()));
    }

    public static ExtensionContext.Store getLocalStore(ExtensionContext context) {
        return context.getStore(ExtensionContext.Namespace.create(
                context.getRequiredTestClass(), context.getRequiredTestMethod()));
    }

    public static class CloseableValue implements ExtensionContext.Store.CloseableResource {

        private final String key;

        public CloseableValue(final String key) {
            this.key = key;
        }

        @Override
        public void close() throws Throwable {
            ActionHolder.add(key + " value closed");
        }
    }

    public static class AutoCloseableValue implements AutoCloseable {

        private final String key;

        public AutoCloseableValue(final String key) {
            this.key = key;
        }

        @Override
        public void close() {
            ActionHolder.add(key + " value closed");
        }
    }
}
