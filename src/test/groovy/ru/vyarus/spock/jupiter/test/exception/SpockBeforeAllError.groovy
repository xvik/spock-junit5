package ru.vyarus.spock.jupiter.test.exception

import org.junit.jupiter.api.extension.ExtendWith
import ru.vyarus.spock.jupiter.AbstractTest
import ru.vyarus.spock.jupiter.support.ActionHolder
import ru.vyarus.spock.jupiter.support.LifecycleExtension
import ru.vyarus.spock.jupiter.support.LifecycleExtension2
import ru.vyarus.spock.jupiter.support.exceptions.BeforeAllError
import spock.lang.Requires
import spock.lang.Specification

/**
 * @author Vyacheslav Rusakov
 * @since 31.12.2021
 */
@Requires({ AbstractTest.ACTIVE })
@ExtendWith([LifecycleExtension.class, BeforeAllError.class, LifecycleExtension2.class])
class SpockBeforeAllError extends Specification {

    def "Sample test"() {

        when:
        ActionHolder.add("test.body");

        then:
        true
    }
}
