package playground;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import playground.tests.JupiterManualRegistration;

import java.util.Arrays;

/**
 * @author Vyacheslav Rusakov
 * @since 26.12.2021
 */
public class JupiterManualRegTest extends AbstractJupiterTest {

    @Test
    void testManualRegistration() {
        Assertions.assertEquals(runTest(JupiterManualRegistration.class),
                Arrays.asList(
                        "BeforeAllCallback",
                        "BeforeEachCallback",
                        "BeforeEachCallback-3",
                        "BeforeEachCallback-2",
                        "BeforeTestExecutionCallback",
                        "BeforeTestExecutionCallback-3",
                        "BeforeTestExecutionCallback-2",
                        "test.body",
                        "AfterTestExecutionCallback-2",
                        "AfterTestExecutionCallback-3",
                        "AfterTestExecutionCallback",
                        "AfterEachCallback-2",
                        "AfterEachCallback-3",
                        "AfterEachCallback",
                        "AfterAllCallback"));
    }
}
