package playground;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import playground.tests.exceptions.JupiterAfterAllMethodExceptionHandler;
import playground.tests.exceptions.JupiterAfterAllMethodExceptionHandler2;
import playground.tests.exceptions.JupiterAfterEachMethodExceptionHandler;
import playground.tests.exceptions.JupiterAfterEachMethodExceptionHandler2;
import playground.tests.exceptions.JupiterBeforeAllMethodExceptionHandler;
import playground.tests.exceptions.JupiterBeforeAllMethodExceptionHandler2;
import playground.tests.exceptions.JupiterBeforeEachMethodExceptionHandler;
import playground.tests.exceptions.JupiterBeforeEachMethodExceptionHandler2;

import java.util.Arrays;

/**
 * @author Vyacheslav Rusakov
 * @since 03.02.2026
 */
public class JupiterMethodErrorsTest extends AbstractJupiterTest {

    @Test
    void testBeforeAllErrorHandling() {
        Assertions.assertEquals(runTest(JupiterBeforeAllMethodExceptionHandler.class),
                Arrays.asList(
                        "BeforeAllCallback",
                        "BeforeAllExceptionHandler problem",
                        "BeforeAllEatExceptionHandler problem",
                        "BeforeEachCallback",
                        "BeforeTestExecutionCallback",
                        "test.body",
                        "AfterTestExecutionCallback",
                        "AfterEachCallback",
                        "AfterAllCallback"));
    }

    @Test
    void testBeforeAllErrorRethrow() {
        Assertions.assertEquals(runTest(JupiterBeforeAllMethodExceptionHandler2.class),
                Arrays.asList(
                        "BeforeAllCallback",
                        "BeforeAllExceptionHandler problem",
                        "AfterAllCallback",
                        "Error: (IllegalStateException) problem"));
    }

    @Test
    void testBeforeEachErrorHandling() {
        Assertions.assertEquals(runTest(JupiterBeforeEachMethodExceptionHandler.class),
                Arrays.asList(
                        "BeforeAllCallback",
                        "BeforeEachCallback",
                        "BeforeEachExceptionHandler problem",
                        "BeforeEachEatExceptionHandler problem",
                        "BeforeTestExecutionCallback",
                        "test.body",
                        "AfterTestExecutionCallback",
                        "AfterEachCallback",
                        "AfterAllCallback"));
    }

    @Test
    void testBeforeEachErrorRethrow() {
        Assertions.assertEquals(runTest(JupiterBeforeEachMethodExceptionHandler2.class),
                Arrays.asList(
                        "BeforeAllCallback",
                        "BeforeEachCallback",
                        "BeforeEachExceptionHandler problem",
                        "AfterEachCallback",
                        "AfterAllCallback",
                        "Error: (IllegalStateException) problem"));
    }

    @Test
    void testAfterEachErrorHandling() {
        Assertions.assertEquals(runTest(JupiterAfterEachMethodExceptionHandler.class),
                Arrays.asList(
                        "BeforeAllCallback",
                        "BeforeEachCallback",
                        "BeforeTestExecutionCallback",
                        "test.body",
                        "AfterTestExecutionCallback",
                        "AfterEachExceptionHandler problem",
                        "AfterEachEatExceptionHandler problem",
                        "AfterEachCallback",
                        "AfterAllCallback"));
    }

    @Test
    void testAfterEachErrorRethrow() {
        Assertions.assertEquals(runTest(JupiterAfterEachMethodExceptionHandler2.class),
                Arrays.asList(
                        "BeforeAllCallback",
                        "BeforeEachCallback",
                        "BeforeTestExecutionCallback",
                        "test.body",
                        "AfterTestExecutionCallback",
                        "AfterEachExceptionHandler problem",
                        "AfterEachCallback",
                        "AfterAllCallback",
                        "Error: (IllegalStateException) problem"));
    }

    @Test
    void testAfterAllErrorHandling() {
        Assertions.assertEquals(runTest(JupiterAfterAllMethodExceptionHandler.class),
                Arrays.asList(
                        "BeforeAllCallback",
                        "BeforeEachCallback",
                        "BeforeTestExecutionCallback",
                        "test.body",
                        "AfterTestExecutionCallback",
                        "AfterEachCallback",
                        "AfterAllExceptionHandler problem",
                        "AfterAllEatExceptionHandler problem",
                        "AfterAllCallback"));
    }

    @Test
    void testAfterAllErrorRethrow() {
        Assertions.assertEquals(runTest(JupiterAfterAllMethodExceptionHandler2.class),
                Arrays.asList(
                        "BeforeAllCallback",
                        "BeforeEachCallback",
                        "BeforeTestExecutionCallback",
                        "test.body",
                        "AfterTestExecutionCallback",
                        "AfterEachCallback",
                        "AfterAllExceptionHandler problem",
                        "AfterAllCallback",
                        "Error: (IllegalStateException) problem"));
    }
}
