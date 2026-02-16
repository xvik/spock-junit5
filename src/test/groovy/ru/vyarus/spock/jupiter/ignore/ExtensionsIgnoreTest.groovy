package ru.vyarus.spock.jupiter.ignore

import ru.vyarus.spock.jupiter.AbstractTest
import ru.vyarus.spock.jupiter.ignore.test.SpockIgnoreExtension
import ru.vyarus.spock.jupiter.ignore.test.SpockIgnoreExtensionByInstance

/**
 * @author Vyacheslav Rusakov
 * @since 16.02.2026
 */
class ExtensionsIgnoreTest extends AbstractTest {

    def "Check extensions ignore"() {

        expect:
        runTest(SpockIgnoreExtension) == [
                "BeforeAllCallback-2",
                "BeforeEachCallback-2",
                "BeforeTestExecutionCallback-2",
                "test.body",
                "AfterTestExecutionCallback-2",
                "AfterEachCallback-2",
                "AfterAllCallback-2"
        ]
    }

    def "Check filed extensions ignore"() {

        expect:
        runTest(SpockIgnoreExtensionByInstance) == [
                "BeforeEachCallback-2",
                "BeforeTestExecutionCallback-2",
                "test.body",
                "AfterTestExecutionCallback-2",
                "AfterEachCallback-2"
        ]
    }
}
