package ru.vyarus.spock.jupiter.engine.context;

import org.junit.jupiter.api.extension.ExecutableInvoker;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.api.extension.ParameterResolutionException;
import org.junit.platform.commons.util.ReflectionUtils;
import org.spockframework.runtime.model.MethodInfo;
import ru.vyarus.spock.jupiter.engine.ExtensionUtils;

import java.lang.reflect.Constructor;
import java.lang.reflect.Executable;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Optional;

/**
 * Default {@link org.junit.jupiter.api.extension.ExecutableInvoker} implementation.
 * <p>
 * Based on {@code org.junit.jupiter.engine.execution.DefaultExecutableInvoker}.
 *
 * @author Vyacheslav Rusakov
 * @since 01.09.2022
 */
public class DefaultExecutableInvoker implements ExecutableInvoker {

    private final AbstractContext context;

    public DefaultExecutableInvoker(final AbstractContext context) {
        this.context = context;
    }

    @Override
    public <T> T invoke(final Constructor<T> constructor, final Object outerInstance) {
        // Ensure that the outer instance is resolved as the first parameter if
        // the executable is a constructor for an inner class.
        final Object[] arguments = collectArguments(constructor, null, outerInstance != null ? 1 : 0);
        if (outerInstance != null) {
            arguments[0] = outerInstance;
        }

        return ReflectionUtils.newInstance(constructor, arguments);
    }

    @Override
    public Object invoke(final Method method, final Object target) {
        final Object[] arguments = collectArguments(method, target, 0);
        return ReflectionUtils.invokeMethod(method, target, arguments);
    }

    @SuppressWarnings("PMD.CompareObjectsWithEquals")
    private Object[] collectArguments(final Executable executable, final Object target, final int start) {
        final Parameter[] parameters = executable.getParameters();
        if (parameters.length == 0) {
            return new Object[0];
        }
        final Object[] arguments = new Object[parameters.length];
        final Optional<Object> targetOptional = Optional.ofNullable(target);
        for (int i = start; i < arguments.length; i++) {
            // based on org.junit.jupiter.engine.execution.ExecutableInvoker.resolveParameter
            final Parameter param = parameters[i];
            final ParameterContext parameterContext = new DefaultParameterContext(param, i, targetOptional);
            arguments[i] = ExtensionUtils.resolveParameter(parameterContext, executable, context);
            // utility method was initially used for spock method arguments processing and so may return
            // missing argument to mark argument for processing by other spock extensions. In current context
            // it means error - no extensions provide required parameter
            if (arguments[i] == MethodInfo.MISSING_ARGUMENT) {
                throw new ParameterResolutionException(
                        String.format("No ParameterResolver registered for parameter [%s] in %s [%s].",
                                parameterContext.getParameter(), ExtensionUtils.asLabel(executable),
                                executable.toGenericString()));
            }
        }
        return arguments;
    }
}
