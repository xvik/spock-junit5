package ru.vyarus.spock.jupiter


import ru.vyarus.spock.jupiter.test.SpockInstanceHooks

/**
 * @author Vyacheslav Rusakov
 * @since 27.12.2021
 */
class SpockInstanceHooksTest extends AbstractTest {

    def "Check instance hooks"() {

        expect: 'instance hooks correct execution'
        runTest(SpockInstanceHooks) == ["TestInstancePostProcessor true false",
                                        "BeforeTestExecutionCallback",
                                        "test.body",
                                        "AfterTestExecutionCallback",
                                        "TestInstancePreDestroyCallback true"]
    }
}
