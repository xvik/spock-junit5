package ru.vyarus.spock.jupiter.support;

import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.api.extension.ParameterResolutionException;
import org.junit.jupiter.api.extension.ParameterResolver;

/**
 * @author Vyacheslav Rusakov
 * @since 03.01.2022
 */
public class ParamContextAccess implements ParameterResolver {

    @Override
    public boolean supportsParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
        ActionHolder.add("param.name " + parameterContext.getParameter().getName());
        ActionHolder.add("param.exec " + parameterContext.getDeclaringExecutable().getName());
        ActionHolder.add("param.index " + parameterContext.getIndex());
        ActionHolder.add("param.target " + parameterContext.getTarget().get().getClass().getSimpleName());
        parameterContext.toString();
        ActionHolder.add("param.annotation " + parameterContext.isAnnotated(ExtendWith.class) + " "
                + parameterContext.findAnnotation(ExtendWith.class).isPresent() + " "
                + parameterContext.findRepeatableAnnotations(ExtendWith.class).size());
        return true;
    }

    @Override
    public Object resolveParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
        return 12;
    }
}
