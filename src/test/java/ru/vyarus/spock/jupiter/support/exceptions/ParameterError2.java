package ru.vyarus.spock.jupiter.support.exceptions;

import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.api.extension.ParameterResolutionException;
import org.junit.jupiter.api.extension.ParameterResolver;
import ru.vyarus.spock.jupiter.support.ActionHolder;

/**
 * @author Vyacheslav Rusakov
 * @since 01.01.2022
 */
public class ParameterError2 implements ParameterResolver {

    @Override
    public boolean supportsParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
        ActionHolder.add("ParameterExtension " + parameterContext.getDeclaringExecutable().getName());
        return parameterContext.getParameter().getType().equals(Integer.class);
    }

    @Override
    public Object resolveParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
        throw new IllegalStateException("problem");
    }
}
