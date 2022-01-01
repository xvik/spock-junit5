package playground.tests.exceptions;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import ru.vyarus.spock.jupiter.support.ActionHolder;
import ru.vyarus.spock.jupiter.support.LifecycleExtension;
import ru.vyarus.spock.jupiter.support.PostConstructExtension;
import ru.vyarus.spock.jupiter.support.exceptions.PostProcessorError;

/**
 * @author Vyacheslav Rusakov
 * @since 01.01.2022
 */
@Disabled
@ExtendWith({LifecycleExtension.class, PostProcessorError.class, PostConstructExtension.class})
public class JupiterPostProcessorError {

    @Test
    void sampleTest() {
        ActionHolder.add("test.body");
    }
}
