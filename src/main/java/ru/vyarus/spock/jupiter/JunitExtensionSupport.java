package ru.vyarus.spock.jupiter;

import org.junit.platform.commons.logging.Logger;
import org.junit.platform.commons.logging.LoggerFactory;
import org.spockframework.runtime.extension.IGlobalExtension;
import org.spockframework.runtime.model.FeatureInfo;
import org.spockframework.runtime.model.SpecInfo;
import ru.vyarus.spock.jupiter.engine.ExtensionRegistry;
import ru.vyarus.spock.jupiter.engine.ExtensionUtils;
import ru.vyarus.spock.jupiter.engine.context.ClassContext;
import ru.vyarus.spock.jupiter.engine.context.MethodContext;
import ru.vyarus.spock.jupiter.interceptor.ExtensionLifecycleMerger;

import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Vyacheslav Rusakov
 * @since 25.11.2021
 */
public class JunitExtensionSupport implements IGlobalExtension {

    private final Logger logger = LoggerFactory.getLogger(JunitExtensionSupport.class);

    @Override
    public void visitSpec(final SpecInfo spec) {
        // based on org.junit.jupiter.engine.descriptor.ClassBasedTestDescriptor.prepare from junit-jupiter-engine (5.8)
        final Class<?> testClass = spec.getReflection();
        final ExtensionRegistry registry = ExtensionUtils.createRegistry(testClass);

        // Register extensions from static fields here, at the class level but
        // after extensions registered via @ExtendWith.
        ExtensionUtils.registerExtensionsFromFields(registry, testClass, null);

        // register extensions from setupSpec, setup, cleanup, cleanupSpec method parameters
        // (see org.junit.jupiter.engine.descriptor.ClassBasedTestDescriptor.prepare for reference logic)
        spec.getAllFixtureMethods().forEach(methodInfo -> ExtensionUtils
                .registerExtensionsFromExecutableParameters(registry, methodInfo.getReflection()));

        final ClassContext specContext = new ClassContext(null, registry, testClass, spec);

        // parse all methods immediately to reveal possible problems
        final Map<AnnotatedElement, MethodContext> methods = new HashMap<>();
        for (FeatureInfo feature : spec.getAllFeatures()) {
            // support method-level extensions
            final Method method = feature.getFeatureMethod().getReflection();
            final ExtensionRegistry methodRegistry = ExtensionUtils.createMethodRegistry(registry, method);
            ExtensionUtils.registerExtensionsFromExecutableParameters(methodRegistry, method);
            methods.put(method, new MethodContext(specContext, methodRegistry, method, feature));
        }

        // https://spockframework.org/spock/docs/2.0/extensions.html
        final ExtensionLifecycleMerger interceptor = new ExtensionLifecycleMerger(specContext, methods);
        spec.addSetupSpecInterceptor(interceptor);
        spec.addSetupInterceptor(interceptor);
        spec.addCleanupInterceptor(interceptor);
        spec.addCleanupSpecInterceptor(interceptor);

        // intercept methods
        spec.getAllFeatures().forEach(featureInfo -> featureInfo.getFeatureMethod().addInterceptor(interceptor));

        //todo method analysis org.junit.jupiter.engine.descriptor.TestMethodTestDescriptor.populateNewExtensionRegistry
    }
}
