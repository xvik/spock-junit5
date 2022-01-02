package ru.vyarus.spock.jupiter.test.exception

import org.junit.jupiter.api.extension.ExtendWith
import org.junit.jupiter.api.extension.ExtensionContext
import org.junit.jupiter.api.extension.TestTemplateInvocationContext
import org.junit.jupiter.api.extension.TestTemplateInvocationContextProvider
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
class SpockNotSupportedExtensions extends Specification {

    @ExtendWith(NotSupportedExtension)
    def "Sample test"() {

        when:
        ActionHolder.add("test.body");

        then:
        true
    }

    static class NotSupportedExtension implements TestTemplateInvocationContextProvider {

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
