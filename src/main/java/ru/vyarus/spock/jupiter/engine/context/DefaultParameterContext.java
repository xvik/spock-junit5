package ru.vyarus.spock.jupiter.engine.context;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.platform.commons.util.AnnotationUtils;
import org.junit.platform.commons.util.Preconditions;
import org.junit.platform.commons.util.ToStringBuilder;

import java.lang.annotation.Annotation;
import java.lang.reflect.Parameter;
import java.util.List;
import java.util.Optional;

/**
 * {@link ParameterContext} implementation (used for {@link org.junit.jupiter.api.extension.ParameterResolver}
 * extensions to resolve method parameters).
 * <p>
 * Copy of {@code org.junit.jupiter.engine.execution.DefaultParameterContext} from junit-jupiter-engine (5.8).
 *
 * @author Vyacheslav Rusakov
 * @since 24.12.2021
 */
public class DefaultParameterContext implements ParameterContext {

    private final Parameter parameter;
    private final int index;
    private final Optional<Object> target;

    @SuppressFBWarnings("CT_CONSTRUCTOR_THROW")
    public DefaultParameterContext(final Parameter parameter, final int index, final Optional<Object> target) {
        Preconditions.condition(index >= 0, "index must be greater than or equal to zero");
        this.parameter = Preconditions.notNull(parameter, "parameter must not be null");
        this.index = index;
        this.target = Preconditions.notNull(target, "target must not be null");
    }

    @Override
    public Parameter getParameter() {
        return this.parameter;
    }

    @Override
    public int getIndex() {
        return this.index;
    }

    @Override
    public Optional<Object> getTarget() {
        return this.target;
    }

    @Override
    public boolean isAnnotated(final Class<? extends Annotation> annotationType) {
        return AnnotationUtils.isAnnotated(this.parameter, this.index, annotationType);
    }

    @Override
    public <A extends Annotation> Optional<A> findAnnotation(final Class<A> annotationType) {
        return AnnotationUtils.findAnnotation(this.parameter, this.index, annotationType);
    }

    @Override
    public <A extends Annotation> List<A> findRepeatableAnnotations(final Class<A> annotationType) {
        return AnnotationUtils.findRepeatableAnnotations(this.parameter, this.index, annotationType);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("parameter", this.parameter)
                .append("index", this.index)
                .append("target", this.target)
                .toString();
    }
}
