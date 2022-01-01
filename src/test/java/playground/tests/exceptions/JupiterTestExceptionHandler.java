package playground.tests.exceptions;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import ru.vyarus.spock.jupiter.support.ActionHolder;
import ru.vyarus.spock.jupiter.support.LifecycleExtension;
import ru.vyarus.spock.jupiter.support.RethrowExceptionHandler;
import ru.vyarus.spock.jupiter.support.SwallowExceptionHandler;

/**
 * @author Vyacheslav Rusakov
 * @since 02.01.2022
 */
@Disabled
// exception handlers processing order is reversed
@ExtendWith({LifecycleExtension.class, SwallowExceptionHandler.class, RethrowExceptionHandler.class})
public class JupiterTestExceptionHandler {

    @Test
    void sampleTest() {
        throw new IllegalStateException("problem");
    }
}
