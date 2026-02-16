package ru.vyarus.spock.jupiter.ignore.test

import org.junit.jupiter.api.extension.ExtendWith
import ru.vyarus.spock.jupiter.AbstractTest
import ru.vyarus.spock.jupiter.DebugJunitExtensions
import ru.vyarus.spock.jupiter.IgnoreJunitExtensions
import ru.vyarus.spock.jupiter.support.ActionHolder
import ru.vyarus.spock.jupiter.support.LifecycleExtension
import ru.vyarus.spock.jupiter.support.LifecycleExtension2
import spock.lang.Requires
import spock.lang.Specification

/**
 * @author Vyacheslav Rusakov
 * @since 16.02.2026
 */
@Requires({ AbstractTest.ACTIVE })
@ExtendWith([LifecycleExtension.class, LifecycleExtension2.class])
@IgnoreJunitExtensions(LifecycleExtension.class)
@DebugJunitExtensions
class SpockIgnoreExtension extends Specification {

    def "Sample test"() {

        when:
        ActionHolder.add("test.body");

        then:
        true
    }
}
