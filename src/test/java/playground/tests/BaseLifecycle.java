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

/**
 * @author Vyacheslav Rusakov
 * @since 26.11.2021
 */
@Disabled
@ExtendWith(LifecycleExtension.class)
public class BaseLifecycle {

    @BeforeAll
    static void beforeAll() {
        ActionHolder.add("test.beforeAll");
    }

    @AfterAll
    static void afterAll() {
        ActionHolder.add("test.afterAll");
    }

    @BeforeEach
    void setUp() {
        ActionHolder.add("test.before");
    }

    @AfterEach
    void tearDown() {
        ActionHolder.add("test.after");
    }

    @Test
    void sampleTest() {
        ActionHolder.add("test.body");
    }
}
