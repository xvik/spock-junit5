package ru.vyarus.spock.jupiter.test

import org.junit.jupiter.api.extension.ExtendWith
import ru.vyarus.spock.jupiter.support.ActionHolder
import ru.vyarus.spock.jupiter.support.LifecycleExtension
import ru.vyarus.spock.jupiter.support.ParameterExtension
import spock.lang.Specification

/**
 * @author Vyacheslav Rusakov
 * @since 23.12.2021
 */
//@Ignore
@ExtendWith([LifecycleExtension, ParameterExtension])
class SpockParameterTest extends Specification {

    void setupSpec(Integer arg) {
        ActionHolder.add("test.beforeAll $arg");
    }

    void cleanupSpec(Integer arg) {
        ActionHolder.add("test.afterAll $arg");
    }

    void setup(Integer arg) {
        ActionHolder.add("test.before $arg");
    }

    void cleanup(Integer arg) {
        ActionHolder.add("test.after $arg");
    }

    def "Sample test"(Integer arg) {

        when:
        ActionHolder.add("test.body $arg");

        then:
        true
    }
}
