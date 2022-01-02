package ru.vyarus.spock.jupiter.test

import org.junit.jupiter.api.extension.ExtendWith
import org.spockframework.runtime.extension.ExtensionAnnotation
import org.spockframework.runtime.extension.IAnnotationDrivenExtension
import org.spockframework.runtime.model.SpecInfo
import ru.vyarus.spock.jupiter.AbstractTest
import ru.vyarus.spock.jupiter.support.*
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
@ExtendWith([LifecycleExtension, PostConstructExtension, PreDestroyExtension])
@SpockLifecycle
class SpockLifecyclesOrder extends Specification {

    void setupSpec() {
        ActionHolder.add("test.beforeAll");
    }

    void cleanupSpec() {
        ActionHolder.add("test.afterAll");
    }

    void setup() {
        ActionHolder.add("test.before");
    }

    void cleanup() {
        ActionHolder.add("test.after");
    }

    def "Sample test"() {

        when:
        ActionHolder.add("test.body");

        then:
        true
    }
}

@Retention(RetentionPolicy.RUNTIME)
@Target([ElementType.TYPE, ElementType.METHOD])
@ExtensionAnnotation(SpockLifecycleExtension)
@interface SpockLifecycle {
    String value() default "";
}

class SpockLifecycleExtension implements IAnnotationDrivenExtension<SpockLifecycle> {
    @Override
    void visitSpecAnnotation(SpockLifecycle annotation, SpecInfo spec) {
        ActionHolder.add("SpockLifecycleExtension")

        spec.getAllFeatures()*.addInterceptor({
            ActionHolder.add("SpockLifecycleExtension $it.feature.displayName")
            it.proceed()
        })
    }
}
