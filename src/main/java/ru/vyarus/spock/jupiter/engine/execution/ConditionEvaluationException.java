package ru.vyarus.spock.jupiter.engine.execution;

import org.junit.platform.commons.JUnitException;

/**
 * Thrown if an error is encountered while evaluating an
 * {@link org.junit.jupiter.api.extension.ExecutionCondition}.
 * <p>
 * Copy of {@code org.junit.jupiter.engine.execution.ConditionEvaluationException}.
 *
 * @author Vyacheslav Rusakov
 * @since 27.12.2021
 */
public class ConditionEvaluationException extends JUnitException {

    private static final long serialVersionUID = 1L;

    public ConditionEvaluationException(String message, Throwable cause) {
        super(message, cause);
    }
}
