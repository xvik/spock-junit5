package ru.vyarus.spock.jupiter;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Prints Junit extensions registration and called junit extensions. Could be used for behavior debugging.
 * Usage: annotate spock test.
 * <p>
 * Registration or execution messages could be disabled.
 * <p>
 * By default, extensions would be printed as a list (one-line log). After enabling {@link #columnMode()} each
 * extension would be printed on a separate line.
 *
 * @author Vyacheslav Rusakov
 * @since 13.02.2026
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface DebugJunitExtensions {

    /**
     * @return true to print extensions registrations, false to avoid registrations messages
     */
    boolean registrations() default true;

    /**
     * @return true to print extensions execution, false to avoid usage messages
     */
    boolean usage() default true;

    /**
     * @return true to print extension one per line, false for a comma-separated list of extensions
     */
    boolean columnMode() default false;
}
