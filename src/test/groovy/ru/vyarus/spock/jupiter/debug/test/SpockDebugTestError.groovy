package ru.vyarus.spock.jupiter.debug.test

import org.junit.jupiter.api.extension.ExtendWith
import ru.vyarus.spock.jupiter.AbstractTest
import ru.vyarus.spock.jupiter.DebugJunitExtensions
import ru.vyarus.spock.jupiter.support.EatExceptionHandler
import ru.vyarus.spock.jupiter.support.RethrowExceptionHandler
import spock.lang.Requires
import spock.lang.Specification

/**
 * @author Vyacheslav Rusakov
 * @since 16.02.2026
 */
@Requires({ AbstractTest.ACTIVE })
@ExtendWith([EatExceptionHandler, RethrowExceptionHandler])
@DebugJunitExtensions
class SpockDebugTestError extends Specification {

    def "Sample test"() {

        when:
        throw new IllegalStateException("problem")

        then:
        true
    }
}
