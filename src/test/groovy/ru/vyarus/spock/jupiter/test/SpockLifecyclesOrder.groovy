package ru.vyarus.spock.jupiter.test

import org.junit.jupiter.api.extension.ExtendWith
import org.spockframework.runtime.extension.ExtensionAnnotation
import org.spockframework.runtime.extension.IAnnotationDrivenExtension
import org.spockframework.runtime.extension.IMethodInterceptor
import org.spockframework.runtime.extension.IMethodInvocation
import org.spockframework.runtime.model.FeatureInfo
import org.spockframework.runtime.model.FieldInfo
import org.spockframework.runtime.model.MethodInfo
import org.spockframework.runtime.model.SpecInfo
import ru.vyarus.spock.jupiter.AbstractTest
import ru.vyarus.spock.jupiter.support.ActionHolder
import ru.vyarus.spock.jupiter.support.LifecycleExtension
import ru.vyarus.spock.jupiter.support.PostConstructExtension
import ru.vyarus.spock.jupiter.support.PreDestroyExtension
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

    @SpockLifecycle
    Object field

    @SpockLifecycle
    void setupSpec() {
        ActionHolder.add("test.beforeAll");
    }

    void cleanupSpec() {
        ActionHolder.add("test.afterAll");
    }

    @SpockLifecycle
    void setup() {
        ActionHolder.add("test.before");
    }

    void cleanup() {
        ActionHolder.add("test.after");
    }

    @SpockLifecycle
    def "Sample test"() {

        when:
        ActionHolder.add("test.body");

        then:
        true
    }
}

@Retention(RetentionPolicy.RUNTIME)
@Target([ElementType.TYPE, ElementType.METHOD, ElementType.FIELD])
@ExtensionAnnotation(SpockLifecycleExtension)
@interface SpockLifecycle {
    String value() default "";
}

class SpockLifecycleExtension implements IAnnotationDrivenExtension<SpockLifecycle> {
    @Override
    void visitSpecAnnotation(SpockLifecycle annotation, SpecInfo spec) {
        ActionHolder.add("spock.visitSpecAnnotation")

        spec.addSharedInitializerInterceptor new I('shared initializer')
        spec.sharedInitializerMethod?.addInterceptor new I('shared initializer method')
        spec.addInterceptor new I('specification')
        spec.addSetupSpecInterceptor new I('setup spec')
        spec.setupSpecMethods*.addInterceptor new I('setup spec method')
        spec.allFeatures*.addInterceptor new I('feature')
        spec.addInitializerInterceptor new I('initializer')
        spec.initializerMethod?.addInterceptor new I('initializer method')
        spec.allFeatures*.addIterationInterceptor new I('iteration')
        spec.addSetupInterceptor new I('setup')
        spec.setupMethods*.addInterceptor new I('setup method')
        spec.allFeatures*.featureMethod*.addInterceptor new I('feature method')
        spec.addCleanupInterceptor new I('cleanup')
        spec.cleanupMethods*.addInterceptor new I('cleanup method')
        spec.addCleanupSpecInterceptor new I('cleanup spec')
        spec.cleanupSpecMethods*.addInterceptor new I('cleanup spec method')
        spec.allFixtureMethods*.addInterceptor new I('fixture method')
    }

    @Override
    void visitFieldAnnotation(SpockLifecycle annotation, FieldInfo field) {
        ActionHolder.add("spock.visitFieldAnnotation " + field.name)
    }

    @Override
    void visitFixtureAnnotation(SpockLifecycle annotation, MethodInfo fixtureMethod) {
        ActionHolder.add("spock.visitFixtureAnnotation " + fixtureMethod.name)
    }

    @Override
    void visitFeatureAnnotation(SpockLifecycle annotation, FeatureInfo feature) {
        ActionHolder.add("spock.visitFeatureAnnotation " + feature.name)
    }

    static class I implements IMethodInterceptor {

        String name

        I(String name) {
            this.name = name
        }

        @Override
        void intercept(IMethodInvocation invocation) throws Throwable {
            ActionHolder.add("spock." + invocation.getMethod().getKind() + " (" + name + ")")
            invocation.proceed()
        }
    }
}
