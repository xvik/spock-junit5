package ru.vyarus.spock.jupiter.test


import org.junit.jupiter.api.extension.ExtendWith
import ru.vyarus.spock.jupiter.AbstractTest
import ru.vyarus.spock.jupiter.support.ActionHolder
import ru.vyarus.spock.jupiter.support.MultiTestStorageAccess
import spock.lang.Requires
import spock.lang.Specification

/**
 * @author Ken Davidson
 * @since 18.11.2022
 */
@Requires({ AbstractTest.ACTIVE })
@ExtendWith(MultiTestStorageAccess.class)
class SpockMultiStorageSecond extends Specification {

  def "Sample test"() {
    when:
    ActionHolder.add("test.second")

    then:
    true
  }
}