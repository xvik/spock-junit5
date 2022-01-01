package playground.tests.exceptions;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import ru.vyarus.spock.jupiter.support.ActionHolder;
import ru.vyarus.spock.jupiter.support.exceptions.ConditionError;

/**
 * @author Vyacheslav Rusakov
 * @since 02.01.2022
 */
@Disabled
@ExtendWith({ConditionError.class})
public class JupiterConditionError {

    @Test
    void sampleTest() {
        ActionHolder.add("test.body");
    }
}
