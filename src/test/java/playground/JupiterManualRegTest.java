package playground;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import playground.tests.JupiterDoubleLifecycle;
import playground.tests.JupiterManualRegistration;

import java.util.Arrays;

/**
 * @author Vyacheslav Rusakov
 * @since 26.12.2021
 */
public class JupiterManualRegTest extends AbstractJupiterTest {

    @Test
    void testDoubleLifecycle() {
        Assertions.assertEquals(runTest(JupiterManualRegistration.class),
                Arrays.asList(
                        "BeforeAllCallback",
                        "BeforeEachCallback",
                        "BeforeEachCallback-2",
                        "BeforeTestExecutionCallback",
                        "BeforeTestExecutionCallback-2",
                        "test.body",
                        "AfterTestExecutionCallback-2",
                        "AfterTestExecutionCallback",
                        "AfterEachCallback-2",
                        "AfterEachCallback",
                        "AfterAllCallback"));
    }
}
