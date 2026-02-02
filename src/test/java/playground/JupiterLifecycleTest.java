package playground;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import playground.tests.JupiterBaseLifecycle;
import playground.tests.JupiterDoubleLifecycle;
import playground.tests.JupiterFiledExtensions;
import playground.tests.JupiterSetupAllMethodExtensions;
import playground.tests.JupiterSetupMethodExtensions;
import playground.tests.JupiterTestMethodExtensions;

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

    @Test
    void testTestMethodExtensions() {
        Assertions.assertEquals(runTest(JupiterTestMethodExtensions.class),
                Arrays.asList(
                        "BeforeEachCallback",
                        "BeforeEachCallback-2",
                        "BeforeTestExecutionCallback",
                        "BeforeTestExecutionCallback-2",
                        "ParameterExtension sampleTest",
                        "test.body 11",
                        "AfterTestExecutionCallback-2",
                        "AfterTestExecutionCallback",
                        "AfterEachCallback-2",
                        "AfterEachCallback"));
    }

    @Test
    void testSetupAllMethodExtensions() {
        Assertions.assertEquals(runTest(JupiterSetupAllMethodExtensions.class),
                Arrays.asList(
                        "BeforeAllCallback-2",
                        "ParameterExtension beforeAll",
                        "test.beforeAll 11",
                        "BeforeEachCallback-2",
                        "BeforeTestExecutionCallback-2",
                        "test.body",
                        "AfterTestExecutionCallback-2",
                        "AfterEachCallback-2",
                        "AfterAllCallback-2"));
    }

    @Test
    void testSetupMethodExtensions() {
        Assertions.assertEquals(runTest(JupiterSetupMethodExtensions.class),
                Arrays.asList(
                        "BeforeAllCallback-2",
                        "BeforeEachCallback-2",
                        "ParameterExtension setUp",
                        "test.before 11",
                        "BeforeTestExecutionCallback-2",
                        "test.body",
                        "AfterTestExecutionCallback-2",
                        "AfterEachCallback-2",
                        "AfterAllCallback-2"));
    }

    @Test
    void testFieldExtensions() {
        Assertions.assertEquals(runTest(JupiterFiledExtensions.class),
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
                        "AfterAllCallback"));
    }
}
