package playground;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
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
                        "BeforeAllCallback null / null",
                        "TestInstancePostProcessor 42 / 12",
                        "BeforeEachCallback 42 / 12 / null",
                        "BeforeTestExecutionCallback 42 / 12 / 11",
                        "test.body",
                        "AfterEachCallback 42 / 12 / 11",
                        "amethod value closed",
                        "method value closed",
                        "AfterAllCallback 42 / 12",
                        "aclass value closed",
                        "class value closed",
                        "aroot value closed",
                        "root value closed"));
    }
}
