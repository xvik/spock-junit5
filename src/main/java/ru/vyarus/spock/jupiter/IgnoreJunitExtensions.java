package ru.vyarus.spock.jupiter;

import org.junit.jupiter.api.extension.Extension;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Provides an ability to ignore declared junit extensions. It might be used to temporarily disable some extensions
 * or to disable extensions that are handled by spock natively (like spock-spring).
 *
 * @author Vyacheslav Rusakov
 * @since 16.02.2026
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface IgnoreJunitExtensions {

    /**
     * @return list of junit extensions to ignore
     */
    Class<? extends Extension>[] value();
}
