package ru.vyarus.spock.jupiter.test

import org.junit.jupiter.api.extension.ExtendWith
import ru.vyarus.spock.jupiter.AbstractTest
import ru.vyarus.spock.jupiter.support.ActionHolder
import ru.vyarus.spock.jupiter.support.SkipCondition
import spock.lang.Requires
import spock.lang.Specification
import spock.lang.Stepwise

/**
 * @author Vyacheslav Rusakov
 * @since 27.12.2021
 */
@Requires({ AbstractTest.ACTIVE })
@Stepwise // force strict execution order
class SpockSkipMethod extends Specification {

    @ExtendWith(SkipCondition)
    def "Sample test"() {

        when:
        ActionHolder.add("test.body")

        then:
        true
    }

    def "Sample test 2"() {

        when:
        ActionHolder.add("test.body2")

        then:
        true
    }
}
