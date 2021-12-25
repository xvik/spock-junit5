package playground.tests;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.extension.RegisterExtension;
import ru.vyarus.spock.jupiter.support.ActionHolder;
import ru.vyarus.spock.jupiter.support.LifecycleExtension;
import ru.vyarus.spock.jupiter.support.LifecycleExtension2;
import ru.vyarus.spock.jupiter.support.LifecycleExtension3;

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
    @ExtendWith(LifecycleExtension3.class)
    void sampleTest() {
        ActionHolder.add("test.body");
    }
}
