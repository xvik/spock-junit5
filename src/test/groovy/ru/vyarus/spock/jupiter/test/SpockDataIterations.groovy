package ru.vyarus.spock.jupiter.test

import org.junit.jupiter.api.extension.ExtendWith
import org.junit.jupiter.api.extension.RegisterExtension
import ru.vyarus.spock.jupiter.AbstractTest
import ru.vyarus.spock.jupiter.support.*
import spock.lang.Requires
import spock.lang.Specification

/**
 * @author Vyacheslav Rusakov
 * @since 26.12.2021
 */
@Requires({ AbstractTest.ACTIVE })
class SpockDataIterations extends Specification {

    @RegisterExtension
    static LifecycleExtension ext = new LifecycleExtension()

    @RegisterExtension
    LifecycleExtension2 ext2 = new LifecycleExtension2()

    @ExtendWith(LifecycleExtension3)
    def "Sample test"(int a, @ExtendWith(ParameterExtension.class) Integer arg) {

        when:
        ActionHolder.add("test.body $arg $a");

        then:
        true

        where:
        a | _
        1 | _
        2 | _
    }
}
