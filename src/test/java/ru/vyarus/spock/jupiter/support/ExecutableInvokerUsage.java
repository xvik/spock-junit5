package ru.vyarus.spock.jupiter.support;

import org.assertj.core.util.Preconditions;
import org.junit.jupiter.api.extension.BeforeTestExecutionCallback;
import org.junit.jupiter.api.extension.ExtensionContext;

/**
 * @author Vyacheslav Rusakov
 * @since 02.09.2022
 */
public class ExecutableInvokerUsage implements BeforeTestExecutionCallback {

    @Override
    public void beforeTestExecution(ExtensionContext context) throws Exception {
        // no-args constructor test
        context.getExecutableInvoker().invoke(Foo.class.getConstructor());

        Inst inst = context.getExecutableInvoker().invoke(Inst.class.getConstructor(Integer.class));

        // static and non-static methods
        ActionHolder.add("getStat==" + context.getExecutableInvoker().invoke(Inst.class.getMethod("getStat", Integer.class)));
        ActionHolder.add("get==" + context.getExecutableInvoker().invoke(Inst.class.getMethod("get", Integer.class), inst));

        Inst.Inn inn = context.getExecutableInvoker().invoke(Inst.Inn.class.getConstructor(Inst.class, Integer.class), inst);
        ActionHolder.add("get==" + context.getExecutableInvoker().invoke(Inst.Inn.class.getMethod("get", Integer.class), inn));
    }

    public static class Foo {
    }

    public static class Inst {
        private Integer in;

        public Inst(Integer in) {
            Preconditions.checkArgument(in != null, "Not null value required");
            this.in = in;
        }

        public int get(Integer in2) {
            Preconditions.checkArgument(in2 != null, "Not null value required");
            return in + in2;
        }

        public static int getStat(Integer in2) {
            Preconditions.checkArgument(in2 != null, "Not null value required");
            return in2;
        }

        public class Inn {
            private Integer in;

            public Inn(Integer in) {
                Preconditions.checkArgument(in != null, "Not null value required");
                this.in = in;
            }

            public int get(Integer in3) {
                Preconditions.checkArgument(in3 != null, "Not null value required");
                return Inst.this.in + in + in3;
            }
        }
    }
}
