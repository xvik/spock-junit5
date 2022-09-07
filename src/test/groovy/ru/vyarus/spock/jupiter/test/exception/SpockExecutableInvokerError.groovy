package ru.vyarus.spock.jupiter.test.exception

import org.junit.jupiter.api.extension.ExtendWith
import ru.vyarus.spock.jupiter.AbstractTest
import ru.vyarus.spock.jupiter.support.ExecutableInvokerUsage
import spock.lang.Requires
import spock.lang.Specification

/**
 * @author Vyacheslav Rusakov
 * @since 02.09.2022
 */
@Requires({ AbstractTest.ACTIVE })
@ExtendWith([ExecutableInvokerUsage])
class SpockExecutableInvokerError extends Specification {

    def "Sample test"() {

        expect:
        true
    }
}
