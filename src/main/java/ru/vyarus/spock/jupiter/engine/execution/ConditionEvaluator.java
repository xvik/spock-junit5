package ru.vyarus.spock.jupiter.engine.execution;

import org.junit.jupiter.api.extension.ConditionEvaluationResult;
import org.junit.jupiter.api.extension.ExecutionCondition;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.platform.commons.logging.Logger;
import org.junit.platform.commons.logging.LoggerFactory;
import org.junit.platform.commons.util.StringUtils;
import org.spockframework.runtime.model.ISkippable;
import ru.vyarus.spock.jupiter.engine.ExtensionRegistry;
import ru.vyarus.spock.jupiter.engine.context.AbstractContext;

import static java.lang.String.format;

/**
 * {@code ConditionEvaluator} evaluates {@link ExecutionCondition} extensions.
 * <p>
 * Based on {@code org.junit.jupiter.engine.execution.ConditionEvaluator} from junit-jupiter-engine (5.8).
 *
 * @author Vyacheslav Rusakov
 * @since 27.12.2021
 */
public class ConditionEvaluator {
    private static final Logger logger = LoggerFactory.getLogger(ConditionEvaluator.class);

    private static final ConditionEvaluationResult ENABLED = ConditionEvaluationResult.enabled(
            "No 'disabled' conditions encountered");

    public static boolean skip(ISkippable test, AbstractContext context) {
        // org.junit.jupiter.engine.descriptor.JupiterTestDescriptor.shouldBeSkipped
        final ConditionEvaluationResult execution = ConditionEvaluator.evaluate(context.getRegistry(), context);
        if (execution.isDisabled()) {
            test.skip(execution.getReason().orElse("<unknown>"));
            return true;
        }
        return false;
    }

    /**
     * Evaluate all {@link ExecutionCondition} extensions registered for the
     * supplied {@link ExtensionContext}.
     *
     * @param context the current {@code ExtensionContext}
     * @return the first <em>disabled</em> {@code ConditionEvaluationResult},
     * or a default <em>enabled</em> {@code ConditionEvaluationResult} if no
     * disabled conditions are encountered
     */
    public static ConditionEvaluationResult evaluate(final ExtensionRegistry extensionRegistry,
                                              final ExtensionContext context) {

        return extensionRegistry.stream(ExecutionCondition.class)
                .map(condition -> evaluateImpl(condition, context))
                .filter(ConditionEvaluationResult::isDisabled)
                .findFirst()
                .orElse(ENABLED);
    }

    private static ConditionEvaluationResult evaluateImpl(ExecutionCondition condition, ExtensionContext context) {
        try {
            ConditionEvaluationResult result = condition.evaluateExecutionCondition(context);
            logResult(condition.getClass(), result, context);
            return result;
        } catch (Exception ex) {
            throw evaluationException(condition.getClass(), ex);
        }
    }

    private static void logResult(Class<?> conditionType, ConditionEvaluationResult result, ExtensionContext context) {
        logger.trace(() -> format("Evaluation of condition [%s] on [%s] resulted in: %s", conditionType.getName(),
                context.getElement().get(), result));
    }

    private static ConditionEvaluationException evaluationException(Class<?> conditionType, Exception ex) {
        String cause = StringUtils.isNotBlank(ex.getMessage()) ? ": " + ex.getMessage() : "";
        return new ConditionEvaluationException(
                format("Failed to evaluate condition [%s]%s", conditionType.getName(), cause), ex);
    }

}
