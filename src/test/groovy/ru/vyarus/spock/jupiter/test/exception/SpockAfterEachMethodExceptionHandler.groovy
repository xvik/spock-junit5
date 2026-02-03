package ru.vyarus.spock.jupiter.test.exception

import org.junit.jupiter.api.extension.ExtendWith
import ru.vyarus.spock.jupiter.AbstractTest
import ru.vyarus.spock.jupiter.support.ActionHolder
import ru.vyarus.spock.jupiter.support.EatLifecycleMethodExceptionHandler
import ru.vyarus.spock.jupiter.support.LifecycleExtension
import ru.vyarus.spock.jupiter.support.RethrowLifecycleMethodsExceptionHandler
import spock.lang.Requires
import spock.lang.Specification

/**
 * @author Vyacheslav Rusakov
 * @since 03.02.2026
 */
@Requires({ AbstractTest.ACTIVE })
// handlers processed in reverse order
@ExtendWith([LifecycleExtension.class, EatLifecycleMethodExceptionHandler.class, RethrowLifecycleMethodsExceptionHandler.class])
class SpockAfterEachMethodExceptionHandler extends Specification {

    void cleanup() {
        throw new IllegalStateException("problem");
    }

    def "Sample test"() {

        when:
        ActionHolder.add("test.body");

        then:
        true
    }
}
