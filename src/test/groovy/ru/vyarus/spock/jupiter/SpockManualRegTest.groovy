package ru.vyarus.spock.jupiter

import playground.tests.JupiterManualRegistration
import playground.tests.exceptions.JupiterInvalidManualRegistration
import ru.vyarus.spock.jupiter.test.SpockInvalidManualRegistration
import ru.vyarus.spock.jupiter.test.SpockInvalidManualRegistration2
import ru.vyarus.spock.jupiter.test.SpockManualRegistration

/**
 * @author Vyacheslav Rusakov
 * @since 26.12.2021
 */
class SpockManualRegTest extends AbstractTest {

    def "Check manual registration"() {

        expect: 'manually registered extensions recognized'
        runTestWithVerification(JupiterManualRegistration, SpockManualRegistration)
                == ["BeforeAllCallback",
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
                    "AfterAllCallback"]
    }

    def "Check incorrect registration"() {

        expect: 'manually registered extensions clash'
        runTestWithVerification(JupiterInvalidManualRegistration, SpockInvalidManualRegistration,
                "ru.vyarus.spock.jupiter.support.LifecycleExtension playground.tests.exceptions.JupiterInvalidManualRegistration",
                "private ru.vyarus.spock.jupiter.support.LifecycleExtension ru.vyarus.spock.jupiter.test.SpockInvalidManualRegistration")

                == ["BeforeAllCallback",
                    "AfterAllCallback",
                    "Error: (PreconditionViolationException) Failed to register extension via field [private ru.vyarus.spock.jupiter.support.LifecycleExtension ru.vyarus.spock.jupiter.test.SpockInvalidManualRegistration.ext]. The field registers an extension of type [ru.vyarus.spock.jupiter.support.LifecycleExtension] via @RegisterExtension and @ExtendWith, but only one registration of a given extension type is permitted."]
    }

    def "Check incorrect registration 2"() {

        expect: 'incorrect extension type'
        runTest(SpockInvalidManualRegistration2)
                == ["Error: (PreconditionViolationException) Failed to register extension via @RegisterExtension field [private java.lang.Integer ru.vyarus.spock.jupiter.test.SpockInvalidManualRegistration2.ext]: field value's type [java.lang.Integer] must implement an [org.junit.jupiter.api.extension.Extension] API."]
    }
}
