package ru.vyarus.spock.jupiter.debug.test

import org.junit.jupiter.api.extension.ExtendWith
import ru.vyarus.spock.jupiter.AbstractTest
import ru.vyarus.spock.jupiter.DebugJunitExtensions
import ru.vyarus.spock.jupiter.support.ActionHolder
import ru.vyarus.spock.jupiter.support.PostConstructExtension
import ru.vyarus.spock.jupiter.support.PreDestroyExtension
import spock.lang.Requires
import spock.lang.Specification

/**
 * @author Vyacheslav Rusakov
 * @since 16.02.2026
 */
@Requires({ AbstractTest.ACTIVE })
@ExtendWith([PostConstructExtension, PreDestroyExtension])
@DebugJunitExtensions
class SpockDebugPrePostConstructExtensions extends Specification {

    def "Sample test"() {

        when:
        ActionHolder.add("test.body");

        then:
        true
    }
}
