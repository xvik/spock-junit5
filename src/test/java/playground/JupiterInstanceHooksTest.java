package playground;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import playground.tests.JupiterInstanceHooks;
import playground.tests.JupiterTestMethodExtensions;

import java.util.Arrays;

/**
 * @author Vyacheslav Rusakov
 * @since 27.12.2021
 */
public class JupiterInstanceHooksTest extends AbstractJupiterTest {

    @Test
    void testInstanceHookExtensions() {
        Assertions.assertEquals(runTest(JupiterInstanceHooks.class),
                Arrays.asList(
                        "TestInstancePostProcessor true false",
                        "BeforeTestExecutionCallback",
                        "test.body",
                        "AfterTestExecutionCallback",
                        "TestInstancePreDestroyCallback true"));
    }
}
