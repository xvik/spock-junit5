package playground.tests.exceptions;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.extension.RegisterExtension;
import ru.vyarus.spock.jupiter.support.ActionHolder;
import ru.vyarus.spock.jupiter.support.LifecycleExtension;

/**
 * @author Vyacheslav Rusakov
 * @since 02.02.2026
 */
@Disabled
public class JupiterInvalidManualRegistration {

    @ExtendWith(LifecycleExtension.class)
    @RegisterExtension
    LifecycleExtension ext = new LifecycleExtension();

    @Test
    void sampleTest() {
        ActionHolder.add("test.body");
    }
}
