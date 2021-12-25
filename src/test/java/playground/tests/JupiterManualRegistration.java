package playground.tests;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import ru.vyarus.spock.jupiter.support.ActionHolder;
import ru.vyarus.spock.jupiter.support.LifecycleExtension;
import ru.vyarus.spock.jupiter.support.LifecycleExtension2;

/**
 * @author Vyacheslav Rusakov
 * @since 26.12.2021
 */
@Disabled
public class JupiterManualRegistration {

    @RegisterExtension
    static LifecycleExtension ext = new LifecycleExtension();

    @RegisterExtension
    LifecycleExtension2 ext2 = new LifecycleExtension2();

    @Test
    void sampleTest() {
        ActionHolder.add("test.body");
    }
}
