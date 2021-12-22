package ru.vyarus.spock.jupiter.engine.context;

import org.junit.jupiter.api.extension.TestInstances;
import org.junit.platform.commons.util.Preconditions;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.ListIterator;
import java.util.Optional;

/**
 * Copy of {@code org.junit.jupiter.engine.execution.DefaultTestInstances} from junit-jupiter-engine.
 *
 * @author Vyacheslav Rusakov
 * @since 22.12.2021
 */
public class DefaultTestInstances implements TestInstances {

    public static DefaultTestInstances of(Object instance) {
        return new DefaultTestInstances(Collections.singletonList(instance));
    }

    public static DefaultTestInstances of(TestInstances testInstances, Object instance) {
        List<Object> allInstances = new ArrayList<>(testInstances.getAllInstances());
        allInstances.add(instance);
        return new DefaultTestInstances(Collections.unmodifiableList(allInstances));
    }

    private final List<Object> instances;

    private DefaultTestInstances(List<Object> instances) {
        this.instances = Preconditions.notEmpty(instances, "instances must not be empty");
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
    public <T> Optional<T> findInstance(Class<T> requiredType) {
        Preconditions.notNull(requiredType, "requiredType must not be null");
        ListIterator<Object> iterator = instances.listIterator(instances.size());
        while (iterator.hasPrevious()) {
            Object instance = iterator.previous();
            if (requiredType.isInstance(instance)) {
                return Optional.of(requiredType.cast(instance));
            }
        }
        return Optional.empty();
    }

}
