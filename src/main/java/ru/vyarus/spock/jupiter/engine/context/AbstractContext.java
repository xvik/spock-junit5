package ru.vyarus.spock.jupiter.engine.context;

import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExecutableInvoker;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.parallel.ExecutionMode;
import org.junit.platform.commons.util.Preconditions;
import org.junit.platform.engine.support.hierarchical.OpenTest4JAwareThrowableCollector;
import org.junit.platform.engine.support.hierarchical.ThrowableCollector;
import org.spockframework.runtime.model.SpecInfo;
import ru.vyarus.spock.jupiter.engine.ExtensionRegistry;
import ru.vyarus.spock.jupiter.engine.store.ExtensionValuesStore;
import ru.vyarus.spock.jupiter.engine.store.NamespaceAwareStore;

import java.lang.reflect.AnnotatedElement;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;

/**
 * Extension context object is passed to all extensions as parameter. Context is hierarchical: first level used
 * for spec (class level) and one context for each spec method. In case of spock data-driven methods, each iteration
 * will have a fresh method (feature) context (because extension instances must be renewed - each iteration is a
 * separate test run).
 * <p>
 * Based on
 * {@code org.junit.jupiter.engine.descriptor.AbstractExtensionContext} from junit-jupiter-engine. Note that original
 * jupiter implementation contains much more context types - they are not needed in context of spock.
 *
 * @author Vyacheslav Rusakov
 * @since 02.12.2021
 */
public abstract class AbstractContext implements ExtensionContext, AutoCloseable {

    protected final ExtensionContext parent;
    // this should not be there, but in case of spock it's more convenient to put it here
    protected final ExtensionRegistry registry;
    protected final AnnotatedElement element;
    protected final SpecInfo spec;
    protected final ThrowableCollector collector;

    private final ExtensionValuesStore valuesStore;
    private final ExecutableInvoker invoker;

    public AbstractContext(final ExtensionContext parent,
                           final ExtensionRegistry registry,
                           final AnnotatedElement element,
                           final SpecInfo spec) {
        this.parent = parent;
        this.registry = registry;
        this.element = element;
        this.spec = spec;
        collector = new OpenTest4JAwareThrowableCollector();
        valuesStore = createStore(parent);
        invoker = new DefaultExecutableInvoker(this);
    }

    @Override
    public Optional<ExtensionContext> getParent() {
        return Optional.ofNullable(this.parent);
    }

    @Override
    public ExtensionContext getRoot() {
        return this.parent != null ? this.parent.getRoot() : this;
    }

    @Override
    public Set<String> getTags() {
        // no tags support
        return Collections.emptySet();
    }

    @Override
    public Optional<AnnotatedElement> getElement() {
        return Optional.of(element);
    }

    @Override
    public Optional<Class<?>> getTestClass() {
        return Optional.of(spec.getReflection());
    }

    @Override
    public Optional<TestInstance.Lifecycle> getTestInstanceLifecycle() {
        // test instance per class not supported by spock
        return Optional.of(TestInstance.Lifecycle.PER_METHOD);
    }

    @Override
    public Optional<Throwable> getExecutionException() {
        return Optional.ofNullable(this.collector.getThrowable());
    }

    @Override
    public Optional<String> getConfigurationParameter(final String key) {
        // not supported (maybe need emulation)
        return Optional.empty();
    }

    @Override
    public <T> Optional<T> getConfigurationParameter(final String key, final Function<String, T> transformer) {
        // not supported (maybe need emulation)
        return Optional.empty();
    }

    @Override
    @SuppressWarnings("PMD.EmptyMethodInAbstractClassShouldBeAbstract")
    public void publishReportEntry(final Map<String, String> map) {
        // execution listener not implemented
    }

    @Override
    public Store getStore(final Namespace namespace) {
        Preconditions.notNull(namespace, "Namespace must not be null");
        return new NamespaceAwareStore(this.valuesStore, namespace);
    }

    @Override
    public ExecutionMode getExecutionMode() {
        final org.spockframework.runtime.model.parallel.ExecutionMode executionMode = spec.getExecutionMode()
                // default value not set in spock
                .orElse(org.spockframework.runtime.model.parallel.ExecutionMode.SAME_THREAD);
        return ExecutionMode.valueOf(executionMode.name());
    }

    @Override
    public ExecutableInvoker getExecutableInvoker() {
        return invoker;
    }

    public ExtensionRegistry getRegistry() {
        return registry;
    }

    public SpecInfo getSpec() {
        return spec;
    }

    // this is a big difference with junit itself because in junit each context has its own collector, but
    // as exceptions propagate to upper levels, upper context collectors being affected. But in spock lifecycle
    // listener collector scope is very limiting and so using context collector directly
    public ThrowableCollector getCollector() {
        return collector;
    }

    @Override
    public void close() throws Exception {
        valuesStore.closeAllStoredCloseableValues();
    }

    // org.junit.jupiter.engine.descriptor.AbstractExtensionContext.createStore
    private ExtensionValuesStore createStore(final ExtensionContext parent) {
        ExtensionValuesStore parentStore = null;
        if (parent != null) {
            parentStore = ((AbstractContext) parent).valuesStore;
        }
        return new ExtensionValuesStore(parentStore);
    }
}
