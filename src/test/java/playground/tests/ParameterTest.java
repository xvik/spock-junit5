package playground.tests;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import ru.vyarus.spock.jupiter.support.ActionHolder;
import ru.vyarus.spock.jupiter.support.LifecycleExtension;
import ru.vyarus.spock.jupiter.support.ParameterExtension;

/**
 * @author Vyacheslav Rusakov
 * @since 23.12.2021
 */
@Disabled
@ExtendWith({LifecycleExtension.class, ParameterExtension.class})
public class ParameterTest {

    @BeforeAll
    static void beforeAll(Integer arg) {
        ActionHolder.add("test.beforeAll " + arg);
    }

    @AfterAll
    static void afterAll(Integer arg) {
        ActionHolder.add("test.afterAll " + arg);
    }

    @BeforeEach
    void setUp(Integer arg) {
        ActionHolder.add("test.before " + arg);
    }

    @AfterEach
    void tearDown(Integer arg) {
        ActionHolder.add("test.after " + arg);
    }

    @Test
    void sampleTest(Integer arg) {
        ActionHolder.add("test.body " + arg);
    }
}
