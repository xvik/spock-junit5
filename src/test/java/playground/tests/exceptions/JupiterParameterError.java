package playground.tests.exceptions;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import ru.vyarus.spock.jupiter.support.ActionHolder;
import ru.vyarus.spock.jupiter.support.LifecycleExtension;
import ru.vyarus.spock.jupiter.support.LifecycleExtension2;
import ru.vyarus.spock.jupiter.support.exceptions.BeforeTestError;
import ru.vyarus.spock.jupiter.support.exceptions.ParameterError;

/**
 * @author Vyacheslav Rusakov
 * @since 01.01.2022
 */
@Disabled
@ExtendWith({LifecycleExtension.class, ParameterError.class})
public class JupiterParameterError {

    @Test
    void sampleTest(Integer a) {
        ActionHolder.add("test.body " + a);
    }
}
