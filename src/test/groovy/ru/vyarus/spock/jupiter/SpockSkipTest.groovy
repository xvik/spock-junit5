package ru.vyarus.spock.jupiter

import playground.tests.JupiterSkipMethod
import ru.vyarus.spock.jupiter.test.SpockSkipClass
import ru.vyarus.spock.jupiter.test.SpockSkipMethod

/**
 * @author Vyacheslav Rusakov
 * @since 27.12.2021
 */
class SpockSkipTest extends AbstractTest {

    def "Check class skip"() {

        expect: 'class skipped'
        runTest(SpockSkipClass) == ["SkipCondition"]
    }

    def "Check method skip"() {

        expect: 'method skipped'
        runTestWithVerification(JupiterSkipMethod, SpockSkipMethod)
                == ["SkipCondition",
                    "test.body2"]
    }
}
