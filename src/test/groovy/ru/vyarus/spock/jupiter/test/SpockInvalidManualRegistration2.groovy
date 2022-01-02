package ru.vyarus.spock.jupiter.test


import org.junit.jupiter.api.extension.RegisterExtension
import ru.vyarus.spock.jupiter.AbstractTest
import ru.vyarus.spock.jupiter.support.ActionHolder
import spock.lang.Requires
import spock.lang.Specification

/**
 * @author Vyacheslav Rusakov
 * @since 02.01.2022
 */
@Requires({ AbstractTest.ACTIVE })
class SpockInvalidManualRegistration2 extends Specification {

    @RegisterExtension
    Integer ext = 12

    def "Sample test"() {

        when:
        ActionHolder.add("test.body");

        then:
        true
    }
}
