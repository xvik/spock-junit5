package playground;

import org.junit.platform.testkit.engine.EngineTestKit;
import ru.vyarus.spock.jupiter.support.ActionHolder;

import java.util.List;

import static org.junit.platform.engine.discovery.DiscoverySelectors.selectClass;

/**
 * @author Vyacheslav Rusakov
 * @since 26.11.2021
 */
public abstract class AbstractJupiterTest {

    /**
     * Run provided test using junit test-kit to collect all events.
     * Test MUST BE disabled!
     *
     * @param test test class to execute
     * @return list of appeared events
     */
    public List<String> runTest(Class test) {
        ActionHolder.cleanup();
        try {
            EngineTestKit
                    .engine("junit-jupiter")
                    .configurationParameter("junit.jupiter.conditions.deactivate", "org.junit.*DisabledCondition")
                    .selectors(selectClass(test))
                    .execute()
                    .containerEvents()
                    .assertStatistics(stats -> stats.failed(0).aborted(0));
            return ActionHolder.getState();
        } finally {
            ActionHolder.cleanup();
        }
    }
}
