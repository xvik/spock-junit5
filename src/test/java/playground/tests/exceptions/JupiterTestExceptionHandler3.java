package playground.tests.exceptions;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import ru.vyarus.spock.jupiter.support.LifecycleExtension;
import ru.vyarus.spock.jupiter.support.RethrowExceptionHandler;

/**
 * @author Vyacheslav Rusakov
 * @since 03.02.2026
 */
@Disabled
// exception handlers processing order is reversed, exception hot handled (rethrowed)
@ExtendWith({LifecycleExtension.class, RethrowExceptionHandler.class})
public class JupiterTestExceptionHandler3 {

    @Test
    void sampleTest() {
        Assertions.fail("assert fail");
    }
}
