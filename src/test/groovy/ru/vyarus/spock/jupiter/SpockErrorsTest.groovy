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
}
