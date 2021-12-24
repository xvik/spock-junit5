package ru.vyarus.spock.jupiter.test

import org.junit.jupiter.api.extension.ExtendWith
import ru.vyarus.spock.jupiter.support.ActionHolder
import ru.vyarus.spock.jupiter.support.LifecycleExtension
import ru.vyarus.spock.jupiter.support.LifecycleExtension2
import ru.vyarus.spock.jupiter.support.ParameterExtension
import spock.lang.Specification

/**
 * @author Vyacheslav Rusakov
 * @since 25.12.2021
 */
//@Ignore
class SpockSetupAllMethodExtensions extends Specification {

    @ExtendWith(LifecycleExtension.class)
    void setupSpec(@ExtendWith([ParameterExtension.class, LifecycleExtension2.class]) Integer arg) {
        ActionHolder.add("test.beforeAll $arg");
    }

    def "Sample test"() {

        when:
        ActionHolder.add("test.body");

        then:
        true
    }
}
