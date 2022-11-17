package playground;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import playground.tests.JupiterMultiStorageFirst;
import playground.tests.JupiterMultiStorageSecond;

import java.util.Arrays;

/**
 * @author Ken Davidson
 * @since 18.11.2022
 */
public class JupiterMultiStorageRepeatTest extends AbstractJupiterTest {

    @Test
    void testMultiRootContextStorage() {
        Assertions.assertEquals(
                runTest(JupiterMultiStorageFirst.class, JupiterMultiStorageSecond.class),
                Arrays.asList(
                        "Root value 1",
                        "JupiterMultiStorageFirst class value 2",
                        "sampleTest() method value 3",
                        "test.first",
                        "Root value 1",
                        "JupiterMultiStorageSecond class value 1",
                        "sampleTest() method value 2",
                        "test.second")
        );
    }
}
