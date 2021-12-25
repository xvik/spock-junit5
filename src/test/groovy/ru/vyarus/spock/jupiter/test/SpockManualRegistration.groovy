package ru.vyarus.spock.jupiter.test

import org.junit.jupiter.api.extension.RegisterExtension
import ru.vyarus.spock.jupiter.support.ActionHolder
import ru.vyarus.spock.jupiter.support.LifecycleExtension
import ru.vyarus.spock.jupiter.support.LifecycleExtension2
import spock.lang.Specification

/**
 * @author Vyacheslav Rusakov
 * @since 26.12.2021
 */
// @Ignore
class SpockManualRegistration extends Specification {

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
