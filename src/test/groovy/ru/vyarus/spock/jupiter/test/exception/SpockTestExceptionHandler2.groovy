package ru.vyarus.spock.jupiter.test.exception

import org.junit.jupiter.api.extension.ExtendWith
import ru.vyarus.spock.jupiter.AbstractTest
import ru.vyarus.spock.jupiter.support.LifecycleExtension
import ru.vyarus.spock.jupiter.support.RethrowExceptionHandler
import ru.vyarus.spock.jupiter.support.SwallowExceptionHandler
import spock.lang.Requires
import spock.lang.Specification

/**
 * @author Vyacheslav Rusakov
 * @since 02.01.2022
 */
@Requires({ AbstractTest.ACTIVE })
// exception handlers processing order is reversed
@ExtendWith([LifecycleExtension, SwallowExceptionHandler, RethrowExceptionHandler])
class SpockTestExceptionHandler2 extends Specification {

    def "Sample test"() {

        expect:
        false
    }
}
