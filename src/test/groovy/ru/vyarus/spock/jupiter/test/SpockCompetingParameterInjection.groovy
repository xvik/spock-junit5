package ru.vyarus.spock.jupiter.test

import org.junit.jupiter.api.extension.ExtendWith
import ru.vyarus.spock.jupiter.support.ActionHolder
import ru.vyarus.spock.jupiter.support.ParameterExtension
import ru.vyarus.spock.jupiter.support.ParameterExtension2
import spock.lang.Specification

/**
 * @author Vyacheslav Rusakov
 * @since 02.01.2022
 */
@ExtendWith([ParameterExtension, ParameterExtension2])
class SpockCompetingParameterInjection extends Specification {

    def "Sample test"(Integer arg) {

        when:
        ActionHolder.add("test.body $arg");

        then:
        true
    }
}
