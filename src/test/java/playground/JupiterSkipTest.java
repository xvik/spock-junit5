package playground;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import playground.tests.JupiterParameterInjection;
import playground.tests.JupiterSkipClass;
import playground.tests.JupiterSkipMethod;

import java.util.Arrays;

/**
 * @author Vyacheslav Rusakov
 * @since 27.12.2021
 */
public class JupiterSkipTest extends AbstractJupiterTest {

    @Test
    void testClassSkip() {
        Assertions.assertEquals(runTest(JupiterSkipClass.class),
                Arrays.asList(
                        "SkipCondition"));
    }

    @Test
    void testMethodSkip() {
        Assertions.assertEquals(runTest(JupiterSkipMethod.class),
                Arrays.asList(
                        "SkipCondition",
                        "test.body2"));
    }
}
