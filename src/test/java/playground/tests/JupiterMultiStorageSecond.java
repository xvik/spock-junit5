package playground.tests;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import ru.vyarus.spock.jupiter.support.ActionHolder;
import ru.vyarus.spock.jupiter.support.MultiTestStorageAccess;

/**
 * In this test the values should be 1, 1 and 2 due to the ROOT context not being
 * set on the second BeforeAll call.
 *
 * @author Ken Davidson
 * @since 18.11.2022
 */
@Disabled
@ExtendWith(MultiTestStorageAccess.class)
public class JupiterMultiStorageSecond {

  @Test
  void sampleTest() {
    ActionHolder.add("test.second");
  }
}