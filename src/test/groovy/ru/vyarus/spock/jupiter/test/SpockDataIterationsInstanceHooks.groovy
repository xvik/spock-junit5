package ru.vyarus.spock.jupiter.test

import org.junit.jupiter.api.extension.ExtendWith
import ru.vyarus.spock.jupiter.AbstractTest
import ru.vyarus.spock.jupiter.support.ActionHolder
import ru.vyarus.spock.jupiter.support.PostConstructExtension
import ru.vyarus.spock.jupiter.support.PreDestroyExtension
import spock.lang.Requires
import spock.lang.Specification

/**
 * @author Vyacheslav Rusakov
 * @since 01.01.2022
 */
@Requires({ AbstractTest.ACTIVE })
@ExtendWith([PostConstructExtension, PreDestroyExtension])
class SpockDataIterationsInstanceHooks extends Specification {

    def "Sample test"() {

        when:
        ActionHolder.add("test.body $a");

        then:
        true

        where:
        a | _
        1 | _
        2 | _
    }
}
