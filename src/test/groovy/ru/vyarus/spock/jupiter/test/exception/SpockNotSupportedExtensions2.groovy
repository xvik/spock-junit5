package ru.vyarus.spock.jupiter.test.exception

import org.junit.jupiter.api.extension.*
import ru.vyarus.spock.jupiter.AbstractTest
import ru.vyarus.spock.jupiter.support.ActionHolder
import spock.lang.Requires
import spock.lang.Specification

import java.util.stream.Stream

/**
 * @author Vyacheslav Rusakov
 * @since 03.01.2022
 */
@Requires({ AbstractTest.ACTIVE })
class SpockNotSupportedExtensions2 extends Specification {

    @ExtendWith(NotSupportedExtension)
    def "Sample test"() {

        when:
        ActionHolder.add("test.body");

        then:
        true
    }

    static class NotSupportedExtension implements ParameterResolver, TestTemplateInvocationContextProvider {

        @Override
        boolean supportsParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
            return false
        }

        @Override
        Object resolveParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
            return null
        }

        @Override
        boolean supportsTestTemplate(ExtensionContext context) {
            return false
        }

        @Override
        Stream<TestTemplateInvocationContext> provideTestTemplateInvocationContexts(ExtensionContext context) {
            return null
        }
    }
}
