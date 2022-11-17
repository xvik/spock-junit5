package playground.tests;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import ru.vyarus.spock.jupiter.support.ActionHolder;
import ru.vyarus.spock.jupiter.support.MultiTestStorageAccess;

/**
 * In this first test the counters should be 1, 2 and 3.
 *
 * @author Ken Davidson
 * @since 18.11.2022
 */
@Disabled
@ExtendWith(MultiTestStorageAccess.class)
public class JupiterMultiStorageFirst {

  @Test
  void sampleTest() {
    ActionHolder.add("test.first");
  }
}