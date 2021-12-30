package ru.vyarus.spock.jupiter.test

import org.junit.jupiter.api.extension.ExtendWith
import ru.vyarus.spock.jupiter.AbstractTest
import ru.vyarus.spock.jupiter.support.ActionHolder
import ru.vyarus.spock.jupiter.support.StorageAccess
import spock.lang.Ignore
import spock.lang.Requires
import spock.lang.Specification

/**
 * @author Vyacheslav Rusakov
 * @since 30.12.2021
 */
@Requires({ AbstractTest.ACTIVE })
@ExtendWith(StorageAccess.class)
class SpockStorage extends Specification {

    def "Sample test"() {

        when:
        ActionHolder.add("test.body");

        then:
        true
    }
}
