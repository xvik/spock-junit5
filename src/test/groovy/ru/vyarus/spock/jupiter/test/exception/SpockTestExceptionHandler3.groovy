package ru.vyarus.spock.jupiter.test.exception

import org.junit.jupiter.api.extension.ExtendWith
import ru.vyarus.spock.jupiter.AbstractTest
import ru.vyarus.spock.jupiter.support.LifecycleExtension
import ru.vyarus.spock.jupiter.support.RethrowExceptionHandler
import spock.lang.Requires
import spock.lang.Specification

/**
 * @author Vyacheslav Rusakov
 * @since 03.02.2026
 */
@Requires({ AbstractTest.ACTIVE })
// exception handlers processing order is reversed, exception hot handled (rethrowed)
@ExtendWith([LifecycleExtension, RethrowExceptionHandler])
class SpockTestExceptionHandler3 extends Specification {

    def "Sample test"() {

        expect:
        false
    }
}
