package ru.vyarus.spock.jupiter


import ru.vyarus.spock.jupiter.test.SpockExecutableInvoker

/**
 * @author Vyacheslav Rusakov
 * @since 02.09.2022
 */
class SpockExecutableInvokerTest extends AbstractTest {

    def "Check executable invoker"() {

        expect: 'executable invoker works'
        runTest(SpockExecutableInvoker) == ['ParameterExtension ru.vyarus.spock.jupiter.support.ExecutableInvokerUsage$Inst',
                                            'ParameterExtension getStat',
                                            'getStat==11',
                                            'ParameterExtension get',
                                            'get==22',
                                            'ParameterExtension ru.vyarus.spock.jupiter.support.ExecutableInvokerUsage$Inst$Inn',
                                            'ParameterExtension get',
                                            "get==33"]
    }
}
