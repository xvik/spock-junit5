package ru.vyarus.spock.jupiter.test

import org.junit.jupiter.api.extension.ExtensionContext
import org.spockframework.runtime.extension.ExtensionAnnotation
import org.spockframework.runtime.extension.IAnnotationDrivenExtension
import org.spockframework.runtime.extension.IMethodInterceptor
import org.spockframework.runtime.model.SpecInfo
import ru.vyarus.spock.jupiter.JunitExtensionSupport
import spock.lang.Specification

import java.lang.annotation.ElementType
import java.lang.annotation.Retention
import java.lang.annotation.RetentionPolicy
import java.lang.annotation.Target
import java.util.concurrent.CopyOnWriteArrayList

/**
 * @author Vyacheslav Rusakov
 * @since 09.01.2022
 */
@SpockParallelStore
class SpockParallelStorageUsage extends Specification {

    def "Check 1"(int i) {

        when:
        SpockParallelStoreExtension.RES.add("test $i")
        println "test $i ${Thread.currentThread().name}"

        then:
        true

        where:
        i << (1..10)
    }
}

@Retention(RetentionPolicy.RUNTIME)
@Target([ElementType.TYPE, ElementType.METHOD])
@ExtensionAnnotation(SpockParallelStoreExtension)
@interface SpockParallelStore {
    String value() default "";
}

class SpockParallelStoreExtension implements IAnnotationDrivenExtension<SpockParallelStore> {

    static List<String> RES = new CopyOnWriteArrayList<>()

    @Override
    void visitSpecAnnotation(SpockParallelStore annotation, SpecInfo spec) {
        RES.add("spock.visitSpecAnnotation")

        spec.getAllFeatures().each { featureInfo ->
            featureInfo.getFeatureMethod().addInterceptor {
                ExtensionContext.Store store = JunitExtensionSupport
                        .getStore(it, ExtensionContext.Namespace.create('SpockParallelStoreExtension'))
                if (store.get('test') != null) {
                    RES.add("Not empty store on iteration ${it.iteration.iterationIndex + 1}: ${store.get('test')}")
                    throw new IllegalStateException("Not empty store")
                }
                store.put('test', 12)
                RES.add("ext ${it.iteration.iterationIndex + 1}")
                println "ext ${it.iteration.iterationIndex + 1} ${Thread.currentThread().name}"
                it.proceed()
            } as IMethodInterceptor
        }
    }
}