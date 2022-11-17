package ru.vyarus.spock.jupiter

import ru.vyarus.spock.jupiter.test.SpockContext

/**
 * @author Vyacheslav Rusakov
 * @since 30.12.2021
 */
class SpockContextTest extends AbstractTest {

    def "Check context methods"() {

        expect: 'context methods ok'
        runTest(SpockContext) == ["root.id: [engine:spock]",
                                  "root.display name: Spock Engine Context",
                                  "root.parent: false",
                                  "root.root: Spock Engine Context",
                                  "root.lifecycle: false",
                                  "root.exec mode: SAME_THREAD",
                                  "root.exception: false",

                                  "class.id: [class:ru.vyarus.spock.jupiter.test.SpockContext]",
                                  "class.display name: SpockContext",
                                  "class.parent: true",
                                  "class.root: Spock Engine Context",
                                  "class.element: class ru.vyarus.spock.jupiter.test.SpockContext",
                                  "class.lifecycle: PER_METHOD",
                                  "class.exec mode: SAME_THREAD",
                                  "class.exception: false",
                                  "class.test class: class ru.vyarus.spock.jupiter.test.SpockContext",
                                  "class.test method: false",
                                  "class.tags: []",
                                  "class.test instance: false",
                                  "class.test instances: false",

                                  "method.id: [class:ru.vyarus.spock.jupiter.test.SpockContext]/[method:Sample test]",
                                  "method.display name: Sample test",
                                  "method.parent: SpockContext",
                                  "method.root: Spock Engine Context",
                                  "method.element: public void ru.vyarus.spock.jupiter.test.SpockContext.\$spock_feature_0_0()",
                                  "method.lifecycle: PER_METHOD",
                                  "method.exec mode: SAME_THREAD",
                                  "method.exception: false",
                                  "method.test class: class ru.vyarus.spock.jupiter.test.SpockContext",
                                  "method.test method: public void ru.vyarus.spock.jupiter.test.SpockContext.\$spock_feature_0_0()",
                                  "method.tags: []",
                                  "method.test instance: true",
                                  "method.test instances: 1",
                                  "test.body"]
    }
}
