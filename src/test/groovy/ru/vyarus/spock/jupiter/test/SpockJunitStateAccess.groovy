package ru.vyarus.spock.jupiter.test

import org.junit.jupiter.api.extension.BeforeAllCallback
import org.junit.jupiter.api.extension.BeforeEachCallback
import org.junit.jupiter.api.extension.ExtendWith
import org.junit.jupiter.api.extension.ExtensionContext
import org.spockframework.runtime.extension.ExtensionAnnotation
import org.spockframework.runtime.extension.IAnnotationDrivenExtension
import org.spockframework.runtime.extension.IMethodInterceptor
import org.spockframework.runtime.extension.IMethodInvocation
import org.spockframework.runtime.model.SpecInfo
import ru.vyarus.spock.jupiter.AbstractTest
import ru.vyarus.spock.jupiter.JunitExtensionSupport
import ru.vyarus.spock.jupiter.support.ActionHolder
import spock.lang.Requires
import spock.lang.Specification

import java.lang.annotation.ElementType
import java.lang.annotation.Retention
import java.lang.annotation.RetentionPolicy
import java.lang.annotation.Target

/**
 * @author Vyacheslav Rusakov
 * @since 03.01.2022
 */
@Requires({ AbstractTest.ACTIVE })
@ExtendWith(JunitExt)
@SpockStore
class SpockJunitStateAccess extends Specification {

    def "Sample test"() {

        when:
        ActionHolder.add("test.body");

        then:
        true
    }

    static class JunitExt implements BeforeAllCallback, BeforeEachCallback {

        @Override
        void beforeAll(ExtensionContext context) throws Exception {
            def store = context.getStore(ExtensionContext.Namespace.create('test'))
            store.put('val', 11)
            ActionHolder.add("JunitExt.beforeAll " + store.get('val'))
        }

        @Override
        void beforeEach(ExtensionContext context) throws Exception {
            // overwrite value
            def store = context.getStore(ExtensionContext.Namespace.create('test'))
            store.put('val', 12)
            ActionHolder.add("JunitExt.beforeEach " + store.get('val'))
        }
    }
}

@Retention(RetentionPolicy.RUNTIME)
@Target([ElementType.TYPE, ElementType.METHOD])
@ExtensionAnnotation(SpockStoreExtension)
@interface SpockStore {
    String value() default "";
}

class SpockStoreExtension implements IAnnotationDrivenExtension<SpockStore> {
    @Override
    void visitSpecAnnotation(SpockStore annotation, SpecInfo spec) {
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

    static class I implements IMethodInterceptor {

        String name

        I(String name) {
            this.name = name
        }

        @Override
        void intercept(IMethodInvocation invocation) throws Throwable {
            ActionHolder.add("spock." + invocation.getMethod().getKind() + " (" + name + ") " + getVal(invocation))
            invocation.proceed()
        }

        private Object getVal(IMethodInvocation invocation) {
            JunitExtensionSupport.getStore(invocation, ExtensionContext.Namespace.create('test')).get('val')
        }
    }
}
