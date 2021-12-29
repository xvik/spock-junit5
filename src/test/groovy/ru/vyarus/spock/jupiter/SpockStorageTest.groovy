package ru.vyarus.spock.jupiter


import ru.vyarus.spock.jupiter.test.SpockStorage

/**
 * @author Vyacheslav Rusakov
 * @since 30.12.2021
 */
class SpockStorageTest extends AbstractTest {

    def "Check storage behaviour"() {

        expect: 'storage ok'
        runTest(SpockStorage) == ["BeforeAllCallback null",
                                  "TestInstancePostProcessor 12",
                                  "BeforeEachCallback 12 null",
                                  "BeforeTestExecutionCallback 12 11",
                                  "test.body",
                                  "AfterEachCallback 12 11",
                                  "local value closed",
                                  "AfterAllCallback 12",
                                  "global value closed"]
    }
}
