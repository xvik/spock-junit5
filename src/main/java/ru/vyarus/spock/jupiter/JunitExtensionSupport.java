package ru.vyarus.spock.jupiter;

import org.junit.jupiter.api.extension.ConditionEvaluationResult;
import org.junit.platform.commons.logging.Logger;
import org.junit.platform.commons.logging.LoggerFactory;
import org.spockframework.runtime.extension.IGlobalExtension;
import org.spockframework.runtime.model.SpecInfo;
import ru.vyarus.spock.jupiter.engine.ExtensionRegistry;
import ru.vyarus.spock.jupiter.engine.ExtensionUtils;
import ru.vyarus.spock.jupiter.engine.context.ClassContext;
import ru.vyarus.spock.jupiter.engine.execution.ConditionEvaluator;
import ru.vyarus.spock.jupiter.interceptor.ExtensionLifecycleMerger;

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

        final ClassContext specContext = new ClassContext(registry, spec);

        if (ConditionEvaluator.skip(spec, specContext)) {
            // skip execution based on junit's ExecutionCondition
            return;
        }

        // note: method-level (feature) extensions are collected just before feature execution because
        // in case of data-providers same feature would be executed several times and each time extensions
        // must be renewed (including field-based instances registered with @RegisterExtensions).

        // https://spockframework.org/spock/docs/2.0/extensions.html
        final ExtensionLifecycleMerger interceptor = new ExtensionLifecycleMerger(specContext);
        spec.addSetupSpecInterceptor(interceptor);
        spec.addInitializerInterceptor(interceptor);
        spec.addSetupInterceptor(interceptor);
        spec.addCleanupInterceptor(interceptor);
        spec.addCleanupSpecInterceptor(interceptor);

        // add support for custom parameters on setup(spec)/cleanup(spec) methods
        spec.getAllFixtureMethods().forEach(methodInfo ->
                methodInfo.addInterceptor(interceptor.getFixtureMethodsInterceptor()));

        // intercept test methods (inject parameters and before/after execution hooks)
        spec.getAllFeatures().forEach(featureInfo -> featureInfo.getFeatureMethod().addInterceptor(interceptor));
    }
}
