package playground.tests.exceptions;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import ru.vyarus.spock.jupiter.support.ActionHolder;
import ru.vyarus.spock.jupiter.support.LifecycleExtension;
import ru.vyarus.spock.jupiter.support.LifecycleExtension2;
import ru.vyarus.spock.jupiter.support.exceptions.AfterEachError;

/**
 * @author Vyacheslav Rusakov
 * @since 30.12.2021
 */
@Disabled
@ExtendWith({LifecycleExtension.class, AfterEachError.class, LifecycleExtension2.class})
public class JupiterAfterEachError {

    @Test
    void sampleTest() {
        ActionHolder.add("test.body");
    }
}
