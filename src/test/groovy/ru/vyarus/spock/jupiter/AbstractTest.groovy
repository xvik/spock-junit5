package ru.vyarus.spock.jupiter


import ru.vyarus.spock.jupiter.support.ActionHolder
import spock.lang.Specification
import spock.util.EmbeddedSpecRunner

import java.util.logging.LogManager

/**
 * Base class for tests.
 *
 * @author Vyacheslav Rusakov
 * @since 25.11.2021
 */
abstract class AbstractTest extends Specification {

    static {
        // configure JUL
        try (InputStream is = AbstractTest.class.getClassLoader().
                getResourceAsStream("logging.properties")) {
            LogManager.getLogManager().readConfiguration(is);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    List<String> runTest(Class test) {
        ActionHolder.cleanup();
        try {
            def runner = new EmbeddedSpecRunner()
            runner.runClass(test)
            return ActionHolder.getState();
        } finally {
            ActionHolder.cleanup();
        }
    }
}
