package ru.vyarus.spock.jupiter.test


import org.junit.jupiter.api.extension.ExtendWith
import ru.vyarus.spock.jupiter.AbstractTest
import ru.vyarus.spock.jupiter.support.ActionHolder
import ru.vyarus.spock.jupiter.support.LifecycleExtension
import spock.lang.Requires
import spock.lang.Specification

/**
 * @author Vyacheslav Rusakov
 * @since 23.12.2021
 */
@Requires({ AbstractTest.ACTIVE })
@ExtendWith(LifecycleExtension)
class SpockBaseLifecycle extends Specification {

    void setupSpec() {
        ActionHolder.add("test.beforeAll");
    }

    void cleanupSpec() {
        ActionHolder.add("test.afterAll");
    }

    void setup() {
        ActionHolder.add("test.before");
    }

    void cleanup() {
        ActionHolder.add("test.after");
    }

    def "Sample test"() {

        when:
        ActionHolder.add("test.body");

        then:
        true
    }
}
