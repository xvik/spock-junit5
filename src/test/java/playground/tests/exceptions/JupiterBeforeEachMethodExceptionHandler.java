package playground.tests.exceptions;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import ru.vyarus.spock.jupiter.support.ActionHolder;
import ru.vyarus.spock.jupiter.support.LifecycleExtension;
import ru.vyarus.spock.jupiter.support.RethrowLifecycleMethodsExceptionHandler;
import ru.vyarus.spock.jupiter.support.EatLifecycleMethodExceptionHandler;

/**
 * @author Vyacheslav Rusakov
 * @since 03.02.2026
 */
@Disabled
// handlers processed in reverse order
@ExtendWith({LifecycleExtension.class, EatLifecycleMethodExceptionHandler.class, RethrowLifecycleMethodsExceptionHandler.class})
public class JupiterBeforeEachMethodExceptionHandler {

    @BeforeEach
    void setUp() {
        throw new IllegalStateException("problem");
    }

    @Test
    void sampleTest() {
        ActionHolder.add("test.body");
    }
}
