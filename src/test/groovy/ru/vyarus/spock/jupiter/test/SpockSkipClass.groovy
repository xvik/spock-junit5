package ru.vyarus.spock.jupiter.test

import org.junit.jupiter.api.extension.ExtendWith
import ru.vyarus.spock.jupiter.AbstractTest
import ru.vyarus.spock.jupiter.support.ActionHolder
import ru.vyarus.spock.jupiter.support.SkipCondition
import spock.lang.Requires
import spock.lang.Specification

/**
 * @author Vyacheslav Rusakov
 * @since 27.12.2021
 */
@Requires({ AbstractTest.ACTIVE })
@ExtendWith(SkipCondition)
class SpockSkipClass extends Specification {

    def "Sample test"() {

        when:
        ActionHolder.add("test.body");

        then:
        true
    }
}
