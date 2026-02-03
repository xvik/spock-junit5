package ru.vyarus.spock.jupiter

import playground.tests.exceptions.JupiterAfterAllMethodExceptionHandler
import playground.tests.exceptions.JupiterAfterAllMethodExceptionHandler2
import playground.tests.exceptions.JupiterAfterEachMethodExceptionHandler
import playground.tests.exceptions.JupiterAfterEachMethodExceptionHandler2
import playground.tests.exceptions.JupiterBeforeAllMethodExceptionHandler
import playground.tests.exceptions.JupiterBeforeAllMethodExceptionHandler2
import playground.tests.exceptions.JupiterBeforeEachMethodExceptionHandler
import playground.tests.exceptions.JupiterBeforeEachMethodExceptionHandler2
import ru.vyarus.spock.jupiter.test.exception.SpockAfterAllMethodExceptionHandler
import ru.vyarus.spock.jupiter.test.exception.SpockAfterAllMethodExceptionHandler2
import ru.vyarus.spock.jupiter.test.exception.SpockAfterEachMethodExceptionHandler
import ru.vyarus.spock.jupiter.test.exception.SpockAfterEachMethodExceptionHandler2
import ru.vyarus.spock.jupiter.test.exception.SpockBeforeAllMethodExceptionHandler
import ru.vyarus.spock.jupiter.test.exception.SpockBeforeAllMethodExceptionHandler2
import ru.vyarus.spock.jupiter.test.exception.SpockBeforeEachMethodExceptionHandler
import ru.vyarus.spock.jupiter.test.exception.SpockBeforeEachMethodExceptionHandler2

/**
 * @author Vyacheslav Rusakov
 * @since 03.02.2026
 */
class SpockMethodErrorsTest extends AbstractTest {

    def "Check before all error handling"() {

        expect: 'test and all callbacks executed'
        runTestWithVerification(JupiterBeforeAllMethodExceptionHandler, SpockBeforeAllMethodExceptionHandler)
                == ["BeforeAllCallback",
                    "BeforeAllExceptionHandler problem",
                    "BeforeAllEatExceptionHandler problem",
                    "BeforeEachCallback",
                    "BeforeTestExecutionCallback",
                    "test.body",
                    "AfterTestExecutionCallback",
                    "AfterEachCallback",
                    "AfterAllCallback"]
    }

    def "Check before all error rethrow"() {

        expect: 'test and all callbacks executed'
        runTestWithVerification(JupiterBeforeAllMethodExceptionHandler2, SpockBeforeAllMethodExceptionHandler2)
                == ["BeforeAllCallback",
                    "BeforeAllExceptionHandler problem",
                    "AfterAllCallback",
                    "Error: (IllegalStateException) problem"]
    }

    def "Check before each error handling"() {

        expect: 'test and all callbacks executed'
        runTestWithVerification(JupiterBeforeEachMethodExceptionHandler, SpockBeforeEachMethodExceptionHandler)
                == ["BeforeAllCallback",
                    "BeforeEachCallback",
                    "BeforeEachExceptionHandler problem",
                    "BeforeEachEatExceptionHandler problem",
                    "BeforeTestExecutionCallback",
                    "test.body",
                    "AfterTestExecutionCallback",
                    "AfterEachCallback",
                    "AfterAllCallback"]
    }

    def "Check before each error rethrow"() {

        expect: 'test and all callbacks executed'
        runTestWithVerification(JupiterBeforeEachMethodExceptionHandler2, SpockBeforeEachMethodExceptionHandler2)
                == ["BeforeAllCallback",
                    "BeforeEachCallback",
                    "BeforeEachExceptionHandler problem",
                    "AfterEachCallback",
                    "AfterAllCallback",
                    "Error: (IllegalStateException) problem"]
    }

    def "Check after each error handling"() {

        expect: 'test and all callbacks executed'
        runTestWithVerification(JupiterAfterEachMethodExceptionHandler, SpockAfterEachMethodExceptionHandler)
                == ["BeforeAllCallback",
                    "BeforeEachCallback",
                    "BeforeTestExecutionCallback",
                    "test.body",
                    "AfterTestExecutionCallback",
                    "AfterEachExceptionHandler problem",
                    "AfterEachEatExceptionHandler problem",
                    "AfterEachCallback",
                    "AfterAllCallback"]
    }

    def "Check after each error rethrow"() {

        expect: 'test and all callbacks executed'
        runTestWithVerification(JupiterAfterEachMethodExceptionHandler2, SpockAfterEachMethodExceptionHandler2)
                == ["BeforeAllCallback",
                    "BeforeEachCallback",
                    "BeforeTestExecutionCallback",
                    "test.body",
                    "AfterTestExecutionCallback",
                    "AfterEachExceptionHandler problem",
                    "AfterEachCallback",
                    "AfterAllCallback",
                    "Error: (IllegalStateException) problem"]
    }

    def "Check after all error handling"() {

        expect: 'test and all callbacks executed'
        runTestWithVerification(JupiterAfterAllMethodExceptionHandler, SpockAfterAllMethodExceptionHandler)
                == ["BeforeAllCallback",
                    "BeforeEachCallback",
                    "BeforeTestExecutionCallback",
                    "test.body",
                    "AfterTestExecutionCallback",
                    "AfterEachCallback",
                    "AfterAllExceptionHandler problem",
                    "AfterAllEatExceptionHandler problem",
                    "AfterAllCallback"]
    }

    def "Check after all error rethrow"() {

        expect: 'test and all callbacks executed'
        runTestWithVerification(JupiterAfterAllMethodExceptionHandler2, SpockAfterAllMethodExceptionHandler2)
                == ["BeforeAllCallback",
                    "BeforeEachCallback",
                    "BeforeTestExecutionCallback",
                    "test.body",
                    "AfterTestExecutionCallback",
                    "AfterEachCallback",
                    "AfterAllExceptionHandler problem",
                    "AfterAllCallback",
                    "Error: (IllegalStateException) problem"]
    }

}
