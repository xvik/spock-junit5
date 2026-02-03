package playground.tests.exceptions;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import ru.vyarus.spock.jupiter.support.ActionHolder;
import ru.vyarus.spock.jupiter.support.LifecycleExtension;
import ru.vyarus.spock.jupiter.support.RethrowLifecycleMethodsExceptionHandler;

/**
 * @author Vyacheslav Rusakov
 * @since 03.02.2026
 */
@Disabled
// handlers processed in reverse order
@ExtendWith({LifecycleExtension.class, RethrowLifecycleMethodsExceptionHandler.class})
public class JupiterAfterAllMethodExceptionHandler2 {

    @AfterAll
    static void afterAll() {
        throw new IllegalStateException("problem");
    }

    @Test
    void sampleTest() {
        ActionHolder.add("test.body");
    }
}
