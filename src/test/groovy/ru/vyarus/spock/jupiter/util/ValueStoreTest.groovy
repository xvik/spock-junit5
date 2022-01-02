package ru.vyarus.spock.jupiter.util

import org.junit.jupiter.api.extension.ExtensionContext
import org.junit.jupiter.api.extension.ExtensionContextException
import ru.vyarus.spock.jupiter.engine.store.ExtensionValuesStore
import ru.vyarus.spock.jupiter.engine.store.NamespaceAwareStore
import spock.lang.Specification

/**
 * @author Vyacheslav Rusakov
 * @since 03.01.2022
 */
class ValueStoreTest extends Specification {

    def "Check value store methods"() {

        when: "prepare store"
        ExtensionValuesStore parent = new ExtensionValuesStore(null)
        NamespaceAwareStore store = new NamespaceAwareStore(new ExtensionValuesStore(parent), ExtensionContext.Namespace.create('test'))
        store.put('tt', 12)

        then: "storage ok"
        store.get('tt') == 12
        store.get('tt', Number) == 12
        store.get('tt', int.class) == 12
        store.get('pp') == null
        store.getOrComputeIfAbsent('pp', {11})
        store.getOrComputeIfAbsent('hh', {11}, Integer)

        when: 'removing values'
        store.remove('pp')

        then: "ok"
        store.get('pp') == null

        when: "remove with invalid type"
        store.remove('tt', String)

        then: "error"
        thrown(ExtensionContextException)

        when: "do computation failure"
        store.getOrComputeIfAbsent('fail', {throw new IllegalStateException("fail")})

        then: "error"
        thrown(IllegalStateException)

        when: "access failed value"
        store.get('fail')

        then: "it fails again"
        thrown(IllegalStateException)
    }

    def "Check root context modification"() {

        when: "prepare store"
        ExtensionValuesStore parent = new ExtensionValuesStore(null)
        NamespaceAwareStore parentStore = new NamespaceAwareStore(parent, ExtensionContext.Namespace.create('test'))
        parentStore.put('tt', 12)
        NamespaceAwareStore store = new NamespaceAwareStore(new ExtensionValuesStore(parent), ExtensionContext.Namespace.create('test'))
        store.remove('tt')

        then: "value not removed from parent context"
        parentStore.get('tt') == 12
    }
}
