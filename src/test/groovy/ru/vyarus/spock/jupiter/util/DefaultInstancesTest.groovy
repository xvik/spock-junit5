package ru.vyarus.spock.jupiter.util

import org.junit.jupiter.api.extension.TestInstances
import ru.vyarus.spock.jupiter.engine.context.DefaultTestInstances
import spock.lang.Specification

/**
 * @author Vyacheslav Rusakov
 * @since 03.01.2022
 */
class DefaultInstancesTest extends Specification {

    def "Check default instances methods"() {

        when: "prepare single context"
        TestInstances inst = DefaultTestInstances.of(Integer.valueOf(12))

        then: "methods work"
        inst.findInstance(Integer).get() == 12
        !inst.findInstance(List).isPresent()
        inst.allInstances == [12]
        inst.enclosingInstances.empty
        inst.innermostInstance == 12

        when: "create inner instance"
        TestInstances inst2 = DefaultTestInstances.of(inst, Double.valueOf(1.0))

        then: "methods work"
        inst2.findInstance(Integer).get() == 12
        inst2.findInstance(Double).get() == 1.0
        inst2.allInstances == [12, 1.0]
        inst2.enclosingInstances == [12]
        inst2.innermostInstance == 1.0
    }
}
