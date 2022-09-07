package playground;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import playground.tests.JupiterExecutableInvoker;

import java.util.Arrays;

/**
 * @author Vyacheslav Rusakov
 * @since 02.09.2022
 */
public class JupiterExecutableInvokerTest extends AbstractJupiterTest {

    @Test
    void testParams() {
        Assertions.assertEquals(runTest(JupiterExecutableInvoker.class),
                Arrays.asList(
                        "ParameterExtension ru.vyarus.spock.jupiter.support.ExecutableInvokerUsage$Inst",
                        "ParameterExtension getStat",
                        "getStat==11",
                        "ParameterExtension get",
                        "get==22",
                        "ParameterExtension ru.vyarus.spock.jupiter.support.ExecutableInvokerUsage$Inst$Inn",
                        "ParameterExtension get",
                        "get==33"
                ));
    }
}
