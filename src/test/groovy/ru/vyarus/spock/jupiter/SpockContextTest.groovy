package ru.vyarus.spock.jupiter

import ru.vyarus.spock.jupiter.test.SpockContext

/**
 * @author Vyacheslav Rusakov
 * @since 30.12.2021
 */
class SpockContextTest extends AbstractTest {

    def "Check context methods"() {

        expect: 'context methods ok'
        runTest(SpockContext) == ["class.id: [class:ru.vyarus.spock.jupiter.test.SpockContext]",
                                  "class.display name: SpockContext",
                                  "class.parent: false",
                                  "class.root: SpockContext",
                                  "class.element: class ru.vyarus.spock.jupiter.test.SpockContext",
                                  "class.lifecycle: PER_METHOD",
                                  "class.exec mode: SAME_THREAD",
                                  "class.exception: false",
                                  "class.test class: class ru.vyarus.spock.jupiter.test.SpockContext",
                                  "class.tags: []",
                                  "class.test instance: false",
                                  "class.test instances: false",

                                  "method.id: [class:ru.vyarus.spock.jupiter.test.SpockContext]/[method:Sample test]",
                                  "method.display name: Sample test",
                                  "method.parent: SpockContext",
                                  "method.root: SpockContext",
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
