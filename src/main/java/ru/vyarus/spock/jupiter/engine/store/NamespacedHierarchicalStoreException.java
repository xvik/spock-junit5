package ru.vyarus.spock.jupiter.engine.store;

import org.junit.platform.commons.JUnitException;

import java.io.Serial;

/**
 * Copy of {@code }org.junit.platform.engine.support.store.NamespacedHierarchicalStoreException}.
 *
 * @author Vyacheslav Rusakov
 * @since 04.02.2026
 */
public class NamespacedHierarchicalStoreException extends JUnitException {

    @Serial
    private static final long serialVersionUID = 1L;

    public NamespacedHierarchicalStoreException(final String message) {
        super(message);
    }

    public NamespacedHierarchicalStoreException(final String message, final Throwable cause) {
        super(message, cause);
    }

}
