package ru.vyarus.spock.jupiter.ignore.test


import org.junit.jupiter.api.extension.RegisterExtension
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
@IgnoreJunitExtensions(LifecycleExtension.class)
@DebugJunitExtensions
class SpockIgnoreExtensionByInstance extends Specification{

    @RegisterExtension
    static LifecycleExtension ext = new LifecycleExtension()

    @RegisterExtension
    LifecycleExtension2 ext2 = new LifecycleExtension2()

    def "Sample test"() {

        when:
        ActionHolder.add("test.body");

        then:
        true
    }
}
