package ru.vyarus.spock.jupiter.test

import org.junit.jupiter.api.extension.ExtendWith
import ru.vyarus.spock.jupiter.support.ActionHolder
import ru.vyarus.spock.jupiter.support.ContextAccess
import spock.lang.Specification

/**
 * @author Vyacheslav Rusakov
 * @since 30.12.2021
 */
// @Ignore
@ExtendWith(ContextAccess)
class SpockContext extends Specification {

    def "Sample test"() {

        when:
        ActionHolder.add("test.body");

        then:
        true
    }
}
