package playground;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.AfterAllCallback;
import playground.tests.JupiterStorage;

import java.util.Arrays;

/**
 * @author Vyacheslav Rusakov
 * @since 30.12.2021
 */
public class JupiterStorageTest extends AbstractJupiterTest {

    @Test
    void testStorageAccess() {
        Assertions.assertEquals(runTest(JupiterStorage.class),
                Arrays.asList(
                        "BeforeAllCallback null",
                        "TestInstancePostProcessor 12",
                        "BeforeEachCallback 12 null",
                        "BeforeTestExecutionCallback 12 11",
                        "test.body",
                        "AfterEachCallback 12 11",
                        "local value closed",
                        "AfterAllCallback 12",
                        "global value closed"));
    }
}
