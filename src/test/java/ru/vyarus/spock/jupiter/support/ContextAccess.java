package ru.vyarus.spock.jupiter.support;

import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;

/**
 * @author Vyacheslav Rusakov
 * @since 30.12.2021
 */
public class ContextAccess implements BeforeAllCallback, BeforeEachCallback {

    @Override
    public void beforeAll(ExtensionContext context) throws Exception {
        // root context
        ExtensionContext root = context.getRoot();
        ActionHolder.add("root.id: "+ root.getUniqueId());
        ActionHolder.add("root.display name: " + root.getDisplayName());
        ActionHolder.add("root.element: " + root.getElement().isPresent());
        ActionHolder.add("root.parent: " + root.getParent().isPresent());
        ActionHolder.add("root.root: " + root.getRoot().getDisplayName());
        ActionHolder.add("root.lifecycle: " + root.getTestInstanceLifecycle().isPresent());
        ActionHolder.add("root.exec mode: " + root.getExecutionMode());
        ActionHolder.add("root.exception: " + root.getExecutionException().isPresent());
        ActionHolder.add("root.test class: " + root.getTestClass().isPresent());
        ActionHolder.add("root.test method: " + root.getTestMethod().isPresent());
        ActionHolder.add("root.tags: " + root.getTags());
        ActionHolder.add("root.test instance: " + root.getTestInstance().isPresent());
        ActionHolder.add("root.test instances: " + root.getTestInstances().isPresent());

        // class context
        ActionHolder.add("class.id: " + context.getUniqueId());
        ActionHolder.add("class.display name: " + context.getDisplayName());
        ActionHolder.add("class.parent: " + context.getParent().isPresent());
        ActionHolder.add("class.root: " + context.getRoot().getDisplayName());
        ActionHolder.add("class.element: " + context.getElement().get());
        ActionHolder.add("class.lifecycle: " + context.getTestInstanceLifecycle().get());
        ActionHolder.add("class.exec mode: " + context.getExecutionMode());
        ActionHolder.add("class.exception: " + context.getExecutionException().isPresent());
        ActionHolder.add("class.test class: " + context.getRequiredTestClass());
        ActionHolder.add("class.test method: " + context.getTestMethod().isPresent());
        ActionHolder.add("class.tags: " + context.getTags());
        ActionHolder.add("class.test instance: " + context.getTestInstance().isPresent());
        ActionHolder.add("class.test instances: " + context.getTestInstances().isPresent());
    }

    @Override
    public void beforeEach(ExtensionContext context) throws Exception {
        // method context
        ActionHolder.add("method.id: " + context.getUniqueId());
        ActionHolder.add("method.display name: " + context.getDisplayName());
        ActionHolder.add("method.parent: " + context.getParent().get().getDisplayName());
        ActionHolder.add("method.root: " + context.getRoot().getDisplayName());
        ActionHolder.add("method.element: " + context.getElement().get());
        ActionHolder.add("method.lifecycle: " + context.getTestInstanceLifecycle().get());
        ActionHolder.add("method.exec mode: " + context.getExecutionMode());
        ActionHolder.add("method.exception: " + context.getExecutionException().isPresent());
        ActionHolder.add("method.test class: " + context.getRequiredTestClass());
        ActionHolder.add("method.test method: " + context.getRequiredTestMethod());
        ActionHolder.add("method.tags: " + context.getTags());
        ActionHolder.add("method.test instance: " + context.getTestInstance().isPresent());
        ActionHolder.add("method.test instances: " + context.getTestInstances().get().getAllInstances().size());
    }
}
