package ru.vyarus.spock.jupiter

import ru.vyarus.spock.jupiter.test.SpockCompetingParameterInjection
import ru.vyarus.spock.jupiter.test.SpockParameterInjection

/**
 * @author Vyacheslav Rusakov
 * @since 23.12.2021
 */
class SpockParamsTest extends AbstractTest {

    def "Check params injection"() {

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

    def "Check competing params injection"() {

        expect: 'params injection error'
        runTest(SpockCompetingParameterInjection) == ["ParameterExtension \$spock_feature_0_0",
                                                      "ParameterExtension2 \$spock_feature_0_0",
                                                      "Error: (ParameterResolutionException) Discovered multiple competing ParameterResolvers for parameter [java.lang.Integer arg0] in method [public void ru.vyarus.spock.jupiter.test.SpockCompetingParameterInjection.\$spock_feature_0_0(java.lang.Integer)]: ParameterExtension, ParameterExtension2"
        ]
    }
}
