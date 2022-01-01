package ru.vyarus.spock.jupiter.engine.execution;

import org.spockframework.runtime.SpockException;

/**
 * Thrown if an error is encountered while evaluating an {@link org.junit.jupiter.api.extension.ExecutionCondition}.
 * <p>
 * Copy of {@code org.junit.jupiter.engine.execution.ConditionEvaluationException}.
 *
 * @author Vyacheslav Rusakov
 * @since 27.12.2021
 */
public class ConditionEvaluationException extends SpockException {

    private static final long serialVersionUID = 1L;

    public ConditionEvaluationException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
