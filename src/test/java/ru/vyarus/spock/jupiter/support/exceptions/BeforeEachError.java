package ru.vyarus.spock.jupiter.support.exceptions;

import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;

/**
 * @author Vyacheslav Rusakov
 * @since 30.12.2021
 */
public class BeforeEachError implements BeforeEachCallback {

    @Override
    public void beforeEach(ExtensionContext context) throws Exception {
        throw new IllegalStateException("problem");
    }
}
