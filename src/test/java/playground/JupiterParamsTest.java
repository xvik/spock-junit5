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
                        "test.beforeAll 11",
                        "test.before 11",
                        "test.body 11",
                        "test.after 11",
                        "test.afterAll 11"));
    }
}
