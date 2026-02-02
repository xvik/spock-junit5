package playground;

import org.junit.platform.engine.DiscoverySelector;
import org.junit.platform.engine.TestExecutionResult;
import org.junit.platform.engine.discovery.DiscoverySelectors;
import org.junit.platform.testkit.engine.EngineTestKit;
import ru.vyarus.spock.jupiter.support.ActionHolder;
import ru.vyarus.spock.jupiter.util.JupiterResultFile;

import java.util.Arrays;
import java.util.List;

/**
 * @author Vyacheslav Rusakov
 * @since 26.11.2021
 */
public abstract class AbstractJupiterTest {

    /**
     * Run provided test using junit test-kit to collect all events.
     * Test MUST BE disabled!
     *
     * @param tests test classes to execute
     * @return list of appeared events
     */
    public List<String> runTest(Class<?>... tests) {
        ActionHolder.cleanup();
        try {
            EngineTestKit
                    .engine("junit-jupiter")
                    .configurationParameter("junit.jupiter.conditions.deactivate", "org.junit.*DisabledCondition")
                    .selectors(Arrays.stream(tests).map(DiscoverySelectors::selectClass).toArray(DiscoverySelector[]::new))
                    .execute().allEvents().failed().stream()
                    // exceptions appended to events log
                    .forEach(event -> {
                        Throwable err = event.getPayload(TestExecutionResult.class).get().getThrowable().get();
                        ActionHolder.add("Error: (" + err.getClass().getSimpleName() + ") " + err.getMessage());
                    });
//                    .containerEvents()
//                    .assertStatistics(stats -> stats.failed(0).aborted(0));
            return writeState(ActionHolder.getState(), tests);
        } finally {
            ActionHolder.cleanup();
        }
    }

    private List<String> writeState(List<String> state, Class<?>... tests) {
        JupiterResultFile.store(state, tests);
        return state;
    }
}
