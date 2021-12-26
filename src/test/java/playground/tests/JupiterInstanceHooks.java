package playground.tests;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import ru.vyarus.spock.jupiter.support.ActionHolder;
import ru.vyarus.spock.jupiter.support.PostConstructExtension;
import ru.vyarus.spock.jupiter.support.PreDestroyExtension;
import ru.vyarus.spock.jupiter.support.TestBoundaryExtension;

/**
 * @author Vyacheslav Rusakov
 * @since 27.12.2021
 */
@Disabled
@ExtendWith({PostConstructExtension.class, TestBoundaryExtension.class})
public class JupiterInstanceHooks {

    @Test
    @ExtendWith(PreDestroyExtension.class)
    void sampleTest() {
        ActionHolder.add("test.body");
    }
}
