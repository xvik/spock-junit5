package ru.vyarus.spock.jupiter.engine.context;

import org.junit.jupiter.api.extension.TestInstances;
import org.spockframework.runtime.model.FeatureInfo;
import ru.vyarus.spock.jupiter.engine.ExtensionRegistry;

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
    private final TestInstances instances;

    public MethodContext(ClassContext parent,
                         ExtensionRegistry registry,
                         FeatureInfo feature,
                         Object testInstance) {
        super(parent, registry, feature.getFeatureMethod().getReflection(), parent.getSpec());
        this.feature = feature;
        instances = DefaultTestInstances.of(testInstance);
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

    @Override
    public Optional<Object> getTestInstance() {
        return getTestInstances().map(TestInstances::getInnermostInstance);
    }

    @Override
    public Optional<TestInstances> getTestInstances() {
        return Optional.ofNullable(instances);
    }

    public FeatureInfo getFeature() {
        return feature;
    }
}
