package playground;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import playground.tests.JupiterParamContextAccess;
import playground.tests.JupiterParameterInjection;

import java.util.Arrays;

/**
 * @author Vyacheslav Rusakov
 * @since 23.12.2021
 */
public class JupiterParamsTest extends AbstractJupiterTest {

    @Test
    void testParams() {
        Assertions.assertEquals(runTest(JupiterParameterInjection.class),
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

    @Test
    void testContext() {
        Assertions.assertEquals(runTest(JupiterParamContextAccess.class),
                Arrays.asList(
                        "param.name arg0",
                        "param.exec sampleTest",
                        "param.index 0",
                        "param.target JupiterParamContextAccess",
                        "param.annotation true true 1",
                        "test.body 12"
                ));
    }
}
