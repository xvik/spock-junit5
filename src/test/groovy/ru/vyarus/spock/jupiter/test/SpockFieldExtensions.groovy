package ru.vyarus.spock.jupiter.test

import org.junit.jupiter.api.extension.ExtendWith
import ru.vyarus.spock.jupiter.support.ActionHolder
import ru.vyarus.spock.jupiter.support.LifecycleExtension
import ru.vyarus.spock.jupiter.support.LifecycleExtension2
import spock.lang.Specification

/**
 * @author Vyacheslav Rusakov
 * @since 30.12.2021
 */
class SpockFieldExtensions extends Specification {

    @ExtendWith(LifecycleExtension.class)
    static int a

    @ExtendWith(LifecycleExtension2.class)
    int b

    def "Sample test"() {

        when:
        ActionHolder.add("test.body");

        then:
        true
    }
}
