package ru.vyarus.spock.jupiter.debug.test

import org.junit.jupiter.api.extension.ExtendWith
import ru.vyarus.spock.jupiter.AbstractTest
import ru.vyarus.spock.jupiter.DebugJunitExtensions
import ru.vyarus.spock.jupiter.support.ActionHolder
import ru.vyarus.spock.jupiter.support.EatLifecycleMethodExceptionHandler
import ru.vyarus.spock.jupiter.support.RethrowLifecycleMethodsExceptionHandler
import spock.lang.Requires
import spock.lang.Specification

/**
 * @author Vyacheslav Rusakov
 * @since 16.02.2026
 */
@Requires({ AbstractTest.ACTIVE })
@ExtendWith([EatLifecycleMethodExceptionHandler.class, RethrowLifecycleMethodsExceptionHandler.class])
@DebugJunitExtensions
class SpockDebugSetupError extends Specification {

    void setupSpec() {
        throw new IllegalStateException("problem");
    }

    def "Sample test"() {

        when:
        ActionHolder.add("test.body");

        then:
        true
    }
}
