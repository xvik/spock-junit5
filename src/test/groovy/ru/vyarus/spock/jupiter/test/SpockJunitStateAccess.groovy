package ru.vyarus.spock.jupiter.test

import org.junit.jupiter.api.extension.BeforeAllCallback
import org.junit.jupiter.api.extension.BeforeEachCallback
import org.junit.jupiter.api.extension.ExtendWith
import org.junit.jupiter.api.extension.ExtensionContext
import org.junit.jupiter.api.extension.TestInstancePostProcessor
import org.spockframework.runtime.extension.AbstractMethodInterceptor
import org.spockframework.runtime.extension.ExtensionAnnotation
import org.spockframework.runtime.extension.IAnnotationDrivenExtension
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
        ActionHolder.add("SpockStoreExtension")

        SpockInterceptor interceptor = new SpockInterceptor()

        spec.addSetupSpecInterceptor(interceptor)
        spec.addSharedInitializerInterceptor(interceptor)
        spec.addInitializerInterceptor(interceptor)
        spec.addSetupInterceptor(interceptor)
        spec.addCleanupInterceptor(interceptor)
        spec.addCleanupSpecInterceptor(interceptor)
    }

    static class SpockInterceptor extends AbstractMethodInterceptor {

        @Override
        void interceptSharedInitializerMethod(IMethodInvocation invocation) throws Throwable {
            ActionHolder.add("SpockInterceptor.sharedInit " + getVal(invocation))
            invocation.proceed()
        }

        @Override
        void interceptSetupSpecMethod(IMethodInvocation invocation) throws Throwable {
            ActionHolder.add("SpockInterceptor.setupAll " + getVal(invocation))
            invocation.proceed()
        }

        @Override
        void interceptInitializerMethod(IMethodInvocation invocation) throws Throwable {
            ActionHolder.add("SpockInterceptor.init " + getVal(invocation))
            invocation.proceed()
        }

        @Override
        void interceptSetupMethod(IMethodInvocation invocation) throws Throwable {
            ActionHolder.add("SpockInterceptor.setup " + getVal(invocation))
            invocation.proceed()
        }

        @Override
        void interceptCleanupMethod(IMethodInvocation invocation) throws Throwable {
            ActionHolder.add("SpockInterceptor.cleanup " + getVal(invocation))
            invocation.proceed()
        }

        @Override
        void interceptCleanupSpecMethod(IMethodInvocation invocation) throws Throwable {
            ActionHolder.add("SpockInterceptor.cleanupAll " + getVal(invocation))
            invocation.proceed()
        }

        private Object getVal(IMethodInvocation invocation) {
            JunitExtensionSupport.getStore(invocation, ExtensionContext.Namespace.create('test')).get('val')
        }
    }
}
