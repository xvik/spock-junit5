package ru.vyarus.spock.jupiter.support.exceptions;

import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.ExtensionContext;

/**
 * @author Vyacheslav Rusakov
 * @since 30.12.2021
 */
public class BeforeAllError implements BeforeAllCallback {

    @Override
    public void beforeAll(ExtensionContext context) throws Exception {
        throw new IllegalStateException("problem");
    }
}
