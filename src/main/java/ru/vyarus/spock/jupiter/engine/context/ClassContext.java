package ru.vyarus.spock.jupiter.engine.context;

import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.TestInstances;
import org.spockframework.runtime.model.SpecInfo;
import ru.vyarus.spock.jupiter.engine.ExtensionRegistry;

import java.lang.reflect.Method;
import java.util.Optional;

/**
 * Represent spec level (class level). Used for instance post processors and before/after all extensions.
 * <p>
 * Based on {@code org.junit.jupiter.engine.descriptor.ClassExtensionContext} from junit-jupiter-engine.
 *
 * @author Vyacheslav Rusakov
 * @since 20.12.2021
 */
public class ClassContext extends AbstractContext {

    public ClassContext(final ExtensionContext root, final ExtensionRegistry registry, final SpecInfo spec) {
        super(root, registry, spec.getReflection(), spec);
    }

    @Override
    public String getUniqueId() {
        return "[class:" + spec.getReflection().getName() + "]";
    }

    @Override
    public String getDisplayName() {
        return spec.getDisplayName();
    }

    @Override
    public Optional<Method> getTestMethod() {
        return Optional.empty();
    }

    @Override
    public Optional<Object> getTestInstance() {
        return Optional.empty();
    }

    @Override
    public Optional<TestInstances> getTestInstances() {
        return Optional.empty();
    }
}
