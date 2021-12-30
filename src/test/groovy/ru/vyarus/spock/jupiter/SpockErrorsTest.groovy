package ru.vyarus.spock.jupiter

import ru.vyarus.spock.jupiter.test.exception.SpockAfterAllError
import ru.vyarus.spock.jupiter.test.exception.SpockAfterEachError
import ru.vyarus.spock.jupiter.test.exception.SpockBeforeAllError
import ru.vyarus.spock.jupiter.test.exception.SpockBeforeEachError
import ru.vyarus.spock.jupiter.test.exception.SpockTestError

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
}
