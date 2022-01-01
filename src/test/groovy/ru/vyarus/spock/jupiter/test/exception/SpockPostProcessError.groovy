package ru.vyarus.spock.jupiter.test.exception

import org.junit.jupiter.api.extension.ExtendWith
import ru.vyarus.spock.jupiter.AbstractTest
import ru.vyarus.spock.jupiter.support.ActionHolder
import ru.vyarus.spock.jupiter.support.LifecycleExtension
import ru.vyarus.spock.jupiter.support.PostConstructExtension
import ru.vyarus.spock.jupiter.support.exceptions.PostProcessorError
import spock.lang.Requires
import spock.lang.Specification

/**
 * @author Vyacheslav Rusakov
 * @since 01.01.2022
 */
@Requires({ AbstractTest.ACTIVE })
@ExtendWith([LifecycleExtension.class, PostProcessorError.class, PostConstructExtension.class])
class SpockPostProcessError extends Specification {

    def "Sample test"() {

        when:
        ActionHolder.add("test.body");

        then:
        true
    }
}
