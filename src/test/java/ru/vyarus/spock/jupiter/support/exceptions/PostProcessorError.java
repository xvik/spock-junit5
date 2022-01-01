package ru.vyarus.spock.jupiter.support.exceptions;

import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.TestInstancePostProcessor;

/**
 * @author Vyacheslav Rusakov
 * @since 01.01.2022
 */
public class PostProcessorError implements TestInstancePostProcessor {

    @Override
    public void postProcessTestInstance(Object testInstance, ExtensionContext context) throws Exception {
        throw new IllegalStateException("problem");
    }
}
