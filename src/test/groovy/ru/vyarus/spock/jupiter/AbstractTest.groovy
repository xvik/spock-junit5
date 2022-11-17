package ru.vyarus.spock.jupiter

import org.junit.platform.engine.TestExecutionResult
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

    static Boolean ACTIVE = false

    static {
        // configure JUL
        try (InputStream is = AbstractTest.class.getClassLoader().
                getResourceAsStream("logging.properties")) {
            LogManager.getLogManager().readConfiguration(is)
        } catch (IOException e) {
            e.printStackTrace()
        }
    }

    List<String> runTest(Class... tests) {
        ActionHolder.cleanup()
        ACTIVE = true
        try {
            def runner = new EmbeddedSpecRunner()
            // do not rethrow exception - all errors will remain in holder
            runner.throwFailure = false
            runner.runClasses(Arrays.asList(tests))
                    .allEvents().failed().stream()
                    // exceptions appended to events log
                    .forEach(event -> {
                        Throwable err = event.getPayload(TestExecutionResult.class).get().getThrowable().get()
                        err.printStackTrace()
                        ActionHolder.add("Error: (" + err.getClass().getSimpleName() + ") " + err.getMessage())
                    })
//                    .containerEvents()
//                    .assertStatistics(stats -> stats.failed(0).aborted(0));
            return ActionHolder.getState()
        } finally {
            ACTIVE = false
            ActionHolder.cleanup()
        }
    }
}
