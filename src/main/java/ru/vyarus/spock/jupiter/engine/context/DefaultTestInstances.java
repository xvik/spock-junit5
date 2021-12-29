package ru.vyarus.spock.jupiter.engine.context;

import org.junit.jupiter.api.extension.TestInstances;
import org.junit.platform.commons.util.Preconditions;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.ListIterator;
import java.util.Optional;

/**
 * Default {@link TestInstances} implementation. Note that in context of spock there can't be a chain of instances
 * (because there are no nested tests), so there will always be only one instance.
 * <p>
 * Copy of {@code org.junit.jupiter.engine.execution.DefaultTestInstances} from junit-jupiter-engine.
 *
 * @author Vyacheslav Rusakov
 * @since 22.12.2021
 */
public final class DefaultTestInstances implements TestInstances {

    private final List<Object> instances;

    private DefaultTestInstances(final List<Object> instances) {
        this.instances = Preconditions.notEmpty(instances, "instances must not be empty");
    }

    public static DefaultTestInstances of(final Object instance) {
        return new DefaultTestInstances(Collections.singletonList(instance));
    }

    public static DefaultTestInstances of(final TestInstances testInstances, final Object instance) {
        final List<Object> allInstances = new ArrayList<>(testInstances.getAllInstances());
        allInstances.add(instance);
        return new DefaultTestInstances(Collections.unmodifiableList(allInstances));
    }

    @Override
    public Object getInnermostInstance() {
        return instances.get(instances.size() - 1);
    }

    @Override
    public List<Object> getEnclosingInstances() {
        return instances.subList(0, instances.size() - 1);
    }

    @Override
    public List<Object> getAllInstances() {
        return instances;
    }

    @Override
    public <T> Optional<T> findInstance(final Class<T> requiredType) {
        Preconditions.notNull(requiredType, "requiredType must not be null");
        final ListIterator<Object> iterator = instances.listIterator(instances.size());
        while (iterator.hasPrevious()) {
            final Object instance = iterator.previous();
            if (requiredType.isInstance(instance)) {
                return Optional.of(requiredType.cast(instance));
            }
        }
        return Optional.empty();
    }
}
