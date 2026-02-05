package ru.vyarus.spock.jupiter.engine.store;

import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ExtensionContext.Namespace;
import org.junit.jupiter.api.extension.ExtensionContextException;
import org.junit.platform.commons.util.Preconditions;

import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Store implementation bound to exact namespace. Note that underlying store is hierarchical: root for spec level
 * and child for test instance. All values from parent store are visible, but it is not possible to modify
 * these values!
 * <p>
 * Copy of {@code org.junit.jupiter.engine.execution.NamespaceAwareStore} from junit-jupiter-engine.
 *
 * @author Vyacheslav Rusakov
 * @since 22.12.2021
 */
@SuppressWarnings({"checkstyle:MultipleStringLiterals", "PMD.AvoidDuplicateLiterals"})
public class NamespaceAwareStore implements ExtensionContext.Store {
    private final NamespacedHierarchicalStore<Namespace> valuesStore;
    private final Namespace namespace;

    public NamespaceAwareStore(final NamespacedHierarchicalStore<Namespace> valuesStore,
                               final Namespace namespace) {
        this.valuesStore = valuesStore;
        this.namespace = namespace;
    }

    @Override
    public Object get(final Object key) {
        Preconditions.notNull(key, "key must not be null");
        final Supplier<Object> action = () -> this.valuesStore.get(this.namespace, key);
        return this.accessStore(action);
    }

    @Override
    public <T> T get(final Object key, final Class<T> requiredType) {
        Preconditions.notNull(key, "key must not be null");
        Preconditions.notNull(requiredType, "requiredType must not be null");
        final Supplier<T> action = () -> this.valuesStore.get(this.namespace, key, requiredType);
        return this.accessStore(action);
    }

    @Override
    public <K, V> Object getOrComputeIfAbsent(final K key, final Function<? super K, ? extends V> defaultCreator) {
        Preconditions.notNull(key, "key must not be null");
        Preconditions.notNull(defaultCreator, "defaultCreator function must not be null");
        final Supplier<Object> action = () ->
                this.valuesStore.getOrComputeIfAbsent(this.namespace, key, defaultCreator);
        return this.accessStore(action);
    }

    @Override
    public <K, V> V getOrComputeIfAbsent(final K key, final Function<? super K, ? extends V> defaultCreator,
                                         final Class<V> requiredType) {
        Preconditions.notNull(key, "key must not be null");
        Preconditions.notNull(defaultCreator, "defaultCreator function must not be null");
        Preconditions.notNull(requiredType, "requiredType must not be null");
        final Supplier<V> action = () ->
                this.valuesStore.getOrComputeIfAbsent(this.namespace, key, defaultCreator, requiredType);
        return this.accessStore(action);
    }

    @Override
    public <K, V> Object computeIfAbsent(final K key, final Function<? super K, ? extends V> defaultCreator) {
        Preconditions.notNull(key, "key must not be null");
        Preconditions.notNull(defaultCreator, "defaultCreator function must not be null");
        final Supplier<Object> action = () -> this.valuesStore.computeIfAbsent(this.namespace, key, defaultCreator);
        return this.accessStore(action);
    }

    @Override
    public <K, V> V computeIfAbsent(final K key,
                                    final Function<? super K, ? extends V> defaultCreator,
                                    final Class<V> requiredType) {
        Preconditions.notNull(key, "key must not be null");
        Preconditions.notNull(defaultCreator, "defaultCreator function must not be null");
        Preconditions.notNull(requiredType, "requiredType must not be null");
        final Supplier<V> action = () ->
                this.valuesStore.computeIfAbsent(this.namespace, key, defaultCreator, requiredType);
        return this.accessStore(action);
    }

    @Override
    public void put(final Object key, final Object value) {
        Preconditions.notNull(key, "key must not be null");
        final Supplier<Object> action = () -> this.valuesStore.put(this.namespace, key, value);
        this.accessStore(action);
    }

    @Override
    public Object remove(final Object key) {
        Preconditions.notNull(key, "key must not be null");
        final Supplier<Object> action = () -> this.valuesStore.remove(this.namespace, key);
        return this.accessStore(action);
    }

    @Override
    public <T> T remove(final Object key, final Class<T> requiredType) {
        Preconditions.notNull(key, "key must not be null");
        Preconditions.notNull(requiredType, "requiredType must not be null");
        final Supplier<T> action = () -> this.valuesStore.remove(this.namespace, key, requiredType);
        return this.accessStore(action);
    }

    private <T extends Object> T accessStore(final Supplier<T> action) {
        try {
            return action.get();
        } catch (NamespacedHierarchicalStoreException e) {
            throw new ExtensionContextException(e.getMessage(), e);
        }
    }
}
