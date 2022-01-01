package ru.vyarus.spock.jupiter.test.exception

import org.junit.jupiter.api.extension.ExtendWith
import ru.vyarus.spock.jupiter.AbstractTest
import ru.vyarus.spock.jupiter.support.ActionHolder
import ru.vyarus.spock.jupiter.support.LifecycleExtension
import ru.vyarus.spock.jupiter.support.PreDestroyExtension
import ru.vyarus.spock.jupiter.support.exceptions.PreDestroyError
import spock.lang.Requires
import spock.lang.Specification

/**
 * @author Vyacheslav Rusakov
 * @since 01.01.2022
 */
@Requires({ AbstractTest.ACTIVE })
@ExtendWith([LifecycleExtension.class, PreDestroyError.class, PreDestroyExtension.class])
class SpockPreDestroyError extends Specification {

    def "Sample test"() {

        when:
        ActionHolder.add("test.body");

        then:
        true
    }
}
