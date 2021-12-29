package playground.tests;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import ru.vyarus.spock.jupiter.support.ActionHolder;
import ru.vyarus.spock.jupiter.support.ContextAccess;

/**
 * @author Vyacheslav Rusakov
 * @since 30.12.2021
 */
@Disabled
@ExtendWith(ContextAccess.class)
public class JupiterContext {

    @Test
    void sampleTest() {
        ActionHolder.add("test.body");
    }
}
