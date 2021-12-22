package ru.vyarus.spock.jupiter.engine.context.store;

import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ExtensionContextException;
import org.junit.platform.engine.support.hierarchical.OpenTest4JAwareThrowableCollector;
import org.junit.platform.engine.support.hierarchical.ThrowableCollector;

import java.util.Comparator;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Function;
import java.util.function.Supplier;

import static org.junit.platform.commons.util.ReflectionUtils.getWrapperType;
import static org.junit.platform.commons.util.ReflectionUtils.isAssignableTo;

/**
 * {@code ExtensionValuesStore} is used inside implementations of
 * {@link ExtensionContext} to store and retrieve values.
 * <p>
 * Copy of {@code org.junit.jupiter.engine.execution.ExtensionValuesStore} from junit-jupiter-engine.
 *
 * @author Vyacheslav Rusakov
 * @since 20.12.2021
 */
public class ExtensionValuesStore {

    private static final Comparator<StoredValue> REVERSE_INSERT_ORDER = Comparator.<StoredValue, Integer> comparing(
            it -> it.order).reversed();

    private final AtomicInteger insertOrderSequence = new AtomicInteger();
    private final ConcurrentMap<CompositeKey, StoredValue> storedValues = new ConcurrentHashMap<>(4);
    private final ExtensionValuesStore parentStore;

    public ExtensionValuesStore(ExtensionValuesStore parentStore) {
        this.parentStore = parentStore;
    }

    /**
     * Close all values that implement {@link ExtensionContext.Store.CloseableResource}.
     *
     * @implNote Only close values stored in this instance. This implementation
     * does not close values in parent stores.
     */
    public void closeAllStoredCloseableValues() {
        ThrowableCollector throwableCollector = new OpenTest4JAwareThrowableCollector();
        storedValues.values().stream() //
                .filter(storedValue -> storedValue.evaluateSafely() instanceof ExtensionContext.Store.CloseableResource) //
                .sorted(REVERSE_INSERT_ORDER) //
                .map(storedValue -> (ExtensionContext.Store.CloseableResource) storedValue.evaluate()) //
                .forEach(resource -> throwableCollector.execute(resource::close));
        throwableCollector.assertEmpty();
    }

    Object get(ExtensionContext.Namespace namespace, Object key) {
        StoredValue storedValue = getStoredValue(new CompositeKey(namespace, key));
        return (storedValue != null ? storedValue.evaluate() : null);
    }

    <T> T get(ExtensionContext.Namespace namespace, Object key, Class<T> requiredType) {
        Object value = get(namespace, key);
        return castToRequiredType(key, value, requiredType);
    }

    <K, V> Object getOrComputeIfAbsent(ExtensionContext.Namespace namespace, K key, Function<K, V> defaultCreator) {
        CompositeKey compositeKey = new CompositeKey(namespace, key);
        StoredValue storedValue = getStoredValue(compositeKey);
        if (storedValue == null) {
            StoredValue newValue = storedValue(new MemoizingSupplier(() -> defaultCreator.apply(key)));
            storedValue = Optional.ofNullable(storedValues.putIfAbsent(compositeKey, newValue)).orElse(newValue);
        }
        return storedValue.evaluate();
    }

    <K, V> V getOrComputeIfAbsent(ExtensionContext.Namespace namespace, K key, Function<K, V> defaultCreator, Class<V> requiredType) {
        Object value = getOrComputeIfAbsent(namespace, key, defaultCreator);
        return castToRequiredType(key, value, requiredType);
    }

    void put(ExtensionContext.Namespace namespace, Object key, Object value) {
        storedValues.put(new CompositeKey(namespace, key), storedValue(() -> value));
    }

    private StoredValue storedValue(Supplier<Object> value) {
        return new StoredValue(insertOrderSequence.getAndIncrement(), value);
    }

    Object remove(ExtensionContext.Namespace namespace, Object key) {
        StoredValue previous = storedValues.remove(new CompositeKey(namespace, key));
        return (previous != null ? previous.evaluate() : null);
    }

    <T> T remove(ExtensionContext.Namespace namespace, Object key, Class<T> requiredType) {
        Object value = remove(namespace, key);
        return castToRequiredType(key, value, requiredType);
    }

    private StoredValue getStoredValue(CompositeKey compositeKey) {
        StoredValue storedValue = storedValues.get(compositeKey);
        if (storedValue != null) {
            return storedValue;
        }
        if (parentStore != null) {
            return parentStore.getStoredValue(compositeKey);
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    private <T> T castToRequiredType(Object key, Object value, Class<T> requiredType) {
        if (value == null) {
            return null;
        }
        if (isAssignableTo(value, requiredType)) {
            if (requiredType.isPrimitive()) {
                return (T) getWrapperType(requiredType).cast(value);
            }
            return requiredType.cast(value);
        }
        // else
        throw new ExtensionContextException(
                String.format("Object stored under key [%s] is not of required type [%s]", key, requiredType.getName()));
    }

    private static class CompositeKey {

        private final ExtensionContext.Namespace namespace;
        private final Object key;

        private CompositeKey(ExtensionContext.Namespace namespace, Object key) {
            this.namespace = namespace;
            this.key = key;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }
            CompositeKey that = (CompositeKey) o;
            return this.namespace.equals(that.namespace) && this.key.equals(that.key);
        }

        @Override
        public int hashCode() {
            return Objects.hash(namespace, key);
        }

    }

    private static class StoredValue {

        private final int order;
        private final Supplier<Object> supplier;

        public StoredValue(int order, Supplier<Object> supplier) {
            this.order = order;
            this.supplier = supplier;
        }

        private Object evaluateSafely() {
            try {
                return evaluate();
            }
            catch (RuntimeException e) {
                return null;
            }
        }

        private Object evaluate() {
            return supplier.get();
        }

    }

    private static class MemoizingSupplier implements Supplier<Object> {

        private static final Object NO_VALUE_SET = new Object();

        private final Lock lock = new ReentrantLock();
        private final Supplier<Object> delegate;
        private volatile Object value = NO_VALUE_SET;

        private MemoizingSupplier(Supplier<Object> delegate) {
            this.delegate = delegate;
        }

        @Override
        public Object get() {
            if (value == NO_VALUE_SET) {
                computeValue();
            }
            if (value instanceof Failure) {
                throw ((Failure) value).exception;
            }
            return value;
        }

        private void computeValue() {
            lock.lock();
            try {
                if (value == NO_VALUE_SET) {
                    value = delegate.get();
                }
            }
            catch (RuntimeException e) {
                value = new Failure(e);
            }
            finally {
                lock.unlock();
            }
        }

        private static class Failure {

            private final RuntimeException exception;

            public Failure(RuntimeException exception) {
                this.exception = exception;
            }
        }

    }

}
