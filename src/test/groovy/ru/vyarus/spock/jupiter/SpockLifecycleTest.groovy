package ru.vyarus.spock.jupiter

import ru.vyarus.spock.jupiter.test.SpockBaseLifecycle
import ru.vyarus.spock.jupiter.test.SpockDoubleLifecycle

/**
 * @author Vyacheslav Rusakov
 * @since 26.11.2021
 */
class SpockLifecycleTest extends AbstractTest {

    def "Test base lifecycle"() {

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

    def "Test double lifecycle"() {

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
}
