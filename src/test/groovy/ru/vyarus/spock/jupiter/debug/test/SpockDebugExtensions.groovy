package ru.vyarus.spock.jupiter.debug.test

import org.junit.jupiter.api.extension.ExtendWith
import ru.vyarus.spock.jupiter.AbstractTest
import ru.vyarus.spock.jupiter.DebugJunitExtensions
import ru.vyarus.spock.jupiter.support.ActionHolder
import ru.vyarus.spock.jupiter.support.LifecycleExtension
import ru.vyarus.spock.jupiter.support.LifecycleExtension2
import ru.vyarus.spock.jupiter.support.ParameterExtension
import spock.lang.Requires
import spock.lang.Specification

/**
 * @author Vyacheslav Rusakov
 * @since 15.02.2026
 */
@Requires({ AbstractTest.ACTIVE })
@ExtendWith([LifecycleExtension])
@DebugJunitExtensions
class SpockDebugExtensions extends Specification {

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

    @ExtendWith(LifecycleExtension2.class)
    def "Sample test"(@ExtendWith(ParameterExtension.class) Integer arg) {

        when:
        ActionHolder.add("test.body");

        then:
        true
    }
}
