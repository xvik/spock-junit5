package ru.vyarus.spock.jupiter.support;

import org.junit.jupiter.api.extension.ConditionEvaluationResult;
import org.junit.jupiter.api.extension.ExecutionCondition;
import org.junit.jupiter.api.extension.ExtensionContext;

/**
 * @author Vyacheslav Rusakov
 * @since 27.12.2021
 */
public class SkipCondition implements ExecutionCondition {

    @Override
    public ConditionEvaluationResult evaluateExecutionCondition(ExtensionContext context) {
        ActionHolder.add("SkipCondition");
        return ConditionEvaluationResult.disabled("force quit");
    }
}
