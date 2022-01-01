package ru.vyarus.spock.jupiter.support.exceptions;

import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.TestInstancePreDestroyCallback;

/**
 * @author Vyacheslav Rusakov
 * @since 01.01.2022
 */
public class PreDestroyError implements TestInstancePreDestroyCallback {

    @Override
    public void preDestroyTestInstance(ExtensionContext context) throws Exception {
        throw new IllegalStateException("problem");
    }
}
