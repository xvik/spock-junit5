package ru.vyarus.spock.jupiter

import playground.tests.JupiterParamContextAccess
import playground.tests.JupiterParameterInjection
import ru.vyarus.spock.jupiter.test.*

/**
 * @author Vyacheslav Rusakov
 * @since 23.12.2021
 */
class SpockParamsTest extends AbstractTest {

    def "Check params injection"() {

        expect: 'params injected'
        runTestWithVerification(JupiterParameterInjection, SpockParameterInjection,
                "ParameterExtension beforeAll", "ParameterExtension setupSpec",
                "setUp", "setup",
                "sampleTest", "\$spock_feature_0_0",
                "tearDown", "cleanup",
                "ParameterExtension afterAll", "ParameterExtension cleanupSpec")

                == ["BeforeAllCallback",
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
        runTest(SpockCompetingParameterInjection)
                == ["ParameterExtension \$spock_feature_0_0",
                    "ParameterExtension2 \$spock_feature_0_0",
                    "Error: (ParameterResolutionException) Discovered multiple competing ParameterResolvers for parameter [java.lang.Integer arg0] in method [public void ru.vyarus.spock.jupiter.test.SpockCompetingParameterInjection.\$spock_feature_0_0(java.lang.Integer)]: ParameterExtension, ParameterExtension2"
        ]
    }

    def "Check spock extension used when no junit providers"() {

        expect: 'params injection done'
        runTest(SpockParameterInjectionNoProvider) == ["test.body 13"]
    }

    def "Check primitive param resolved to null"() {

        expect: 'value resolved as null'
        runTest(SpockParamInjectionNullPrimitive)
                == ["Error: (ParameterResolutionException) ParameterResolver [ru.vyarus.spock.jupiter.test.Param] resolved a null value for parameter [int arg0] in method [public void ru.vyarus.spock.jupiter.test.SpockParamInjectionNullPrimitive.\$spock_feature_0_0(int)], but a primitive of type [int] is required."]
    }

    def "Check wrong parameter type resolved"() {

        expect: 'value resolved with bad type'
        runTest(SpockParamInjectionWrongType)
                == ["Error: (ParameterResolutionException) ParameterResolver [ru.vyarus.spock.jupiter.test.ParamBadType] resolved a value of type [java.lang.Double] for parameter [java.lang.Integer arg0] in method [public void ru.vyarus.spock.jupiter.test.SpockParamInjectionWrongType.\$spock_feature_0_0(java.lang.Integer)], but a value assignment compatible with [java.lang.Integer] is required."]
    }

    def "Check param context"() {

        expect: 'context values ok'
        runTestWithVerification(JupiterParamContextAccess, SpockParamContextAccess,
                "sampleTest", "\$spock_feature_0_0",
                "JupiterParamContextAccess", "SpockParamContextAccess")

                == ["param.name arg0",
                    "param.exec \$spock_feature_0_0",
                    "param.index 0",
                    "param.target SpockParamContextAccess",
                    "param.annotation true true 1",
                    "test.body 12"
        ]
    }
}
