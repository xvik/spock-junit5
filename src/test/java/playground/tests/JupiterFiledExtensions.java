package playground.tests;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import ru.vyarus.spock.jupiter.support.ActionHolder;
import ru.vyarus.spock.jupiter.support.LifecycleExtension;
import ru.vyarus.spock.jupiter.support.LifecycleExtension2;

/**
 * @author Vyacheslav Rusakov
 * @since 30.12.2021
 */
public class JupiterFiledExtensions {

    @ExtendWith(LifecycleExtension.class)
    static int a;

    @ExtendWith(LifecycleExtension2.class)
    int b;

    @Test
    void sampleTest() {
        ActionHolder.add("test.body");
    }
}
