package ru.vyarus.spock.jupiter.test

import org.junit.jupiter.api.extension.ExtendWith
import org.junit.jupiter.api.extension.ExtensionContext
import org.junit.jupiter.api.extension.ParameterContext
import org.junit.jupiter.api.extension.ParameterResolutionException
import org.junit.jupiter.api.extension.ParameterResolver
import ru.vyarus.spock.jupiter.AbstractTest
import ru.vyarus.spock.jupiter.support.ActionHolder
import spock.lang.Requires
import spock.lang.Specification

/**
 * @author Vyacheslav Rusakov
 * @since 02.01.2022
 */
@Requires({ AbstractTest.ACTIVE })
@ExtendWith(ParamBadType)
class SpockParamInjectionWrongType extends Specification {

    def "Sample test"(Integer arg) {

        when:
        ActionHolder.add("test.body $arg");

        then:
        true
    }
}

class ParamBadType implements ParameterResolver {

    @Override
    boolean supportsParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
        return true
    }

    @Override
    Object resolveParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
        return Double.valueOf(12.3)
    }
}