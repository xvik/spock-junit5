package ru.vyarus.spock.jupiter.engine;

import org.junit.jupiter.api.extension.Extension;
import org.junit.platform.commons.logging.Logger;
import org.junit.platform.commons.logging.LoggerFactory;
import org.junit.platform.commons.util.Preconditions;
import org.junit.platform.commons.util.ReflectionUtils;

import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toCollection;
import static java.util.stream.Stream.concat;

/**
 * Holds all found junit extensions for target context. Registry is hierarchical: first spec-level registry created and
 * then feature-level registries. Extensions lookup performed by top-most registry and then by parent (if exists).
 * <p>
 * Based on {@code org.junit.jupiter.engine.extension.MutableExtensionRegistry} from junit-jupiter-engine (5.8).
 * Does not support auto-detected, default and synthetic extensions (no need).
 *
 * @author Vyacheslav Rusakov
 * @since 30.11.2021
 */
public class ExtensionRegistry {
    private final Logger logger = LoggerFactory.getLogger(ExtensionRegistry.class);

    private final ExtensionRegistry parent;

    private final Set<Class<? extends Extension>> registeredExtensionTypes = new LinkedHashSet<>();
    private final List<Extension> registeredExtensions = new ArrayList<>();

    public ExtensionRegistry(final ExtensionRegistry parent) {
        this.parent = parent;
    }

    /**
     * Stream all {@code Extensions} of the specified type that are present
     * in this registry.
     *
     * <p>Extensions in ancestors are ignored.
     *
     * @param extensionType the type of {@link Extension} to stream
     * @param <E>           extension type
     * @return stream of found extensions
     * @see #getReversedExtensions(Class)
     */
    public <E extends Extension> Stream<E> stream(final Class<E> extensionType) {
        if (this.parent == null) {
            return streamLocal(extensionType);
        }
        return concat(this.parent.stream(extensionType), streamLocal(extensionType));
    }

    /**
     * Get all {@code Extensions} of the specified type that are present
     * in this registry or one of its ancestors.
     *
     * @param extensionType the type of {@link Extension} to get
     * @param <E>           extension type
     * @return list of found extensions or empty list
     * @see #getReversedExtensions(Class)
     * @see #stream(Class)
     */
    public <E extends Extension> List<E> getExtensions(final Class<E> extensionType) {
        return stream(extensionType).collect(toCollection(ArrayList::new));
    }

    /**
     * Get all {@code Extensions} of the specified type that are present
     * in this registry or one of its ancestors, in reverse order.
     *
     * @param extensionType the type of {@link Extension} to get
     * @param <E>           extension type
     * @return reversed list of found extensions
     * @see #getExtensions(Class)
     * @see #stream(Class)
     */
    public <E extends Extension> List<E> getReversedExtensions(final Class<E> extensionType) {
        final List<E> extensions = getExtensions(extensionType);
        Collections.reverse(extensions);
        return extensions;
    }


    /**
     * Instantiate an extension of the given type using its default constructor
     * and register it in the registry.
     *
     * <p>A new {@link Extension} should not be registered if an extension of the
     * given type already exists in the registry or a parent registry.
     *
     * @param extensionType the type of extension to register
     */
    public void registerExtension(final Class<? extends Extension> extensionType) {
        if (!isAlreadyRegistered(extensionType)) {
            registerExtension(ReflectionUtils.newInstance(extensionType), null);
        }
    }

    /**
     * Register the supplied {@link Extension}, without checking if an extension
     * of that type has already been registered.
     * <p>
     * Semantics for Source
     *
     * <p>If an extension is registered <em>declaratively</em> via
     * {@link org.junit.jupiter.api.extension.ExtendWith @ExtendWith}, the
     * {@code source} and the {@code extension} should be the same object.
     * However, if an extension is registered <em>programmatically</em> via
     * {@link org.junit.jupiter.api.extension.RegisterExtension @RegisterExtension},
     * the {@code source} object should be the {@link java.lang.reflect.Field}
     * that is annotated with {@code @RegisterExtension}. Similarly, if an
     * extension is registered <em>programmatically</em> as a lambda expression
     * or method reference, the {@code source} object should be the underlying
     * {@link java.lang.reflect.Method} that implements the extension API.
     *
     * @param extension the extension to register; never {@code null}
     * @param source    the source of the extension
     */
    public void registerExtension(final Extension extension, final Object source) {
        Preconditions.notNull(extension, "Extension must not be null");

        final Class<? extends Extension> type = extension.getClass();
        validateExtensionType(type);

        logger.trace(() -> String.format("Registering extension [%s]%s", extension, buildSourceInfo(source)));

        this.registeredExtensions.add(extension);
        this.registeredExtensionTypes.add(type);
    }

    @SuppressWarnings("checkstyle:MultipleStringLiterals")
    private void validateExtensionType(final Class<? extends Extension> extension) {
        final int supported = (int) ExtensionUtils.SUPPORTED_EXTENSIONS.stream()
                .filter(ext -> ext.isAssignableFrom(extension))
                .count();

        final List<Class<? extends Extension>> unsupported = ExtensionUtils.UNSUPPORTED_EXTENSIONS.stream()
                .filter(ext -> ext.isAssignableFrom(extension))
                .collect(Collectors.toList());

        if (!unsupported.isEmpty()) {
            logger.warn(() -> "Extension " + extension.getName() + " implements not supported extension "
                    + "types: " + unsupported.stream()
                    .map(Class::getSimpleName)
                    .collect(Collectors.joining(", ")));
        }

        if (supported == 0) {
            // in this case extension would be useless
            throw new IllegalStateException("Extension " + extension.getName() + " does not use any of supported "
                    + "extension types: " + ExtensionUtils.SUPPORTED_EXTENSIONS.stream()
                    .map(Class::getSimpleName)
                    .collect(Collectors.joining(", ")));
        }
    }

    private String buildSourceInfo(final Object source) {
        if (source == null) {
            return "";
        }
        final String res;
        if (source instanceof Member) {
            final Member member = (Member) source;
            final Object type = (member instanceof Method ? "method" : "field");
            res = String.format("%s %s.%s", type, member.getDeclaringClass().getName(), member.getName());
        } else {
            res = source.toString();
        }
        return " from source [" + res + "]";
    }

    /**
     * Determine if the supplied type is already registered in this registry or in a
     * parent registry.
     */
    private boolean isAlreadyRegistered(final Class<? extends Extension> extensionType) {
        return (this.registeredExtensionTypes.contains(extensionType)
                || (this.parent != null && this.parent.isAlreadyRegistered(extensionType)));
    }

    /**
     * Stream all {@code Extensions} of the specified type that are present
     * in this registry.
     *
     * <p>Extensions in ancestors are ignored.
     *
     * @param extensionType the type of {@link Extension} to stream
     * @see #getReversedExtensions(Class)
     */
    private <E extends Extension> Stream<E> streamLocal(final Class<E> extensionType) {
        return this.registeredExtensions.stream()
                .filter(extensionType::isInstance)
                .map(extensionType::cast);
    }
}
