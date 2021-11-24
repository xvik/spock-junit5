package ru.vyarus.spock.jupiter;

/**
 * Dummy test.
 *
 * @author Vyacheslav Rusakov
 * @since 25.11.2021
 */
class DummyTest extends AbstractTest {

    def "Check something important"() {

        when: "do something"
        Integer checkAssignment = 1
        then: "check result"
        checkAssignment == 1
    }
}
