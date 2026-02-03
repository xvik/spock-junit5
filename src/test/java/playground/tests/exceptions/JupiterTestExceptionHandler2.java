package playground.tests.exceptions;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import ru.vyarus.spock.jupiter.support.LifecycleExtension;
import ru.vyarus.spock.jupiter.support.RethrowExceptionHandler;
import ru.vyarus.spock.jupiter.support.EatExceptionHandler;

/**
 * @author Vyacheslav Rusakov
 * @since 02.01.2022
 */
// exception handlers processing order is reversed
@ExtendWith({LifecycleExtension.class, EatExceptionHandler.class, RethrowExceptionHandler.class})
public class JupiterTestExceptionHandler2 {

    @Test
    void sampleTest() {
        Assertions.fail("assert fail");
    }
}
