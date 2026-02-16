package ru.vyarus.spock.jupiter.debug

import io.github.joke.spockoutputcapture.CapturedOutput
import io.github.joke.spockoutputcapture.OutputCapture
import ru.vyarus.spock.jupiter.AbstractTest
import ru.vyarus.spock.jupiter.debug.test.SpockDebugExtensions
import ru.vyarus.spock.jupiter.debug.test.SpockDebugFieldExtensions
import ru.vyarus.spock.jupiter.debug.test.SpockDebugPrePostConstructExtensions
import ru.vyarus.spock.jupiter.debug.test.SpockDebugSetupError
import ru.vyarus.spock.jupiter.debug.test.SpockDebugTestError

/**
 * @author Vyacheslav Rusakov
 * @since 15.02.2026
 */
class ExtensionsDebugTest extends AbstractTest {

    @OutputCapture CapturedOutput logs

    def "Check extensions debug"() {

        expect:
        runTest(SpockDebugExtensions) == [
                "BeforeAllCallback",
                "test.beforeAll",
                "BeforeEachCallback",
                "BeforeEachCallback-2",
                "test.before",
                "BeforeTestExecutionCallback",
                "BeforeTestExecutionCallback-2",
                "ParameterExtension \$spock_feature_0_0",
                "test.body",
                "AfterTestExecutionCallback-2",
                "AfterTestExecutionCallback",
                "test.after",
                "AfterEachCallback-2",
                "AfterEachCallback",
                "test.afterAll",
                "AfterAllCallback"
        ]

        logs ==~ /(?s).*\[junit] Registered test class \(SpockDebugExtensions\) extensions: LifecycleExtension.*/
        logs ==~ /(?s).*\[junit] BeforeAllCallback extensions called: LifecycleExtension.*/
        logs ==~ /(?s).*\[junit] Registered test method \(SpockDebugExtensions.Sample test\) extensions: LifecycleExtension2.*/
        logs ==~ /(?s).*\[junit] Registered test method \(SpockDebugExtensions.Sample test\) parameters extensions: ParameterExtension.*/
        logs ==~ /(?s).*\[junit] BeforeEachCallback extensions called: LifecycleExtension, LifecycleExtension2.*/
        logs ==~ /(?s).*\[junit] BeforeTestExecutionCallback extensions called: LifecycleExtension, LifecycleExtension2.*/
        logs ==~ /(?s).*\[junit] ParameterResolver extensions called: ParameterExtension.*/
        logs ==~ /(?s).*\[junit] AfterTestExecutionCallback extensions called: LifecycleExtension2, LifecycleExtension.*/
        logs ==~ /(?s).*\[junit] AfterEachCallback extensions called: LifecycleExtension2, LifecycleExtension.*/
        logs ==~ /(?s).*\[junit] AfterAllCallback extensions called: LifecycleExtension.*/
    }

    def "Check extensions on fields debug"() {

        expect:
        runTest(SpockDebugFieldExtensions) == [
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
                "AfterAllCallback"
        ]

        logs ==~ /(?s).*\[junit] Registered static field \(SpockDebugFieldExtensions.a\) extensions: LifecycleExtension.*/
        logs ==~ /(?s).*\[junit] Registered field \(SpockDebugFieldExtensions.b\) extensions: LifecycleExtension2.*/
        logs ==~ /(?s).*\[junit] BeforeAllCallback extensions called: LifecycleExtension, LifecycleExtension2.*/
        logs ==~ /(?s).*\[junit] BeforeEachCallback extensions called: LifecycleExtension, LifecycleExtension2.*/
        logs ==~ /(?s).*\[junit] BeforeTestExecutionCallback extensions called: LifecycleExtension, LifecycleExtension2.*/
        logs ==~ /(?s).*\[junit] AfterTestExecutionCallback extensions called: LifecycleExtension2, LifecycleExtension.*/
        logs ==~ /(?s).*\[junit] AfterEachCallback extensions called: LifecycleExtension2, LifecycleExtension.*/
        logs ==~ /(?s).*\[junit] AfterAllCallback extensions called: LifecycleExtension2, LifecycleExtension.*/
    }

    def "Check pre/post construct debug"() {

        expect:
        runTest(SpockDebugPrePostConstructExtensions) == [
                "TestInstancePostProcessor true false",
                "test.body", 
                "TestInstancePreDestroyCallback true"
        ]

        logs ==~ /(?s).*\[junit] Registered test class \(SpockDebugPrePostConstructExtensions\) extensions: PostConstructExtension, PreDestroyExtension.*/
        logs ==~ /(?s).*\[junit] TestInstancePostProcessor extensions called: PostConstructExtension.*/
        logs ==~ /(?s).*\[junit] TestInstancePreDestroyCallback extensions called: PreDestroyExtension.*/
    }

    def "Check test method exception handler debug"() {

        expect:
        runTest(SpockDebugTestError) == [
                "RethrowExceptionHandler problem",
                "EatExceptionHandler problem"
        ]

        logs ==~ /(?s).*\[junit] Registered test class \(SpockDebugTestError\) extensions: EatExceptionHandler, RethrowExceptionHandler.*/
        logs ==~ /(?s).*\[junit] TestExecutionExceptionHandler extensions called: RethrowExceptionHandler, EatExceptionHandler.*/
    }

    def "Check setup method exception handler debug"() {

        expect:
        runTest(SpockDebugSetupError) == [
                "BeforeAllExceptionHandler problem",
                "BeforeAllEatExceptionHandler problem",
                "test.body"
        ]

        logs ==~ /(?s).*\[junit] Registered test class \(SpockDebugSetupError\) extensions: EatLifecycleMethodExceptionHandler, RethrowLifecycleMethodsExceptionHandler.*/
        logs ==~ /(?s).*\[junit] LifecycleMethodExecutionExceptionHandler extensions called: RethrowLifecycleMethodsExceptionHandler, EatLifecycleMethodExceptionHandler.*/
    }
}
