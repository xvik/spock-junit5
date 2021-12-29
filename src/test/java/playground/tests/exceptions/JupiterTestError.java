package playground.tests.exceptions;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import ru.vyarus.spock.jupiter.support.ActionHolder;
import ru.vyarus.spock.jupiter.support.LifecycleExtension;

/**
 * @author Vyacheslav Rusakov
 * @since 30.12.2021
 */
@ExtendWith(LifecycleExtension.class)
public class JupiterTestError {

    @Test
    void sampleTest() {
        throw new IllegalStateException("problem");
    }
}
