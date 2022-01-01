package playground.tests.exceptions;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import ru.vyarus.spock.jupiter.support.ActionHolder;
import ru.vyarus.spock.jupiter.support.LifecycleExtension;
import ru.vyarus.spock.jupiter.support.exceptions.ParameterError2;

/**
 * @author Vyacheslav Rusakov
 * @since 01.01.2022
 */
@Disabled
@ExtendWith({LifecycleExtension.class, ParameterError2.class})
public class JupiterParameterError2 {

    @Test
    void sampleTest(Integer a) {
        ActionHolder.add("test.body " + a);
    }
}
