package ru.vyarus.spock.jupiter.engine.store;

import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.platform.commons.util.Preconditions;

import java.util.function.Function;

/**
 * Copy of {@code org.junit.jupiter.engine.execution.NamespaceAwareStore} from junit-jupiter-engine.
 *
 * @author Vyacheslav Rusakov
 * @since 22.12.2021
 */
@SuppressWarnings({"checkstyle:MultipleStringLiterals", "PMD.AvoidDuplicateLiterals"})
public class NamespaceAwareStore implements ExtensionContext.Store {

    private final ExtensionValuesStore valuesStore;
    private final ExtensionContext.Namespace namespace;

    public NamespaceAwareStore(final ExtensionValuesStore valuesStore, final ExtensionContext.Namespace namespace) {
        this.valuesStore = valuesStore;
        this.namespace = namespace;
    }

    @Override
    public Object get(final Object key) {
        Preconditions.notNull(key, "key must not be null");
        return this.valuesStore.get(this.namespace, key);
    }

    @Override
    public <T> T get(final Object key, final Class<T> requiredType) {
        Preconditions.notNull(key, "key must not be null");
        Preconditions.notNull(requiredType, "requiredType must not be null");
        return this.valuesStore.get(this.namespace, key, requiredType);
    }

    @Override
    public <K, V> Object getOrComputeIfAbsent(final K key, final Function<K, V> defaultCreator) {
        Preconditions.notNull(key, "key must not be null");
        Preconditions.notNull(defaultCreator, "defaultCreator function must not be null");
        return this.valuesStore.getOrComputeIfAbsent(this.namespace, key, defaultCreator);
    }

    @Override
    public <K, V> V getOrComputeIfAbsent(final K key,
                                         final Function<K, V> defaultCreator,
                                         final Class<V> requiredType) {
        Preconditions.notNull(key, "key must not be null");
        Preconditions.notNull(defaultCreator, "defaultCreator function must not be null");
        Preconditions.notNull(requiredType, "requiredType must not be null");
        return this.valuesStore.getOrComputeIfAbsent(this.namespace, key, defaultCreator, requiredType);
    }

    @Override
    public void put(final Object key, final Object value) {
        Preconditions.notNull(key, "key must not be null");
        this.valuesStore.put(this.namespace, key, value);
    }

    @Override
    public Object remove(final Object key) {
        Preconditions.notNull(key, "key must not be null");
        return this.valuesStore.remove(this.namespace, key);
    }

    @Override
    public <T> T remove(final Object key, final Class<T> requiredType) {
        Preconditions.notNull(key, "key must not be null");
        Preconditions.notNull(requiredType, "requiredType must not be null");
        return this.valuesStore.remove(this.namespace, key, requiredType);
    }
}
