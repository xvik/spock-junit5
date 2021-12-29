package ru.vyarus.spock.jupiter.support.exceptions;

import org.junit.jupiter.api.extension.AfterEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;

/**
 * @author Vyacheslav Rusakov
 * @since 30.12.2021
 */
public class AfterEachError implements AfterEachCallback {

    @Override
    public void afterEach(ExtensionContext context) throws Exception {
        throw new IllegalStateException("problem");
    }
}
