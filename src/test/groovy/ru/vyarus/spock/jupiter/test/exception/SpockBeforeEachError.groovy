package ru.vyarus.spock.jupiter.test.exception

import org.junit.jupiter.api.extension.ExtendWith
import ru.vyarus.spock.jupiter.AbstractTest
import ru.vyarus.spock.jupiter.support.ActionHolder
import ru.vyarus.spock.jupiter.support.LifecycleExtension
import ru.vyarus.spock.jupiter.support.LifecycleExtension2
import ru.vyarus.spock.jupiter.support.exceptions.BeforeEachError
import spock.lang.Requires
import spock.lang.Specification

/**
 * @author Vyacheslav Rusakov
 * @since 31.12.2021
 */
@Requires({ AbstractTest.ACTIVE })
@ExtendWith([LifecycleExtension.class, BeforeEachError.class, LifecycleExtension2.class])
class SpockBeforeEachError extends Specification {

    def "Sample test"() {

        when:
        ActionHolder.add("test.body");

        then:
        true
    }
}
