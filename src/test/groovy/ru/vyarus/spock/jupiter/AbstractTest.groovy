package ru.vyarus.spock.jupiter;

import spock.lang.Specification

/**
 * Base class for tests.
 *
 * @author Vyacheslav Rusakov
 * @since 25.11.2021
 */
abstract class AbstractTest extends Specification {

    void setup() {
        // todo: do test setup here
    }

    void cleanup() {
        // todo: do test cleanup here
    }
}
