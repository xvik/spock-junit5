package ru.vyarus.spock.jupiter.test

import org.junit.jupiter.api.extension.ExtendWith
import ru.vyarus.spock.jupiter.AbstractTest
import ru.vyarus.spock.jupiter.support.LifecycleExtension2
import spock.lang.Requires

/**
 * @author Vyacheslav Rusakov
 * @since 23.12.2021
 */
@Requires({ AbstractTest.ACTIVE })
@ExtendWith(LifecycleExtension2)
class SpockDoubleLifecycle extends SpockBaseLifecycle {
}
