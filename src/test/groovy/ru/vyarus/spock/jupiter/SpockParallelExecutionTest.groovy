package ru.vyarus.spock.jupiter

import org.junit.platform.engine.TestExecutionResult
import org.spockframework.runtime.model.parallel.ExecutionMode
import ru.vyarus.spock.jupiter.test.SpockParallelStorageUsage
import ru.vyarus.spock.jupiter.test.SpockParallelStoreExtension
import spock.lang.Specification
import spock.util.EmbeddedSpecRunner

/**
 * @author Vyacheslav Rusakov
 * @since 09.01.2022
 */
class SpockParallelExecutionTest extends Specification {

    def "Check parallel execution"() {

        setup:
        Set<String> expect = ["spock.visitSpecAnnotation"]
        10.times {expect.addAll(["ext ${it + 1}", "test ${it + 1}"])}

        SpockParallelStoreExtension.RES.clear() // ActionHolder can't be used due to multiple threads involved
        def specRunner = new EmbeddedSpecRunner()
        specRunner.throwFailure = false
        specRunner.configurationScript {
            runner {
                parallel {
                    enabled true
                    defaultExecutionMode ExecutionMode.CONCURRENT
                    defaultSpecificationExecutionMode ExecutionMode.CONCURRENT
                }
            }
        }

        when:
        specRunner.runClass(SpockParallelStorageUsage)
                .allEvents().failed().list()
        // exceptions appended to events log
                .each {
                    Throwable err = it.getPayload(TestExecutionResult.class).get().getThrowable().get();
                    err.printStackTrace()
                    SpockParallelStoreExtension.RES.add("Error: (" + err.getClass().getSimpleName() + ") " + err.getMessage());
                }


        then: "parallel execution ok"
        SpockParallelStoreExtension.RES as Set == expect
    }
}
