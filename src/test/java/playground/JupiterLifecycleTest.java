package playground;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import playground.tests.JupiterBaseLifecycle;
import playground.tests.JupiterDoubleLifecycle;

import java.util.Arrays;

/**
 * @author Vyacheslav Rusakov
 * @since 26.11.2021
 */
public class JupiterLifecycleTest extends AbstractJupiterTest {

    @Test
    void testBaseLifecycle() {
        Assertions.assertEquals(runTest(JupiterBaseLifecycle.class),
                Arrays.asList(
                        "BeforeAllCallback",
                        "test.beforeAll",
                        "BeforeEachCallback",
                        "test.before",
                        "BeforeTestExecutionCallback",
                        "test.body",
                        "AfterTestExecutionCallback",
                        "test.after",
                        "AfterEachCallback",
                        "test.afterAll",
                        "AfterAllCallback"));
    }

    @Test
    void testDoubleLifecycle() {
        Assertions.assertEquals(runTest(JupiterDoubleLifecycle.class),
                Arrays.asList(
                        "BeforeAllCallback",
                        "BeforeAllCallback-2",
                        "test.beforeAll",
                        "BeforeEachCallback",
                        "BeforeEachCallback-2",
                        "test.before",
                        "BeforeTestExecutionCallback",
                        "BeforeTestExecutionCallback-2",
                        "test.body",
                        "AfterTestExecutionCallback-2",
                        "AfterTestExecutionCallback",
                        "test.after",
                        "AfterEachCallback-2",
                        "AfterEachCallback",
                        "test.afterAll",
                        "AfterAllCallback-2",
                        "AfterAllCallback"));
    }
}
