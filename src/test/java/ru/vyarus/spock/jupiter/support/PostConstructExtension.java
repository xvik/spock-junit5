package ru.vyarus.spock.jupiter.support;

import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.TestInstancePostProcessor;

/**
 * @author Vyacheslav Rusakov
 * @since 27.12.2021
 */
public class PostConstructExtension implements TestInstancePostProcessor {

    @Override
    public void postProcessTestInstance(Object testInstance, ExtensionContext context) throws Exception {
        ActionHolder.add("TestInstancePostProcessor " + (testInstance != null) + " "
                + context.getTestInstance().isPresent());
    }
}
