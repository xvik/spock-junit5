package playground.tests;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import ru.vyarus.spock.jupiter.support.ActionHolder;
import ru.vyarus.spock.jupiter.support.LifecycleExtension;
import ru.vyarus.spock.jupiter.support.LifecycleExtension2;
import ru.vyarus.spock.jupiter.support.ParameterExtension;

/**
 * @author Vyacheslav Rusakov
 * @since 25.12.2021
 */
@Disabled
public class JupiterSetupMethodExtensions {

    @BeforeEach
    @ExtendWith(LifecycleExtension.class) // ignored
    void setUp(@ExtendWith({ParameterExtension.class, LifecycleExtension2.class}) Integer arg) {
        ActionHolder.add("test.before " + arg);
    }

    @Test
    void sampleTest() {
        ActionHolder.add("test.body");
    }
}
