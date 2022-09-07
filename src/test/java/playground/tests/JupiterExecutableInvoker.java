package playground.tests;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import ru.vyarus.spock.jupiter.support.ExecutableInvokerUsage;
import ru.vyarus.spock.jupiter.support.ParameterExtension;

/**
 * @author Vyacheslav Rusakov
 * @since 02.09.2022
 */
@Disabled
@ExtendWith({ParameterExtension.class, ExecutableInvokerUsage.class})
public class JupiterExecutableInvoker {

    @Test
    void test() {}
}
