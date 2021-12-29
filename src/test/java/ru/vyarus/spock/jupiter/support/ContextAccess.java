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
