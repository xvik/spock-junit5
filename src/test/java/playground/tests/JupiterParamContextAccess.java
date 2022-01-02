package playground.tests;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import ru.vyarus.spock.jupiter.support.ActionHolder;
import ru.vyarus.spock.jupiter.support.ParamContextAccess;

/**
 * @author Vyacheslav Rusakov
 * @since 03.01.2022
 */
@Disabled
@ExtendWith(ParamContextAccess.class)
public class JupiterParamContextAccess {

    @Test
    void sampleTest(@ExtendWith({}) Integer arg) {
        ActionHolder.add("test.body " + arg);
    }
}
