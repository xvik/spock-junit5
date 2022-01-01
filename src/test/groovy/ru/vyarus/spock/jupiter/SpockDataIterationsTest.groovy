package ru.vyarus.spock.jupiter

import ru.vyarus.spock.jupiter.test.SpockDataIterations
import ru.vyarus.spock.jupiter.test.SpockDataIterationsInstanceHooks

/**
 * @author Vyacheslav Rusakov
 * @since 26.12.2021
 */
class SpockDataIterationsTest extends AbstractTest {

    def "Check iterations"() {

        expect: 'check correct iterations handling'
        runTest(SpockDataIterations) == ["BeforeAllCallback",
                                         "BeforeEachCallback",
                                         "BeforeEachCallback-3",
                                         "BeforeEachCallback-2",
                                         "BeforeTestExecutionCallback",
                                         "BeforeTestExecutionCallback-3",
                                         "BeforeTestExecutionCallback-2",
                                         "ParameterExtension \$spock_feature_0_0",
                                         "test.body 11 1",
                                         "AfterTestExecutionCallback-2",
                                         "AfterTestExecutionCallback-3",
                                         "AfterTestExecutionCallback",
                                         "AfterEachCallback-2",
                                         "AfterEachCallback-3",
                                         "AfterEachCallback",
                                         "BeforeEachCallback",
                                         "BeforeEachCallback-3",
                                         "BeforeEachCallback-2",
                                         "BeforeTestExecutionCallback",
                                         "BeforeTestExecutionCallback-3",
                                         "BeforeTestExecutionCallback-2",
                                         "ParameterExtension \$spock_feature_0_0",
                                         "test.body 11 2",
                                         "AfterTestExecutionCallback-2",
                                         "AfterTestExecutionCallback-3",
                                         "AfterTestExecutionCallback",
                                         "AfterEachCallback-2",
                                         "AfterEachCallback-3",
                                         "AfterEachCallback",
                                         "AfterAllCallback"]
    }

    def "Check instance hooks"() {

        expect: 'check instance hooks called every time'
        runTest(SpockDataIterationsInstanceHooks) == ["TestInstancePostProcessor true false",
                                                      "test.body 1",
                                                      "TestInstancePreDestroyCallback true",
                                                      "TestInstancePostProcessor true false",
                                                      "test.body 2",
                                                      "TestInstancePreDestroyCallback true"]
    }
}
