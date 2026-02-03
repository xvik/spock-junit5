package playground;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import playground.tests.exceptions.JupiterAfterAllError;
import playground.tests.exceptions.JupiterAfterEachError;
import playground.tests.exceptions.JupiterAfterTestError;
import playground.tests.exceptions.JupiterBeforeAllError;
import playground.tests.exceptions.JupiterBeforeEachError;
import playground.tests.exceptions.JupiterBeforeTestError;
import playground.tests.exceptions.JupiterConditionError;
import playground.tests.exceptions.JupiterExecutableInvokerError;
import playground.tests.exceptions.JupiterParameterError;
import playground.tests.exceptions.JupiterParameterError2;
import playground.tests.exceptions.JupiterPostProcessorError;
import playground.tests.exceptions.JupiterPreDestroyError;
import playground.tests.exceptions.JupiterTestError;
import playground.tests.exceptions.JupiterTestExceptionHandler;
import playground.tests.exceptions.JupiterTestExceptionHandler2;
import playground.tests.exceptions.JupiterTestExceptionHandler3;

import java.util.Arrays;
import java.util.Collections;

/**
 * @author Vyacheslav Rusakov
 * @since 30.12.2021
 */
public class JupiterErrorsTest extends AbstractJupiterTest {

    @Test
    void testBeforeAllError() {
        Assertions.assertEquals(runTest(JupiterBeforeAllError.class),
                Arrays.asList(
                        "BeforeAllCallback",
                        "AfterAllCallback-2",
                        "AfterAllCallback",
                        "Error: (IllegalStateException) problem"));
    }

    @Test
    void testBeforeEachError() {
        Assertions.assertEquals(runTest(JupiterBeforeEachError.class),
                Arrays.asList(
                        "BeforeAllCallback",
                        "BeforeAllCallback-2",
                        "BeforeEachCallback",
                        "AfterEachCallback-2",
                        "AfterEachCallback",
                        "AfterAllCallback-2",
                        "AfterAllCallback",
                        "Error: (IllegalStateException) problem"));
    }

    @Test
    void testAfterEachError() {
        Assertions.assertEquals(runTest(JupiterAfterEachError.class),
                Arrays.asList(
                        "BeforeAllCallback",
                        "BeforeAllCallback-2",
                        "BeforeEachCallback",
                        "BeforeEachCallback-2",
                        "BeforeTestExecutionCallback",
                        "BeforeTestExecutionCallback-2",
                        "test.body",
                        "AfterTestExecutionCallback-2",
                        "AfterTestExecutionCallback",
                        "AfterEachCallback-2",
                        "AfterEachCallback",
                        "AfterAllCallback-2",
                        "AfterAllCallback",
                        "Error: (IllegalStateException) problem"));
    }

    @Test
    void testAfterAllError() {
        Assertions.assertEquals(runTest(JupiterAfterAllError.class),
                Arrays.asList(
                        "BeforeAllCallback",
                        "BeforeAllCallback-2",
                        "BeforeEachCallback",
                        "BeforeEachCallback-2",
                        "BeforeTestExecutionCallback",
                        "BeforeTestExecutionCallback-2",
                        "test.body",
                        "AfterTestExecutionCallback-2",
                        "AfterTestExecutionCallback",
                        "AfterEachCallback-2",
                        "AfterEachCallback",
                        "AfterAllCallback-2",
                        "AfterAllCallback",
                        "Error: (IllegalStateException) problem"));
    }

    @Test
    void testMethodError() {
        Assertions.assertEquals(runTest(JupiterTestError.class),
                Arrays.asList(
                        "BeforeAllCallback",
                        "BeforeEachCallback",
                        "BeforeTestExecutionCallback",
                        "AfterTestExecutionCallback",
                        "AfterEachCallback",
                        "AfterAllCallback",
                        "Error: (IllegalStateException) problem"));
    }

    @Test
    void testBeforeTestError() {
        Assertions.assertEquals(runTest(JupiterBeforeTestError.class),
                Arrays.asList(
                        "BeforeAllCallback",
                        "BeforeAllCallback-2",
                        "BeforeEachCallback",
                        "BeforeEachCallback-2",
                        "BeforeTestExecutionCallback",
                        "AfterTestExecutionCallback-2",
                        "AfterTestExecutionCallback",
                        "AfterEachCallback-2",
                        "AfterEachCallback",
                        "AfterAllCallback-2",
                        "AfterAllCallback",
                        "Error: (IllegalStateException) problem"));
    }

    @Test
    void testAfterTestError() {
        Assertions.assertEquals(runTest(JupiterAfterTestError.class),
                Arrays.asList(
                        "BeforeAllCallback",
                        "BeforeAllCallback-2",
                        "BeforeEachCallback",
                        "BeforeEachCallback-2",
                        "BeforeTestExecutionCallback",
                        "BeforeTestExecutionCallback-2",
                        "test.body",
                        "AfterTestExecutionCallback-2",
                        "AfterTestExecutionCallback",
                        "AfterEachCallback-2",
                        "AfterEachCallback",
                        "AfterAllCallback-2",
                        "AfterAllCallback",
                        "Error: (IllegalStateException) problem"));
    }

    @Test
    void testPostProcessError() {
        Assertions.assertEquals(runTest(JupiterPostProcessorError.class),
                Arrays.asList(
                        "BeforeAllCallback",
                        "AfterAllCallback",
                        "Error: (IllegalStateException) problem"));
    }

    @Test
    void testPreDestroyError() {
        Assertions.assertEquals(runTest(JupiterPreDestroyError.class),
                Arrays.asList(
                        "BeforeAllCallback",
                        "BeforeEachCallback",
                        "BeforeTestExecutionCallback",
                        "test.body",
                        "AfterTestExecutionCallback",
                        "AfterEachCallback",
                        "TestInstancePreDestroyCallback true",
                        "AfterAllCallback",
                        "Error: (IllegalStateException) problem"));
    }

    @Test
    void testParameterError() {
        Assertions.assertEquals(runTest(JupiterParameterError.class),
                Arrays.asList("BeforeAllCallback",
                        "BeforeEachCallback",
                        "BeforeTestExecutionCallback",
                        "AfterTestExecutionCallback",
                        "AfterEachCallback",
                        "AfterAllCallback",
                        "Error: (ParameterResolutionException) Failed to resolve parameter [java.lang.Integer arg0] in method [void playground.tests.exceptions.JupiterParameterError.sampleTest(java.lang.Integer)]: problem"
                ));
    }

    @Test
    void testParameterError2() {
        Assertions.assertEquals(runTest(JupiterParameterError2.class),
                Arrays.asList("BeforeAllCallback",
                        "BeforeEachCallback",
                        "BeforeTestExecutionCallback",
                        "ParameterExtension sampleTest",
                        "AfterTestExecutionCallback",
                        "AfterEachCallback",
                        "AfterAllCallback",
                        "Error: (ParameterResolutionException) Failed to resolve parameter [java.lang.Integer arg0] in method [void playground.tests.exceptions.JupiterParameterError2.sampleTest(java.lang.Integer)]: problem"
                ));
    }

    @Test
    void testConditionError() {
        Assertions.assertEquals(runTest(JupiterConditionError.class),
                Arrays.asList("Error: (ConditionEvaluationException) Failed to evaluate condition [ru.vyarus.spock.jupiter.support.exceptions.ConditionError]: problem"
                ));
    }

    @Test
    void testExceptionHandler() {
        Assertions.assertEquals(runTest(JupiterTestExceptionHandler.class),
                Arrays.asList("BeforeAllCallback",
                        "BeforeEachCallback",
                        "BeforeTestExecutionCallback",
                        "RethrowExceptionHandler problem",
                        "EatExceptionHandler problem",
                        "AfterTestExecutionCallback",
                        "AfterEachCallback",
                        "AfterAllCallback"
                ));
    }

    @Test
    void testExceptionHandlerForAssertion() {
        Assertions.assertEquals(runTest(JupiterTestExceptionHandler2.class),
                Arrays.asList("BeforeAllCallback",
                        "BeforeEachCallback",
                        "BeforeTestExecutionCallback",
                        "RethrowExceptionHandler assert fail",
                        "EatExceptionHandler assert fail",
                        "AfterTestExecutionCallback",
                        "AfterEachCallback",
                        "AfterAllCallback"
                ));
    }

    @Test
    void testExceptionHandlerRethrows() {
        Assertions.assertEquals(runTest(JupiterTestExceptionHandler3.class),
                Arrays.asList("BeforeAllCallback",
                        "BeforeEachCallback",
                        "BeforeTestExecutionCallback",
                        "RethrowExceptionHandler assert fail",
                        "AfterTestExecutionCallback",
                        "AfterEachCallback",
                        "AfterAllCallback",
                        "Error: (AssertionFailedError) assert fail"
                ));
    }

    @Test
    void testExecutableInvokerError() {
        Assertions.assertEquals(runTest(JupiterExecutableInvokerError.class),
                Collections.singletonList(
                        "Error: (ParameterResolutionException) No ParameterResolver registered for parameter [java.lang.Integer arg0] in constructor [public ru.vyarus.spock.jupiter.support.ExecutableInvokerUsage$Inst(java.lang.Integer)]."
                ));
    }
}
