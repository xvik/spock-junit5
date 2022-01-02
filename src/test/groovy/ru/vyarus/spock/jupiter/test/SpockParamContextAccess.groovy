package ru.vyarus.spock.jupiter.test

import org.junit.jupiter.api.extension.ExtendWith
import ru.vyarus.spock.jupiter.AbstractTest
import ru.vyarus.spock.jupiter.support.ActionHolder
import ru.vyarus.spock.jupiter.support.ParamContextAccess
import spock.lang.Requires
import spock.lang.Specification

/**
 * @author Vyacheslav Rusakov
 * @since 03.01.2022
 */
@Requires({ AbstractTest.ACTIVE })
@ExtendWith(ParamContextAccess.class)
class SpockParamContextAccess extends Specification {

    def "Sample test"(@ExtendWith([]) Integer arg) {

        when:
        ActionHolder.add("test.body $arg");

        then:
        true
    }
}
