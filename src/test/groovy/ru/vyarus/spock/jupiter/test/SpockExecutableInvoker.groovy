package ru.vyarus.spock.jupiter.test

import org.junit.jupiter.api.extension.ExtendWith
import ru.vyarus.spock.jupiter.AbstractTest
import ru.vyarus.spock.jupiter.support.ExecutableInvokerUsage
import ru.vyarus.spock.jupiter.support.ParameterExtension
import spock.lang.Requires
import spock.lang.Specification

/**
 * @author Vyacheslav Rusakov
 * @since 02.09.2022
 */
@Requires({ AbstractTest.ACTIVE })
@ExtendWith([ParameterExtension, ExecutableInvokerUsage])
class SpockExecutableInvoker extends Specification {

    def "Sample test"() {

        expect:
        true
    }
}
