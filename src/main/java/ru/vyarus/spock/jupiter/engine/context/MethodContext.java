package ru.vyarus.spock.jupiter.engine.context;

import org.junit.jupiter.api.extension.ExtensionContext;
import org.spockframework.runtime.model.FeatureInfo;
import ru.vyarus.spock.jupiter.engine.ExtensionRegistry;

import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Method;
import java.util.Optional;

/**
 * Based on {@code org.junit.jupiter.engine.descriptor.MethodExtensionContext} from junit-jupiter-engine.
 *
 * @author Vyacheslav Rusakov
 * @since 20.12.2021
 */
public class MethodContext extends AbstractContext {

    private final FeatureInfo feature;

    public MethodContext(ExtensionContext parent, ExtensionRegistry registry, AnnotatedElement element, FeatureInfo feature) {
        super(parent, registry, element, ((ClassContext) parent).getSpec());
        this.feature = feature;
    }

    @Override
    public String getUniqueId() {
        return parent.getUniqueId() + "#" + feature.getFeatureMethod().getName();
    }

    @Override
    public String getDisplayName() {
        return feature.getDisplayName();
    }

    @Override
    public Optional<Method> getTestMethod() {
        return Optional.of((Method) feature.getReflection());
    }

    public FeatureInfo getFeature() {
        return feature;
    }
}
