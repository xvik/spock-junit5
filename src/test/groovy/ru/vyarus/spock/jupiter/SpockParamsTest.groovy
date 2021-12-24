package ru.vyarus.spock.jupiter

import ru.vyarus.spock.jupiter.test.SpockParameterInjection

/**
 * @author Vyacheslav Rusakov
 * @since 23.12.2021
 */
class SpockParamsTest extends AbstractTest {

    def "Test params injection"() {

        expect: 'params injected'
        runTest(SpockParameterInjection) == ["BeforeAllCallback",
                                             "ParameterExtension setupSpec",
                                             "test.beforeAll 11",
                                             "BeforeEachCallback",
                                             "ParameterExtension setup",
                                             "test.before 11",
                                             "BeforeTestExecutionCallback",
                                             "ParameterExtension \$spock_feature_0_0",
                                             "test.body 11",
                                             "AfterTestExecutionCallback",
                                             "ParameterExtension cleanup",
                                             "test.after 11",
                                             "AfterEachCallback",
                                             "ParameterExtension cleanupSpec",
                                             "test.afterAll 11",
                                             "AfterAllCallback"]
    }
}
