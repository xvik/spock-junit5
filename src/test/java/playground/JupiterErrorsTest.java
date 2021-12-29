package playground;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import playground.tests.exceptions.JupiterAfterAllError;
import playground.tests.exceptions.JupiterAfterEachError;
import playground.tests.exceptions.JupiterBeforeAllError;
import playground.tests.exceptions.JupiterBeforeEachError;
import playground.tests.exceptions.JupiterTestError;

import java.util.Arrays;

/**
 * @author Vyacheslav Rusakov
 * @since 30.12.2021
 */
public class JupiterErrorsTest extends AbstractJupiterTest {

    @Test
    void testBeforeAllError() {
        Assertions.assertEquals(runTest(JupiterBeforeAllError.class),
                Arrays.asList(
                        "BeforeAllCallback",
                        "AfterAllCallback-2",
                        "AfterAllCallback",
                        "Error: (IllegalStateException) problem"));
    }

    @Test
    void testBeforeEachError() {
        Assertions.assertEquals(runTest(JupiterBeforeEachError.class),
                Arrays.asList(
                        "BeforeAllCallback",
                        "BeforeAllCallback-2",
                        "BeforeEachCallback",
                        "AfterEachCallback-2",
                        "AfterEachCallback",
                        "AfterAllCallback-2",
                        "AfterAllCallback",
                        "Error: (IllegalStateException) problem"));
    }

    @Test
    void testAfterEachError() {
        Assertions.assertEquals(runTest(JupiterAfterEachError.class),
                Arrays.asList(
                        "BeforeAllCallback",
                        "BeforeAllCallback-2",
                        "BeforeEachCallback",
                        "BeforeEachCallback-2",
                        "BeforeTestExecutionCallback",
                        "BeforeTestExecutionCallback-2",
                        "test.body",
                        "AfterTestExecutionCallback-2",
                        "AfterTestExecutionCallback",
                        "AfterEachCallback-2",
                        "AfterEachCallback",
                        "AfterAllCallback-2",
                        "AfterAllCallback",
                        "Error: (IllegalStateException) problem"));
    }

    @Test
    void testAfterAllError() {
        Assertions.assertEquals(runTest(JupiterAfterAllError.class),
                Arrays.asList(
                        "BeforeAllCallback",
                        "BeforeAllCallback-2",
                        "BeforeEachCallback",
                        "BeforeEachCallback-2",
                        "BeforeTestExecutionCallback",
                        "BeforeTestExecutionCallback-2",
                        "test.body",
                        "AfterTestExecutionCallback-2",
                        "AfterTestExecutionCallback",
                        "AfterEachCallback-2",
                        "AfterEachCallback",
                        "AfterAllCallback-2",
                        "AfterAllCallback",
                        "Error: (IllegalStateException) problem"));
    }

    @Test
    void testMethodError() {
        Assertions.assertEquals(runTest(JupiterTestError.class),
                Arrays.asList(
                        "BeforeAllCallback",
                        "BeforeEachCallback",
                        "BeforeTestExecutionCallback",
                        "AfterTestExecutionCallback",
                        "AfterEachCallback",
                        "AfterAllCallback",
                        "Error: (IllegalStateException) problem"));
    }
}
