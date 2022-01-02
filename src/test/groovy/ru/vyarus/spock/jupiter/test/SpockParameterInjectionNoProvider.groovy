package ru.vyarus.spock.jupiter.test

import org.spockframework.runtime.extension.ExtensionAnnotation
import org.spockframework.runtime.extension.IAnnotationDrivenExtension
import org.spockframework.runtime.model.MethodInfo
import org.spockframework.runtime.model.SpecInfo
import ru.vyarus.spock.jupiter.AbstractTest
import ru.vyarus.spock.jupiter.support.ActionHolder
import spock.lang.Requires
import spock.lang.Specification

import java.lang.annotation.ElementType
import java.lang.annotation.Retention
import java.lang.annotation.RetentionPolicy
import java.lang.annotation.Target

/**
 * @author Vyacheslav Rusakov
 * @since 02.01.2022
 */
@Requires({ AbstractTest.ACTIVE })
// spock extension
@Params
class SpockParameterInjectionNoProvider extends Specification {

    def "Sample test"(Integer arg) {

        when:
        ActionHolder.add("test.body $arg");

        then:
        true
    }

}

@Retention(RetentionPolicy.RUNTIME)
@Target([ElementType.TYPE, ElementType.METHOD])
@ExtensionAnnotation(ParamsExtension)
@interface Params {
    /**
     * The reason for ignoring this element.
     *
     * @return the reason for ignoring this element
     */
    String value() default "";
}

class ParamsExtension implements IAnnotationDrivenExtension<Params> {
    @Override
    void visitSpecAnnotations(List<Params> annotations, SpecInfo spec) {
        spec.getAllFeatures().each {
            it.getFeatureMethod().addInterceptor({
                final Object[] arguments = it.getArguments()

                for (int i = 0; i < arguments.length; i++) {
                    if (arguments[i] == MethodInfo.MISSING_ARGUMENT) {
                        arguments[i] = i + 13
                    }
                }
                it.proceed()
            })
        }
    }
}
