package ru.vyarus.spock.jupiter.engine.store;

import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.platform.commons.util.Preconditions;
import org.junit.platform.commons.util.UnrecoverableExceptions;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;

import static java.util.Comparator.comparing;
import static java.util.Objects.requireNonNull;
import static org.junit.platform.commons.util.ExceptionUtils.throwAsUncheckedException;
import static org.junit.platform.commons.util.ReflectionUtils.getWrapperType;
import static org.junit.platform.commons.util.ReflectionUtils.isAssignableTo;

/**
 * Used inside implementations of {@link ExtensionContext} to store and retrieve values.
 * <p>
 * Copy of {@code org.junit.platform.engine.support.store.NamespacedHierarchicalStore} from junit-jupiter-engine
 * (because exactly the same behavior is required).
 *
 * @param <N> namespace type
 * @author Vyacheslav Rusakov
 * @since 20.12.2021
 */
@SuppressWarnings("PMD.CouplingBetweenObjects")
public class NamespacedHierarchicalStore<N> implements AutoCloseable {

    private final AtomicInteger insertOrderSequence = new AtomicInteger();

    private final ConcurrentMap<CompositeKey<N>, StoredValue> storedValues = new ConcurrentHashMap<>(4);

    private final NamespacedHierarchicalStore<N> parentStore;

    private final CloseAction<N> closeAction;

    private volatile boolean closed;

    /**
     * Create a new store with the supplied parent.
     *
     * @param parentStore the parent store to use for lookups; may be {@code null}
     */
    public NamespacedHierarchicalStore(final NamespacedHierarchicalStore<N> parentStore) {
        this(parentStore, null);
    }

    /**
     * Create a new store with the supplied parent and close action.
     *
     * @param parentStore the parent store to use for lookups; may be {@code null}
     * @param closeAction the action to be called for each stored value when this
     *                    store is closed; may be {@code null}
     */
    public NamespacedHierarchicalStore(final NamespacedHierarchicalStore<N> parentStore,
                                       final CloseAction<N> closeAction) {
        this.parentStore = parentStore;
        this.closeAction = closeAction;
    }

    /**
     * Create a child store with this store as its parent and this store's close
     * action.
     */
    public NamespacedHierarchicalStore<N> newChild() {
        return new NamespacedHierarchicalStore<>(this, this.closeAction);
    }

    /**
     * Returns the parent store of this {@code NamespacedHierarchicalStore}.
     *
     * <p>If this store does not have a parent, an empty {@code Optional} is returned.
     *
     * @return an {@code Optional} containing the parent store, or an empty {@code Optional} if there is no parent
     * @since 1.13
     */
    public Optional<NamespacedHierarchicalStore<N>> getParent() {
        return Optional.ofNullable(this.parentStore);
    }

    /**
     * Determine if this store has been {@linkplain #close() closed}.
     *
     * @return {@code true} if this store has been closed
     * @see #close()
     * @since 1.11
     */
    public boolean isClosed() {
        return this.closed;
    }

    /**
     * If a {@link CloseAction} is configured, it will be called with all successfully
     * stored values in reverse insertion order.
     *
     * <p>Closing a store does not close its parent or any of its children.
     *
     * <p>Invocations of this method after the store has already been closed will
     * be ignored.
     *
     * @see #isClosed()
     */
    @Override
    public void close() {
        if (!this.closed) {
            try {
                if (this.closeAction != null) {
                    final List<Throwable> failures = new ArrayList<>();
                    this.storedValues.entrySet().stream()
                            .map(e -> EvaluatedValue.createSafely(e.getKey(), e.getValue()))
                            .filter(Objects::nonNull) //
                            .sorted(EvaluatedValue.REVERSE_INSERT_ORDER)
                            .forEach(it -> {
                                try {
                                    it.close(this.closeAction);
                                } catch (Throwable t) {
                                    UnrecoverableExceptions.rethrowIfUnrecoverable(t);
                                    failures.add(t);
                                }
                            });
                    if (!failures.isEmpty()) {
                        final Iterator<Throwable> iterator = failures.iterator();
                        final Throwable throwable = iterator.next();
                        iterator.forEachRemaining(throwable::addSuppressed);
                        throw throwAsUncheckedException(throwable);
                    }
                }
            } finally {
                this.closed = true;
            }
        }
    }

    /**
     * Get the value stored for the supplied namespace and key in this store or
     * the parent store, if present.
     *
     * @param namespace the namespace; never {@code null}
     * @param key       the key; never {@code null}
     * @return the stored value; may be {@code null}
     * @throws NamespacedHierarchicalStoreException if this store has already been
     *                                              closed
     */
    public Object get(final N namespace, final Object key) {
        final StoredValue storedValue = getStoredValue(new CompositeKey<>(namespace, key));
        return StoredValue.evaluateIfNotNull(storedValue);
    }

    /**
     * Get the value stored for the supplied namespace and key in this store or
     * the parent store, if present, and cast it to the supplied required type.
     *
     * @param namespace    the namespace; never {@code null}
     * @param key          the key; never {@code null}
     * @param requiredType the required type of the value; never {@code null}
     * @return the stored value; may be {@code null}
     * @throws NamespacedHierarchicalStoreException if the stored value cannot
     *                                              be cast to the required type, or if this store has already
     *                                              been closed
     */
    public <T> T get(final N namespace, final Object key, final Class<T> requiredType)
            throws NamespacedHierarchicalStoreException {
        final Object value = get(namespace, key);
        return castToRequiredType(key, value, requiredType);
    }

    /**
     * Return the value stored for the supplied namespace and key in this store
     * or the parent store, if present and not {@code null}, or call the
     * supplied function to compute it.
     *
     * @param namespace      the namespace; never {@code null}
     * @param key            the key; never {@code null}
     * @param defaultCreator the function called with the supplied {@code key}
     *                       to create a new value; never {@code null} and must not return
     *                       {@code null}
     * @return the stored value; never {@code null}
     * @throws NamespacedHierarchicalStoreException if this store has already been
     *                                              closed
     * @since 6.0
     */
    @SuppressWarnings("checkstyle:ReturnCount")
    public <K, V> Object computeIfAbsent(final N namespace,
                                         final K key,
                                         final Function<? super K, ? extends V> defaultCreator) {
        Preconditions.notNull(defaultCreator, "defaultCreator must not be null");
        final CompositeKey<N> compositeKey = new CompositeKey<>(namespace, key);
        final StoredValue currentStoredValue = getStoredValue(compositeKey);
        final Object result = StoredValue.evaluateIfNotNull(currentStoredValue);
        if (result != null) {
            return result;
        }
        final StoredValue.DeferredOptionalValue candidateStoredValue = newStoredSuppliedValue(() -> {
            rejectIfClosed();
            return Preconditions.notNull(defaultCreator.apply(key), "defaultCreator must not return null");
        });
        final StoredValue storedValue = storedValues.compute(compositeKey,
                (compKey, oldStoredValue) -> {
                    // guard against race conditions
                    // computeIfAbsent replaces both null and absent values
                    if (StoredValue.evaluateIfNotNull(oldStoredValue) != null) {
                        return oldStoredValue;
                    }
                    rejectIfClosed();
                    return candidateStoredValue;
                });

        // In a race condition either put, getOrComputeIfAbsent, or another
        // computeIfAbsent call put a non-null value in the store
        if (!candidateStoredValue.equals(storedValue)) {
            return requireNonNull(storedValue.evaluate());
        }
        // Only the caller that created the candidateStoredValue may run it
        // and see the exception.
        final Object newResult = candidateStoredValue.execute();
        // DeferredOptionalValue is quite heavy, replace with lighter container
        if (candidateStoredValue.isPresent()) {
            storedValues.computeIfPresent(compositeKey, compareAndPut(storedValue, newStoredValue(newResult)));
        }
        return newResult;
    }

    /**
     * Return the value stored for the supplied namespace and key in this store
     * or the parent store, if present and not {@code null}, or call the
     * supplied function to compute it and, finally, cast it to the supplied
     * required type.
     *
     * @param namespace      the namespace; never {@code null}
     * @param key            the key; never {@code null}
     * @param defaultCreator the function called with the supplied {@code key}
     *                       to create a new value; never {@code null} and must not return
     *                       {@code null}
     * @param requiredType   the required type of the value; never {@code null}
     * @return the stored value; never {@code null}
     * @throws NamespacedHierarchicalStoreException if the stored value cannot
     *                                              be cast to the required type, or if this store has already
     *                                              been closed
     * @since 6.0
     */
    public <K, V> V computeIfAbsent(final N namespace, final K key,
                                    final Function<? super K, ? extends V> defaultCreator,
                                    final Class<V> requiredType) throws NamespacedHierarchicalStoreException {

        final Object value = computeIfAbsent(namespace, key, defaultCreator);
        return castNonNullToRequiredType(key, value, requiredType);
    }

    /**
     * Get the value stored for the supplied namespace and key in this store or
     * the parent store, if present, or call the supplied function to compute it.
     *
     * @param namespace      the namespace; never {@code null}
     * @param key            the key; never {@code null}
     * @param defaultCreator the function called with the supplied {@code key}
     *                       to create a new value; never {@code null} but may return {@code null}
     * @return the stored value; may be {@code null}
     * @throws NamespacedHierarchicalStoreException if this store has already been
     *                                              closed
     * @deprecated Please use {@link #computeIfAbsent(Object, Object, Function)} instead.
     */
    @Deprecated(since = "6.0")
    @SuppressWarnings("checkstyle:ReturnCount")
    public <K, V extends Object> Object getOrComputeIfAbsent(final N namespace, final K key,
                                                             final Function<? super K, ? extends V> defaultCreator) {
        Preconditions.notNull(defaultCreator, "defaultCreator must not be null");
        final CompositeKey<N> compositeKey = new CompositeKey<>(namespace, key);
        final StoredValue currentStoredValue = getStoredValue(compositeKey);
        if (currentStoredValue != null) {
            return currentStoredValue.evaluate();
        }
        final StoredValue.DeferredValue candidateStoredValue = newStoredSuppliedNullableValue(() -> {
            rejectIfClosed();
            return defaultCreator.apply(key);
        });
        final StoredValue storedValue = storedValues.compute(compositeKey, //
                (compKey, oldStoredValue) -> {
                    // guard against race conditions, repeated from getStoredValue
                    // this filters out failures inserted by computeIfAbsent
                    if (StoredValue.isNonNullAndPresent(oldStoredValue)) {
                        return oldStoredValue;
                    }
                    rejectIfClosed();
                    return candidateStoredValue;
                });

        // Only the caller that created the candidateStoredValue may run it
        if (candidateStoredValue.equals(storedValue)) {
            return candidateStoredValue.execute();
        }
        return storedValue.evaluate();
    }

    /**
     * Get the value stored for the supplied namespace and key in this store or
     * the parent store, if present, or call the supplied function to compute it
     * and, finally, cast it to the supplied required type.
     *
     * @param namespace      the namespace; never {@code null}
     * @param key            the key; never {@code null}
     * @param defaultCreator the function called with the supplied {@code key}
     *                       to create a new value; never {@code null} but may return {@code null}
     * @param requiredType   the required type of the value; never {@code null}
     * @return the stored value; may be {@code null}
     * @throws NamespacedHierarchicalStoreException if the stored value cannot
     *                                              be cast to the required type, or if this store has already
     *                                              been closed
     * @deprecated Please use {@link #computeIfAbsent(Object, Object, Function, Class)} instead.
     */
    @Deprecated(since = "6.0")
    public <K, V extends Object> V getOrComputeIfAbsent(
            final N namespace, final K key,
            final Function<? super K, ? extends V> defaultCreator,
            final Class<V> requiredType)
            throws NamespacedHierarchicalStoreException {

        final Object value = getOrComputeIfAbsent(namespace, key, defaultCreator);
        return castToRequiredType(key, value, requiredType);
    }

    /**
     * Put the supplied value for the supplied namespace and key into this
     * store and return the previously associated value in this store.
     *
     * <p>The {@link CloseAction} will <em>not</em> be called for the previously
     * stored value, if any.
     *
     * @param namespace the namespace; never {@code null}
     * @param key       the key; never {@code null}
     * @param value     the value to store; may be {@code null}
     * @return the previously stored value; may be {@code null}
     * @throws NamespacedHierarchicalStoreException if an error occurs while
     *                                              storing the value, or if this store has already been closed
     */
    public Object put(final N namespace, final Object key, final Object value)
            throws NamespacedHierarchicalStoreException {
        rejectIfClosed();
        final StoredValue oldValue = this.storedValues.put(new CompositeKey<>(namespace, key), newStoredValue(value));
        return StoredValue.evaluateIfNotNull(oldValue);
    }

    /**
     * Remove the value stored for the supplied namespace and key from this
     * store.
     *
     * <p>The {@link CloseAction} will <em>not</em> be called for the removed
     * value.
     *
     * @param namespace the namespace; never {@code null}
     * @param key       the key; never {@code null}
     * @return the previously stored value; may be {@code null}
     * @throws NamespacedHierarchicalStoreException if this store has already been
     *                                              closed
     */
    public Object remove(final N namespace, final Object key) {
        rejectIfClosed();
        final StoredValue previous = this.storedValues.remove(new CompositeKey<>(namespace, key));
        return StoredValue.evaluateIfNotNull(previous);
    }

    /**
     * Remove the value stored for the supplied namespace and key from this
     * store and cast it to the supplied required type.
     *
     * <p>The {@link CloseAction} will <em>not</em> be called for the removed
     * value.
     *
     * @param namespace    the namespace; never {@code null}
     * @param key          the key; never {@code null}
     * @param requiredType the required type of the value; never {@code null}
     * @return the previously stored value; may be {@code null}
     * @throws NamespacedHierarchicalStoreException if the stored value cannot
     *                                              be cast to the required type, or if this store has already
     *                                              been closed
     */
    public <T> T remove(final N namespace, final Object key, final Class<T> requiredType)
            throws NamespacedHierarchicalStoreException {
        rejectIfClosed();
        final Object value = remove(namespace, key);
        return castToRequiredType(key, value, requiredType);
    }

    private StoredValue.Value newStoredValue(final Object value) {
        final int sequenceNumber = insertOrderSequence.getAndIncrement();
        return new StoredValue.Value(sequenceNumber, value);
    }

    private StoredValue.DeferredValue newStoredSuppliedNullableValue(final Supplier<Object> supplier) {
        final int sequenceNumber = insertOrderSequence.getAndIncrement();
        return new StoredValue.DeferredValue(sequenceNumber, supplier);
    }

    private StoredValue.DeferredOptionalValue newStoredSuppliedValue(final Supplier<Object> supplier) {
        final int sequenceNumber = insertOrderSequence.getAndIncrement();
        return new StoredValue.DeferredOptionalValue(sequenceNumber, supplier);
    }

    @SuppressWarnings("checkstyle:ReturnCount")
    private StoredValue getStoredValue(final CompositeKey<N> compositeKey) {
        final StoredValue storedValue = this.storedValues.get(compositeKey);
        if (StoredValue.isNonNullAndPresent(storedValue)) {
            return storedValue;
        }
        if (this.parentStore != null) {
            return this.parentStore.getStoredValue(compositeKey);
        }
        return null;
    }

    private <T> T castToRequiredType(final Object key, final Object value, final Class<T> requiredType) {
        Preconditions.notNull(requiredType, "requiredType must not be null");
        if (value == null) {
            return null;
        }
        return castNonNullToRequiredType(key, value, requiredType);
    }

    @SuppressWarnings("unchecked")
    private <T, V> T castNonNullToRequiredType(final Object key, final V value, final Class<T> requiredType) {
        if (isAssignableTo(value, requiredType)) {
            if (requiredType.isPrimitive()) {
                return (T) requireNonNull(getWrapperType(requiredType)).cast(value);
            }
            return requiredType.cast(value);
        }
        // else
        throw new NamespacedHierarchicalStoreException(
                "Object stored under key [%s] is not of required type [%s], but was [%s]: %s".formatted(key,
                        requiredType.getName(), value.getClass().getName(), value));
    }

    private void rejectIfClosed() {
        if (this.closed) {
            throw new NamespacedHierarchicalStoreException(
                    "A NamespacedHierarchicalStore cannot be modified or queried after it has been closed");
        }
    }

    private static <N> BiFunction<CompositeKey<N>, StoredValue, StoredValue> compareAndPut(
            final StoredValue expectedValue,
            final StoredValue newValue) {
        return (compositeKey, storedValue) -> {
            if (expectedValue.equals(storedValue)) {
                return newValue;
            }
            return storedValue;
        };
    }

    private record CompositeKey<N>(N namespace, Object key) {

        CompositeKey {
            Preconditions.notNull(namespace, "namespace must not be null");
            Preconditions.notNull(key, "key must not be null");
        }

    }

    private interface StoredValue {

        int order();

        Object evaluate();

        boolean isPresent();

        static Object evaluateIfNotNull(final StoredValue value) {
            return value != null ? value.evaluate() : null;
        }

        static boolean isNonNullAndPresent(final StoredValue value) {
            return value != null && value.isPresent();
        }

        /**
         * May contain {@code null} or a value, never an exception.
         */
        @SuppressWarnings("PMD.AvoidFieldNameMatchingMethodName")
        final class Value implements StoredValue {
            private final int order;
            private final Object val;

            Value(final int order, final Object value) {
                this.order = order;
                this.val = value;
            }

            @Override
            public Object evaluate() {
                return val;
            }

            @Override
            public boolean isPresent() {
                return true;
            }

            @Override
            public int order() {
                return order;
            }
        }

        /**
         * May eventually contain {@code null} or a value or an exception.
         */
        @SuppressWarnings("PMD.AvoidFieldNameMatchingMethodName")
        final class DeferredValue implements StoredValue {
            private final int order;
            private final DeferredSupplier delegate;

            DeferredValue(final int order, final Supplier<Object> delegate) {
                this.order = order;
                this.delegate = new DeferredSupplier(delegate);
            }

            @Override
            public Object evaluate() {
                return delegate.getOrThrow();
            }

            @Override
            public boolean isPresent() {
                return true;
            }

            Object execute() {
                delegate.run();
                return delegate.getOrThrow();
            }

            @Override
            public int order() {
                return order;
            }
        }

        /**
         * May eventually contain a value or an exception, never {@code null}.
         */
        @SuppressWarnings("PMD.AvoidFieldNameMatchingMethodName")
        final class DeferredOptionalValue implements StoredValue {
            private final int order;
            private final DeferredSupplier delegate;

            DeferredOptionalValue(final int order, final Supplier<Object> delegate) {
                this.order = order;
                this.delegate = new DeferredSupplier(delegate);
            }

            @Override
            public Object evaluate() {
                return delegate.get();
            }

            @Override
            public boolean isPresent() {
                return evaluate() != null;
            }

            Object execute() {
                delegate.run();
                // Delegate does not produce null
                return requireNonNull(delegate.getOrThrow());
            }

            @Override
            public int order() {
                return order;
            }
        }
    }

    private record EvaluatedValue<N>(CompositeKey<N> compositeKey, int order, Object value) {

        private static final Comparator<EvaluatedValue<?>> REVERSE_INSERT_ORDER = comparing(
                (EvaluatedValue<?> it) -> it.order).reversed();

        @SuppressWarnings("checkstyle:ReturnCount")
        private static <N> EvaluatedValue<N> createSafely(final CompositeKey<N> compositeKey, final StoredValue value) {
            try {
                final Object evaluatedValue = value.evaluate();
                if (evaluatedValue == null) {
                    return null;
                }
                return new EvaluatedValue<>(compositeKey, value.order(), evaluatedValue);
            } catch (Throwable t) {
                UnrecoverableExceptions.rethrowIfUnrecoverable(t);
                return null;
            }
        }

        private void close(final CloseAction<N> closeAction) throws Throwable {
            closeAction.close(this.compositeKey.namespace, this.compositeKey.key, this.value);
        }

    }

    /**
     * Deferred computation that can be added to the store.
     * <p>
     * This allows values to be computed outside the
     * {@link ConcurrentHashMap#compute(Object, BiFunction)} calls and
     * prevents recursive updates.
     */
    static final class DeferredSupplier {

        private final FutureTask<Object> task;

        DeferredSupplier(final Supplier<?> delegate) {
            this.task = new FutureTask<>(delegate::get);
        }

        void run() {
            task.run();
        }

        Object get() {
            try {
                return task.get();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw throwAsUncheckedException(e);
            } catch (ExecutionException e) {
                // non-null guaranteed by FutureTask
                final Throwable cause = requireNonNull(e.getCause());
                UnrecoverableExceptions.rethrowIfUnrecoverable(cause);
                return null;
            }
        }

        @SuppressWarnings("PMD.PreserveStackTrace")
        Object getOrThrow() {
            try {
                return task.get();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw throwAsUncheckedException(e);
            } catch (ExecutionException e) {
                // non-null guaranteed by FutureTask
                final Throwable cause = requireNonNull(e.getCause());
                UnrecoverableExceptions.rethrowIfUnrecoverable(cause);
                throw throwAsUncheckedException(cause);
            }
        }
    }

    /**
     * Called for each successfully stored non-null value in the store when a
     * {@link NamespacedHierarchicalStore} is
     * {@linkplain NamespacedHierarchicalStore#close() closed}.
     *
     * @param <N> namespace type
     */
    @FunctionalInterface
    public interface CloseAction<N> {

        /**
         * Static factory method for creating a {@link CloseAction} which
         * {@linkplain #close(Object, Object, Object) closes} any value that
         * implements {@link AutoCloseable}.
         *
         * @since 6.0
         */
        static <N> CloseAction<N> closeAutoCloseables() {
            return (ns, key, value) -> {
                if (value instanceof AutoCloseable closeable) {
                    closeable.close();
                }
            };
        }

        /**
         * Close the supplied {@code value}.
         *
         * @param namespace the namespace; never {@code null}
         * @param key       the key; never {@code null}
         * @param value     the value; never {@code null}
         */
        void close(N namespace, Object key, Object value) throws Throwable;
    }
}
