package ru.vyarus.spock.jupiter.test

import org.junit.jupiter.api.extension.ExtendWith
import org.junit.jupiter.api.extension.RegisterExtension
import ru.vyarus.spock.jupiter.AbstractTest
import ru.vyarus.spock.jupiter.support.ActionHolder
import ru.vyarus.spock.jupiter.support.LifecycleExtension
import spock.lang.Requires
import spock.lang.Specification

/**
 * @author Vyacheslav Rusakov
 * @since 02.01.2022
 */
@Requires({ AbstractTest.ACTIVE })
class SpockInvalidManualRegistration extends Specification {

    @ExtendWith(LifecycleExtension)
    @RegisterExtension
    LifecycleExtension ext = new LifecycleExtension()

    def "Sample test"() {

        when:
        ActionHolder.add("test.body");

        then:
        true
    }
}
