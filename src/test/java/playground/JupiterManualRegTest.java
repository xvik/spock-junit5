package playground;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import playground.tests.JupiterManualRegistration;
import playground.tests.exceptions.JupiterInvalidManualRegistration;
import playground.tests.exceptions.JupiterInvalidManualRegistration2;

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
                        "BeforeEachCallback-2",
                        "BeforeEachCallback-3",
                        "BeforeTestExecutionCallback",
                        "BeforeTestExecutionCallback-2",
                        "BeforeTestExecutionCallback-3",
                        "test.body",
                        "AfterTestExecutionCallback-3",
                        "AfterTestExecutionCallback-2",
                        "AfterTestExecutionCallback",
                        "AfterEachCallback-3",
                        "AfterEachCallback-2",
                        "AfterEachCallback",
                        "AfterAllCallback"));
    }

    @Test
    void testIncorrectFieldDeclaration() {
        Assertions.assertEquals(runTest(JupiterInvalidManualRegistration.class),
                Arrays.asList(
                        "BeforeAllCallback",
                        "AfterAllCallback",
                        "Error: (PreconditionViolationException) Failed to register extension via field [ru.vyarus.spock.jupiter.support.LifecycleExtension playground.tests.exceptions.JupiterInvalidManualRegistration.ext]. The field registers an extension of type [ru.vyarus.spock.jupiter.support.LifecycleExtension] via @RegisterExtension and @ExtendWith, but only one registration of a given extension type is permitted."));
    }

    @Test
    void testIncorrectFieldDeclaration2() {
        Assertions.assertEquals(runTest(JupiterInvalidManualRegistration2.class),
                Arrays.asList(
                        "Error: (PreconditionViolationException) Failed to register extension via @RegisterExtension field [java.lang.Integer playground.tests.exceptions.JupiterInvalidManualRegistration2.ext]: field value's type [java.lang.Integer] must implement an [org.junit.jupiter.api.extension.Extension] API."));
    }
}
