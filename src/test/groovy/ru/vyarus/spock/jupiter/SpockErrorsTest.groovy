package ru.vyarus.spock.jupiter

import ru.vyarus.spock.jupiter.test.exception.*

/**
 * @author Vyacheslav Rusakov
 * @since 31.12.2021
 */
class SpockErrorsTest extends AbstractTest {

    def "Check before all error"() {

        expect: 'no test execution'
        runTest(SpockBeforeAllError) == ["BeforeAllCallback",
                                         "AfterAllCallback-2",
                                         "AfterAllCallback",
                                         "Error: (IllegalStateException) problem"]
    }

    def "Check before each error"() {

        expect: 'no test execution'
        runTest(SpockBeforeEachError) == ["BeforeAllCallback",
                                          "BeforeAllCallback-2",
                                          "BeforeEachCallback",
                                          "AfterEachCallback-2",
                                          "AfterEachCallback",
                                          "AfterAllCallback-2",
                                          "AfterAllCallback",
                                          "Error: (IllegalStateException) problem"]
    }

    def "Check after each error"() {

        expect: 'test and all callbacks executed'
        runTest(SpockAfterEachError) == ["BeforeAllCallback",
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
                                         "Error: (IllegalStateException) problem"]
    }

    def "Check after all error"() {

        expect: 'test and all callbacks executed'
        runTest(SpockAfterAllError) == ["BeforeAllCallback",
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
                                        "Error: (IllegalStateException) problem"]
    }

    def "Check test method error"() {

        expect: 'all callbacks executed'
        runTest(SpockTestError) == ["BeforeAllCallback",
                                    "BeforeEachCallback",
                                    "BeforeTestExecutionCallback",
                                    "AfterTestExecutionCallback",
                                    "AfterEachCallback",
                                    "AfterAllCallback",
                                    "Error: (IllegalStateException) problem"]
    }

    def "Check before test error"() {

        expect: 'test not executed'
        runTest(SpockBeforeTestError) == ["BeforeAllCallback",
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
                                          "Error: (IllegalStateException) problem"]
    }

    def "Check after test error"() {

        expect: 'test executed'
        runTest(SpockAfterTestError) == ["BeforeAllCallback",
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
                                         "Error: (IllegalStateException) problem"]
    }

    def "Check post processor error"() {

        expect: 'test not executed'
        runTest(SpockPostProcessError) == ["BeforeAllCallback",
                                           "AfterAllCallback",
                                           "Error: (IllegalStateException) problem",
                                           // this is correct because spock creates 2 failed events
                                           "Error: (IllegalStateException) problem"]
    }

    def "Check pre destroy error"() {

        expect: 'all callbacks executed'
        runTest(SpockPreDestroyError) == ["BeforeAllCallback",
                                          "BeforeEachCallback",
                                          "BeforeTestExecutionCallback",
                                          "test.body",
                                          "AfterTestExecutionCallback",
                                          "AfterEachCallback",
                                          "TestInstancePreDestroyCallback true",
                                          "AfterAllCallback",
                                          "Error: (IllegalStateException) problem"]
    }

    def "Check parameter error"() {

        expect: 'all callbacks executed'
        runTest(SpockParameterError) == ["BeforeAllCallback",
                                         "BeforeEachCallback",
                                         "BeforeTestExecutionCallback",
                                         "AfterTestExecutionCallback",
                                         "AfterEachCallback",
                                         "AfterAllCallback",
                                         "Error: (ParameterResolutionException) Failed to resolve parameter [java.lang.Integer arg0] in method [public void ru.vyarus.spock.jupiter.test.exception.SpockParameterError.\$spock_feature_0_0(java.lang.Integer)]: problem"
        ]
    }

    def "Check parameter error 2"() {

        expect: 'all callbacks executed'
        runTest(SpockParameterError2) == ["BeforeAllCallback",
                                          "BeforeEachCallback",
                                          "BeforeTestExecutionCallback",
                                          "ParameterExtension \$spock_feature_0_0",
                                          "AfterTestExecutionCallback",
                                          "AfterEachCallback",
                                          "AfterAllCallback",
                                          "Error: (ParameterResolutionException) Failed to resolve parameter [java.lang.Integer arg0] in method [public void ru.vyarus.spock.jupiter.test.exception.SpockParameterError2.\$spock_feature_0_0(java.lang.Integer)]: problem"
        ]
    }

    def "Check condition evaluation fail"() {

        expect: 'fail'
        runTest(SpockConditionError) == ["Error: (ConditionEvaluationException) Failed to evaluate condition [ru.vyarus.spock.jupiter.support.exceptions.ConditionError]: problem"
        ]
    }

    def "Check exception handler extension"() {

        expect: 'exception should be swallowed'
        runTest(SpockTestExceptionHandler) == ["BeforeAllCallback",
                                               "BeforeEachCallback",
                                               "BeforeTestExecutionCallback",
                                               "RethrowExceptionHandler problem",
                                               "SwallowExceptionHandler problem",
                                               "AfterTestExecutionCallback",
                                               "AfterEachCallback",
                                               "AfterAllCallback"]
    }

    def "Check exception handler on assertion"() {

        expect: 'assertion swallowed'
        runTest(SpockTestExceptionHandler2) == ["BeforeAllCallback",
                                                "BeforeEachCallback",
                                                "BeforeTestExecutionCallback",
                                                "RethrowExceptionHandler Condition not satisfied:\n\nfalse\n",
                                                "SwallowExceptionHandler Condition not satisfied:\n\nfalse\n",
                                                "AfterTestExecutionCallback",
                                                "AfterEachCallback",
                                                "AfterAllCallback"]
    }

    def "Check executable invoker error"() {

        expect: 'executable invoker call failed'
        runTest(SpockExecutableInvokerError) == ['Error: (ParameterResolutionException) No ParameterResolver registered for parameter [java.lang.Integer arg0] in constructor [public ru.vyarus.spock.jupiter.support.ExecutableInvokerUsage$Inst(java.lang.Integer)].']
    }

    def "Check no supported extension found"() {

        expect: 'error'
        runTest(SpockNotSupportedExtensions) == ["Error: (IllegalStateException) Extension ru.vyarus.spock.jupiter.test.exception.SpockNotSupportedExtensions\$NotSupportedExtension does not use any of supported extension types: ExecutionCondition, BeforeAllCallback, AfterAllCallback, BeforeEachCallback, AfterEachCallback, BeforeTestExecutionCallback, AfterTestExecutionCallback, ParameterResolver, TestInstancePostProcessor, TestInstancePreDestroyCallback, TestExecutionExceptionHandler"]
    }

    def "Check extension with both supported and not supported extensions"() {

        expect: 'only warning printed'
        runTest(SpockNotSupportedExtensions2) == ["test.body"]
    }
}
