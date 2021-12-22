package ru.vyarus.spock.jupiter.engine.context;

import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.TestInstances;
import org.junit.jupiter.api.parallel.ExecutionMode;
import org.junit.platform.commons.util.Preconditions;
import org.junit.platform.engine.support.hierarchical.OpenTest4JAwareThrowableCollector;
import org.junit.platform.engine.support.hierarchical.ThrowableCollector;
import org.spockframework.runtime.model.SpecInfo;
import ru.vyarus.spock.jupiter.engine.ExtensionRegistry;
import ru.vyarus.spock.jupiter.engine.context.store.ExtensionValuesStore;
import ru.vyarus.spock.jupiter.engine.context.store.NamespaceAwareStore;

import java.lang.reflect.AnnotatedElement;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;

/**
 * Base class for extensions execution context. Based on
 * {@code org.junit.jupiter.engine.descriptor.AbstractExtensionContext} from junit-jupiter-engine.
 *
 * @author Vyacheslav Rusakov
 * @since 02.12.2021
 */
public abstract class AbstractContext implements ExtensionContext {

    protected final ExtensionContext parent;
    // this should not be there, but in case of spock it's more convenient to put it here
    protected final ExtensionRegistry registry;
    protected final AnnotatedElement element;
    protected final SpecInfo spec;
    protected final ThrowableCollector collector;

    private final ExtensionValuesStore valuesStore;
    private TestInstances instances;

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
    public Optional<Object> getTestInstance() {
        return getTestInstances().map(TestInstances::getInnermostInstance);
    }

    @Override
    public Optional<TestInstances> getTestInstances() {
        // todo move to class level
        return parent != null ? parent.getTestInstances() : Optional.ofNullable(instances);
    }

    @Override
    public Optional<Throwable> getExecutionException() {
        return Optional.ofNullable(this.collector.getThrowable());
    }

    @Override
    public Optional<String> getConfigurationParameter(String key) {
        // not supported (maybe need emulation)
        return Optional.empty();
    }

    @Override
    public <T> Optional<T> getConfigurationParameter(String key, Function<String, T> transformer) {
        // not supported (maybe need emulation)
        return Optional.empty();
    }

    @Override
    public void publishReportEntry(Map<String, String> map) {
        // execution listener not implemented
    }

    @Override
    public Store getStore(Namespace namespace) {
        Preconditions.notNull(namespace, "Namespace must not be null");
        return new NamespaceAwareStore(this.valuesStore, namespace);
    }

    @Override
    public ExecutionMode getExecutionMode() {
        org.spockframework.runtime.model.parallel.ExecutionMode executionMode = spec.getExecutionMode().get();
        return ExecutionMode.valueOf(executionMode.name());
    }

    public void setInstances(TestInstances instances) {
        this.instances = instances;
    }

    public ExtensionRegistry getRegistry() {
        return registry;
    }

    public SpecInfo getSpec() {
        return spec;
    }

    private ExtensionValuesStore createStore(ExtensionContext parent) {
        ExtensionValuesStore parentStore = null;
        if (parent != null) {
            parentStore = ((AbstractContext) parent).valuesStore;
        }
        return new ExtensionValuesStore(parentStore);
    }
}
