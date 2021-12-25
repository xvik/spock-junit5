package ru.vyarus.spock.jupiter

import ru.vyarus.spock.jupiter.test.SpockDoubleLifecycle
import ru.vyarus.spock.jupiter.test.SpockManualRegistration

/**
 * @author Vyacheslav Rusakov
 * @since 26.12.2021
 */
class SpockManualRegTest extends AbstractTest {

    def "Check manual registration"() {

        expect: 'manually registered extensions recognized'
        runTest(SpockManualRegistration) == ["BeforeAllCallback",
                                             "BeforeEachCallback",
                                             "BeforeEachCallback-2",
                                             "BeforeTestExecutionCallback",
                                             "BeforeTestExecutionCallback-2",
                                             "test.body",
                                             "AfterTestExecutionCallback-2",
                                             "AfterTestExecutionCallback",
                                             "AfterEachCallback-2",
                                             "AfterEachCallback",
                                             "AfterAllCallback"]
    }

}
