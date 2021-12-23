package playground;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import playground.tests.ParameterTest;

import java.util.Arrays;

/**
 * @author Vyacheslav Rusakov
 * @since 23.12.2021
 */
public class JupiterParamsTest extends AbstractJupiterTest {

    @Test
    void testParams() {
        Assertions.assertEquals(runTest(ParameterTest.class),
                Arrays.asList(
                        "BeforeAllCallback",
                        "ParameterExtension beforeAll",
                        "test.beforeAll 11",
                        "BeforeEachCallback",
                        "ParameterExtension setUp",
                        "test.before 11",
                        "BeforeTestExecutionCallback",
                        "ParameterExtension sampleTest",
                        "test.body 11",
                        "AfterTestExecutionCallback",
                        "ParameterExtension tearDown",
                        "test.after 11",
                        "AfterEachCallback",
                        "ParameterExtension afterAll",
                        "test.afterAll 11",
                        "AfterAllCallback"));
    }
}
