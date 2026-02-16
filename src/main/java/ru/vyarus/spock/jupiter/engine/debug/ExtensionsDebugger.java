package ru.vyarus.spock.jupiter.engine.debug;

import org.junit.jupiter.api.extension.Extension;
import org.spockframework.runtime.model.MethodInfo;

import java.lang.reflect.Field;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * Debugger used to log extension registrations and execution.
 *
 * @author Vyacheslav Rusakov
 * @since 13.02.2026
 */
@SuppressWarnings({"checkstyle:MultipleStringLiterals", "PMD.AvoidDuplicateLiterals", "PMD.SystemPrintln"})
public class ExtensionsDebugger {

    private final boolean reg;
    private final boolean usage;
    private final boolean columnMode;

    /**
     * Create debugger.
     *
     * @param reg   true to log extensions registration
     * @param usage true to log extensions usage
     * @param columnMode true to print extensions in columns (one per line)
     */
    public ExtensionsDebugger(final boolean reg, final boolean usage, final boolean columnMode) {
        this.reg = reg;
        this.usage = usage;
        this.columnMode = columnMode;
    }

    /**
     * @param test       test class
     * @param extensions extensions declared on class
     */
    public void registeredClassExtensions(final Class<?> test,
                                          final List<Class<? extends Extension>> extensions) {
        if (!extensions.isEmpty()) {
            log(reg, "Registered test class (" + test.getSimpleName() + ") extensions:", () -> toString(extensions));
        }
    }

    /**
     * @param method     method (test or setup/cleanup)
     * @param extensions extensions declared on method
     */
    public void registeredMethodExtensions(final MethodInfo method,
                                           final List<Class<? extends Extension>> extensions) {
        if (!extensions.isEmpty()) {
            log(reg, "Registered test method (" + method.getReflection().getDeclaringClass().getSimpleName()
                    + "." + method.getName() + ") extensions:", () -> toString(extensions));
        }
    }

    /**
     * @param method     method (test of setup/cleanup)
     * @param extensions extensions declared on parameters
     */
    public void registeredMethodParametersExtensions(final MethodInfo method,
                                                     final List<Class<? extends Extension>> extensions) {
        if (!extensions.isEmpty()) {
            log(reg, "Registered test method (" + method.getReflection().getDeclaringClass().getSimpleName()
                    + "." + method.getName() + ") parameters extensions:", () -> toString(extensions));
        }
    }

    /**
     * @param field      static field
     * @param extensions extensions registered on static field
     */
    public void registeredStaticFieldExtensions(final Field field,
                                                final List<Class<? extends Extension>> extensions) {
        if (!extensions.isEmpty()) {
            log(reg, "Registered static field (" + field.getDeclaringClass().getSimpleName() + "." + field.getName()
                    + ") extensions:", () -> toString(extensions));
        }
    }

    /**
     * @param field      non-static filed
     * @param extensions extensions registered on field
     */
    public void registeredFieldExtensions(final Field field,
                                          final List<Class<? extends Extension>> extensions) {
        if (!extensions.isEmpty()) {
            log(reg, "Registered field (" + field.getDeclaringClass().getSimpleName() + "." + field.getName()
                    + ") extensions:", () -> toString(extensions));
        }
    }

    /**
     * @param extension  jupiter lifecycle interface
     * @param extensions executed extensions (might be not all registered extensions in some cases)
     * @param <T>        jupiter extension type
     */
    public <T extends Extension> void extensionsCalled(final Class<T> extension, final List<T> extensions) {
        if (!extensions.isEmpty()) {
            log(usage, extension.getSimpleName() + " extensions called:",
                    () -> toStringClasses(extensions));
        }
    }

    private void log(final boolean enabled, final String msg, final Supplier<String> action) {
        if (enabled) {
            System.out.println("[junit] " + msg + (columnMode ? "\n " : " ") + action.get());
        }
    }

    private String toString(final List<Class<? extends Extension>> extensions) {
        return extensions.stream().map(ext -> (columnMode ? "\t" : "") + ext.getSimpleName())
                .collect(Collectors.joining(columnMode ? "\n" : ", "));
    }

    @SuppressWarnings("unchecked")
    private String toStringClasses(final List<? extends Extension> extensions) {
        return toString((List<Class<? extends Extension>>) (List) extensions
                .stream().map(Extension::getClass).toList());
    }
}
