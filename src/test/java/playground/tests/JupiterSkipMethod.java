package playground.tests;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import ru.vyarus.spock.jupiter.support.ActionHolder;
import ru.vyarus.spock.jupiter.support.SkipCondition;

/**
 * @author Vyacheslav Rusakov
 * @since 27.12.2021
 */
@Disabled
public class JupiterSkipMethod {

    @Test
    @ExtendWith(SkipCondition.class)
    void sampleTest() {
        ActionHolder.add("test.body");
    }

    @Test
    void sampleTest2() {
        ActionHolder.add("test.body2");
    }
}
