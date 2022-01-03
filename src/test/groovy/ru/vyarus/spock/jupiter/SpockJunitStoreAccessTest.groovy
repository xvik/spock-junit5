package ru.vyarus.spock.jupiter


import ru.vyarus.spock.jupiter.test.SpockJunitStateAccess

/**
 * @author Vyacheslav Rusakov
 * @since 03.01.2022
 */
class SpockJunitStoreAccessTest extends AbstractTest {

    def "Check junit context access from spock extension"() {

        expect: 'correct storages'
        runTest(SpockJunitStateAccess) == ["SpockStoreExtension",
                                           "SpockInterceptor.sharedInit null",
                                           "JunitExt.beforeAll 11",
                                           "SpockInterceptor.setupAll 11",
                                           "SpockInterceptor.init 11",
                                           "JunitExt.beforeEach 12",
                                           "SpockInterceptor.setup 12",
                                           "test.body",
                                           "SpockInterceptor.cleanup 12",
                                           "SpockInterceptor.cleanupAll 11"]
    }
}
