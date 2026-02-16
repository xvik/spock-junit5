package ru.vyarus.spock.jupiter.debug.test

import org.junit.jupiter.api.extension.ExtendWith
import ru.vyarus.spock.jupiter.AbstractTest
import ru.vyarus.spock.jupiter.DebugJunitExtensions
import ru.vyarus.spock.jupiter.support.ActionHolder
import ru.vyarus.spock.jupiter.support.LifecycleExtension
import ru.vyarus.spock.jupiter.support.LifecycleExtension2
import spock.lang.Requires
import spock.lang.Specification

/**
 * @author Vyacheslav Rusakov
 * @since 15.02.2026
 */
@Requires({ AbstractTest.ACTIVE })
@DebugJunitExtensions
class SpockDebugFieldExtensions extends Specification {

    @ExtendWith(LifecycleExtension.class)
    static int a

    @ExtendWith(LifecycleExtension2.class)
    int b

    def "Sample test"() {

        when:
        ActionHolder.add("test.body");

        then:
        true
    }
}
