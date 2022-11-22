package ru.vyarus.spock.jupiter


import ru.vyarus.spock.jupiter.test.SpockMultiStorageFirst
import ru.vyarus.spock.jupiter.test.SpockMultiStorageSecond
/**
 * @author Ken Davidson
 * @since 18.11.2022
 */
class SpockMultiStorageRepeatTest extends AbstractTest {

    def "root value updated only once"() {
        expect: 'context values ok'
        runTest(SpockMultiStorageFirst, SpockMultiStorageSecond) == ["Root value 1",
                                                                     "SpockMultiStorageFirst class value 2",
                                                                     "Sample test method value 3",
                                                                     "test.first",
                                                                     "Root value 1",
                                                                     "SpockMultiStorageSecond class value 1",
                                                                     "Sample test method value 2",
                                                                     "test.second"]
    }
}
