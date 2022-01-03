package ru.vyarus.spock.jupiter


import ru.vyarus.spock.jupiter.test.SpockJunitStateAccess

/**
 * @author Vyacheslav Rusakov
 * @since 03.01.2022
 */
class SpockJunitStoreAccessTest extends AbstractTest {

    def "Check junit context access from spock extension"() {

        expect: 'correct storages'
        runTest(SpockJunitStateAccess) == ["spock.visitSpecAnnotation",
                                           "spock.SHARED_INITIALIZER (shared initializer) null",
                                           "spock.SPEC_EXECUTION (specification) null",
                                           "JunitExt.beforeAll 11",
                                           "spock.SETUP_SPEC (setup spec) 11",
                                           "spock.INITIALIZER (initializer) 11",
                                           "spock.FEATURE_EXECUTION (feature) 11",
                                           "spock.ITERATION_EXECUTION (iteration) 11",
                                           "JunitExt.beforeEach 12",
                                           "spock.SETUP (setup) 12",
                                           "spock.FEATURE (feature method) 12",
                                           "test.body",
                                           "spock.CLEANUP (cleanup) 12",
                                           "spock.CLEANUP_SPEC (cleanup spec) 11"]
    }
}
