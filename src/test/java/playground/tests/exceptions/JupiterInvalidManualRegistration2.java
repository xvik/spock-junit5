package playground.tests.exceptions;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import ru.vyarus.spock.jupiter.support.ActionHolder;

/**
 * @author Vyacheslav Rusakov
 * @since 02.02.2026
 */
@Disabled
public class JupiterInvalidManualRegistration2 {

    @RegisterExtension
    Integer ext = 12;

    @Test
    void sampleTest() {
        ActionHolder.add("test.body");
    }
}
