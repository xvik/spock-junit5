package ru.vyarus.spock.jupiter.engine.context;

import org.junit.jupiter.api.extension.ExtensionContext;
import org.spockframework.runtime.model.SpecInfo;
import ru.vyarus.spock.jupiter.engine.ExtensionRegistry;

import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Method;
import java.util.Optional;

/**
 * Based on {@code org.junit.jupiter.engine.descriptor.ClassExtensionContext} from junit-jupiter-engine.
 *
 * @author Vyacheslav Rusakov
 * @since 20.12.2021
 */
public class ClassContext extends AbstractContext {

    public ClassContext(ExtensionContext parent, ExtensionRegistry registry, AnnotatedElement element, SpecInfo spec) {
        super(parent, registry, element, spec);
    }

    @Override
    public String getUniqueId() {
        return "class:" + spec.getReflection().getName();
    }

    @Override
    public String getDisplayName() {
        return spec.getDisplayName();
    }

    @Override
    public Optional<Method> getTestMethod() {
        return Optional.empty();
    }
}
