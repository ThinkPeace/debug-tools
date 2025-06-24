/*
 * Copyright (C) 2024-2025 the original author or authors.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
package io.github.future0923.debug.tools.hotswap.core.javassist;

import io.github.future0923.debug.tools.hotswap.core.javassist.CtMethod.ConstParameter;
import io.github.future0923.debug.tools.hotswap.core.javassist.bytecode.Bytecode;
import io.github.future0923.debug.tools.hotswap.core.javassist.bytecode.ClassFile;
import io.github.future0923.debug.tools.hotswap.core.javassist.bytecode.Descriptor;

class CtNewWrappedConstructor extends CtNewWrappedMethod {
    private static final int PASS_NONE = CtNewConstructor.PASS_NONE;
    // private static final int PASS_ARRAY = CtNewConstructor.PASS_ARRAY;
    private static final int PASS_PARAMS = CtNewConstructor.PASS_PARAMS;

    public static CtConstructor wrapped(CtClass[] parameterTypes,
                                        CtClass[] exceptionTypes,
                                        int howToCallSuper,
                                        CtMethod body,
                                        ConstParameter constParam,
                                        CtClass declaring)
        throws CannotCompileException
    {
        try {
            CtConstructor cons = new CtConstructor(parameterTypes, declaring);
            cons.setExceptionTypes(exceptionTypes);
            Bytecode code = makeBody(declaring, declaring.getClassFile2(),
                                     howToCallSuper, body,
                                     parameterTypes, constParam);
            cons.getMethodInfo2().setCodeAttribute(code.toCodeAttribute());
            // a stack map table is not needed.
            return cons;
        }
        catch (NotFoundException e) {
            throw new CannotCompileException(e);
        }
    }

    protected static Bytecode makeBody(CtClass declaring, ClassFile classfile,
                                       int howToCallSuper,
                                       CtMethod wrappedBody,
                                       CtClass[] parameters,
                                       ConstParameter cparam)
        throws CannotCompileException
    {
        int stacksize, stacksize2;

        int superclazz = classfile.getSuperclassId();
        Bytecode code = new Bytecode(classfile.getConstPool(), 0, 0);
        code.setMaxLocals(false, parameters, 0);
        code.addAload(0);
        if (howToCallSuper == PASS_NONE) {
            stacksize = 1;
            code.addInvokespecial(superclazz, "<init>", "()V");
        }
        else if (howToCallSuper == PASS_PARAMS) {
            stacksize = code.addLoadParameters(parameters, 1) + 1;
            code.addInvokespecial(superclazz, "<init>",
                                  Descriptor.ofConstructor(parameters));
        }
        else {
            stacksize = compileParameterList(code, parameters, 1);
            String desc;
            if (cparam == null) {
                stacksize2 = 2;
                desc = ConstParameter.defaultConstDescriptor();
            }
            else {
                stacksize2 = cparam.compile(code) + 2;
                desc = cparam.constDescriptor();
            }

            if (stacksize < stacksize2)
                stacksize = stacksize2;

            code.addInvokespecial(superclazz, "<init>", desc);
        }

        if (wrappedBody == null)
            code.add(Bytecode.RETURN);
        else {
            stacksize2 = makeBody0(declaring, classfile, wrappedBody,
                                   false, parameters, CtClass.voidType,
                                   cparam, code);
            if (stacksize < stacksize2)
                stacksize = stacksize2;
        }

        code.setMaxStack(stacksize);
        return code;
    }
}
