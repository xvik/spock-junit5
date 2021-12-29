package playground;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import playground.tests.JupiterContext;

import java.util.Arrays;

/**
 * @author Vyacheslav Rusakov
 * @since 30.12.2021
 */
public class JupiterContextTest extends AbstractJupiterTest {

    @Test
    void testContextMethods() {
        Assertions.assertEquals(runTest(JupiterContext.class),
                Arrays.asList(
                        "class.id: [engine:junit-jupiter]/[class:playground.tests.JupiterContext]",
                        "class.display name: JupiterContext",
                        "class.parent: true",
                        "class.root: JUnit Jupiter",
                        "class.element: class playground.tests.JupiterContext",
                        "class.lifecycle: PER_METHOD",
                        "class.exec mode: SAME_THREAD",
                        "class.exception: false",
                        "class.test class: class playground.tests.JupiterContext",
                        "class.test method: false",
                        "class.tags: []",
                        "class.test instance: false",
                        "class.test instances: false",

                        "method.id: [engine:junit-jupiter]/[class:playground.tests.JupiterContext]/[method:sampleTest()]",
                        "method.display name: sampleTest()",
                        "method.parent: JupiterContext",
                        "method.root: JUnit Jupiter",
                        "method.element: void playground.tests.JupiterContext.sampleTest()",
                        "method.lifecycle: PER_METHOD",
                        "method.exec mode: SAME_THREAD",
                        "method.exception: false",
                        "method.test class: class playground.tests.JupiterContext",
                        "method.test method: void playground.tests.JupiterContext.sampleTest()",
                        "method.tags: []",
                        "method.test instance: true",
                        "method.test instances: 1",
                        "test.body"));
    }
}
