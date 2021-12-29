package ru.vyarus.spock.jupiter.support.exceptions;

import org.junit.jupiter.api.extension.AfterAllCallback;
import org.junit.jupiter.api.extension.ExtensionContext;

/**
 * @author Vyacheslav Rusakov
 * @since 30.12.2021
 */
public class AfterAllError implements AfterAllCallback {

    @Override
    public void afterAll(ExtensionContext context) throws Exception {
        throw new IllegalStateException("problem");
    }
}
