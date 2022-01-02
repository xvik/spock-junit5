package ru.vyarus.spock.jupiter

import ru.vyarus.spock.jupiter.test.*

/**
 * @author Vyacheslav Rusakov
 * @since 26.11.2021
 */
class SpockLifecycleTest extends AbstractTest {

    def "Check base lifecycle"() {

        expect: 'correct lifecycle order'
        runTest(SpockBaseLifecycle) == ["BeforeAllCallback",
                                        "test.beforeAll",
                                        "BeforeEachCallback",
                                        "test.before",
                                        "BeforeTestExecutionCallback",
                                        "test.body",
                                        "AfterTestExecutionCallback",
                                        "test.after",
                                        "AfterEachCallback",
                                        "test.afterAll",
                                        "AfterAllCallback"]
    }

    def "Check double lifecycle"() {

        expect: 'correct lifecycle order for two extensions'
        runTest(SpockDoubleLifecycle) == ["BeforeAllCallback",
                                          "BeforeAllCallback-2",
                                          "test.beforeAll",
                                          "BeforeEachCallback",
                                          "BeforeEachCallback-2",
                                          "test.before",
                                          "BeforeTestExecutionCallback",
                                          "BeforeTestExecutionCallback-2",
                                          "test.body",
                                          "AfterTestExecutionCallback-2",
                                          "AfterTestExecutionCallback",
                                          "test.after",
                                          "AfterEachCallback-2",
                                          "AfterEachCallback",
                                          "test.afterAll",
                                          "AfterAllCallback-2",
                                          "AfterAllCallback"]
    }

    def "Check test method extensions"() {

        expect: 'extensions registered from method and parameter'
        runTest(SpockTestMethodExtensions) == ["BeforeEachCallback",
                                               "BeforeEachCallback-2",
                                               "BeforeTestExecutionCallback",
                                               "BeforeTestExecutionCallback-2",
                                               "ParameterExtension \$spock_feature_0_0",
                                               "test.body 11",
                                               "AfterTestExecutionCallback-2",
                                               "AfterTestExecutionCallback",
                                               "AfterEachCallback-2",
                                               "AfterEachCallback"]
    }

    def "Check setupAll method extensions"() {

        expect: 'extensions registered only from method parameter'
        runTest(SpockSetupAllMethodExtensions) == ["BeforeAllCallback-2",
                                                   "ParameterExtension setupSpec",
                                                   "test.beforeAll 11",
                                                   "BeforeEachCallback-2",
                                                   "BeforeTestExecutionCallback-2",
                                                   "test.body",
                                                   "AfterTestExecutionCallback-2",
                                                   "AfterEachCallback-2",
                                                   "AfterAllCallback-2"]
    }

    def "Check setup method extensions"() {

        expect: 'extensions registered only from method parameter'
        runTest(SpockSetupMethodExtensions) == ["BeforeAllCallback-2",
                                                "BeforeEachCallback-2",
                                                "ParameterExtension setup",
                                                "test.before 11",
                                                "BeforeTestExecutionCallback-2",
                                                "test.body",
                                                "AfterTestExecutionCallback-2",
                                                "AfterEachCallback-2",
                                                "AfterAllCallback-2"]
    }

    def "Check field extensions"() {

        expect: 'field extensions recognized'
        runTest(SpockFieldExtensions) == ["BeforeAllCallback",
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

    def "Check spock lifecycle order"() {

        expect: 'executed'
        runTest(SpockLifecyclesOrder) == ["SpockLifecycleExtension",
                                          "BeforeAllCallback",
                                          "test.beforeAll",
                                          "TestInstancePostProcessor true false",
                                          "SpockLifecycleExtension Sample test",
                                          "BeforeEachCallback",
                                          "test.before",
                                          "BeforeTestExecutionCallback",
                                          "test.body",
                                          "AfterTestExecutionCallback",
                                          "test.after",
                                          "AfterEachCallback",
                                          "TestInstancePreDestroyCallback true",
                                          "test.afterAll",
                                          "AfterAllCallback"]
    }
}
