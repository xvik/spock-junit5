package ru.vyarus.spock.jupiter.support;

import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.TestInstancePreDestroyCallback;

/**
 * @author Vyacheslav Rusakov
 * @since 27.12.2021
 */
public class PreDestroyExtension implements TestInstancePreDestroyCallback {

    @Override
    public void preDestroyTestInstance(ExtensionContext context) throws Exception {
        ActionHolder.add("TestInstancePreDestroyCallback " + context.getTestInstance().isPresent());
    }
}
