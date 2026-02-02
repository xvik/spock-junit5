package ru.vyarus.spock.jupiter

import playground.tests.JupiterMultiStorageFirst
import playground.tests.JupiterMultiStorageSecond
import ru.vyarus.spock.jupiter.test.SpockMultiStorageFirst
import ru.vyarus.spock.jupiter.test.SpockMultiStorageSecond

/**
 * @author Ken Davidson
 * @since 18.11.2022
 */
class SpockMultiStorageRepeatTest extends AbstractTest {

    def "root value updated only once"() {
        expect: 'context values ok'
        runTestWithVerification([JupiterMultiStorageFirst, JupiterMultiStorageSecond] as Class[],
                [SpockMultiStorageFirst, SpockMultiStorageSecond] as Class[],
                "JupiterMultiStorageFirst class value 2", "SpockMultiStorageFirst class value 2",
                "sampleTest() method value 3", "Sample test method value 3",
                "JupiterMultiStorageSecond class value 1", "SpockMultiStorageSecond class value 1",
                "sampleTest() method value 2", "Sample test method value 2")

                == ["Root value 1",
                    "SpockMultiStorageFirst class value 2",
                    "Sample test method value 3",
                    "test.first",
                    "Root value 1",
                    "SpockMultiStorageSecond class value 1",
                    "Sample test method value 2",
                    "test.second"]
    }
}
