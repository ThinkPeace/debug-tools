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

import java.util.Map;

import io.github.future0923.debug.tools.hotswap.core.javassist.CtMethod.ConstParameter;
import io.github.future0923.debug.tools.hotswap.core.javassist.bytecode.AccessFlag;
import io.github.future0923.debug.tools.hotswap.core.javassist.bytecode.BadBytecode;
import io.github.future0923.debug.tools.hotswap.core.javassist.bytecode.Bytecode;
import io.github.future0923.debug.tools.hotswap.core.javassist.bytecode.ClassFile;
import io.github.future0923.debug.tools.hotswap.core.javassist.bytecode.MethodInfo;
import io.github.future0923.debug.tools.hotswap.core.javassist.bytecode.SyntheticAttribute;
import io.github.future0923.debug.tools.hotswap.core.javassist.compiler.JvstCodeGen;

class CtNewWrappedMethod {

    private static final String addedWrappedMethod = "_added_m$";

    public static CtMethod wrapped(CtClass returnType, String mname,
                                   CtClass[] parameterTypes,
                                   CtClass[] exceptionTypes,
                                   CtMethod body, ConstParameter constParam,
                                   CtClass declaring)
        throws CannotCompileException
    {
        CtMethod mt = new CtMethod(returnType, mname, parameterTypes,
                                   declaring);
        mt.setModifiers(body.getModifiers());
        try {
            mt.setExceptionTypes(exceptionTypes);
        }
        catch (NotFoundException e) {
            throw new CannotCompileException(e);
        }

        Bytecode code = makeBody(declaring, declaring.getClassFile2(), body,
                                 parameterTypes, returnType, constParam);
        MethodInfo minfo = mt.getMethodInfo2();
        minfo.setCodeAttribute(code.toCodeAttribute());
        // a stack map has been already created. 
        return mt;
    }

    static Bytecode makeBody(CtClass clazz, ClassFile classfile,
                             CtMethod wrappedBody,
                             CtClass[] parameters,
                             CtClass returnType,
                             ConstParameter cparam)
        throws CannotCompileException
    {
        boolean isStatic = Modifier.isStatic(wrappedBody.getModifiers());
        Bytecode code = new Bytecode(classfile.getConstPool(), 0, 0);
        int stacksize = makeBody0(clazz, classfile, wrappedBody, isStatic,
                                  parameters, returnType, cparam, code);
        code.setMaxStack(stacksize);
        code.setMaxLocals(isStatic, parameters, 0);
        return code;
    }

    /* The generated method body does not need a stack map table
     * because it does not contain a branch instruction.
     */
    protected static int makeBody0(CtClass clazz, ClassFile classfile,
                                   CtMethod wrappedBody,
                                   boolean isStatic, CtClass[] parameters,
                                   CtClass returnType, ConstParameter cparam,
                                   Bytecode code)
        throws CannotCompileException
    {
        if (!(clazz instanceof CtClassType))
            throw new CannotCompileException("bad declaring class"
                                             + clazz.getName());

        if (!isStatic)
            code.addAload(0);

        int stacksize = compileParameterList(code, parameters,
                                             (isStatic ? 0 : 1));
        int stacksize2;
        String desc;
        if (cparam == null) {
            stacksize2 = 0;
            desc = ConstParameter.defaultDescriptor();
        }
        else {
            stacksize2 = cparam.compile(code);
            desc = cparam.descriptor();
        }

        checkSignature(wrappedBody, desc);

        String bodyname;
        try {
            bodyname = addBodyMethod((CtClassType)clazz, classfile,
                                     wrappedBody);
            /* if an exception is thrown below, the method added above
             * should be removed. (future work :<)
             */
        }
        catch (BadBytecode e) {
            throw new CannotCompileException(e);
        }

        if (isStatic)
            code.addInvokestatic(Bytecode.THIS, bodyname, desc);
        else
            code.addInvokespecial(Bytecode.THIS, bodyname, desc);

        compileReturn(code, returnType);        // consumes 2 stack entries

        if (stacksize < stacksize2 + 2)
            stacksize = stacksize2 + 2;

        return stacksize;
    }

    private static void checkSignature(CtMethod wrappedBody,
                                       String descriptor)
        throws CannotCompileException
    {
        if (!descriptor.equals(wrappedBody.getMethodInfo2().getDescriptor()))
            throw new CannotCompileException(
                        "wrapped method with a bad signature: "
                        + wrappedBody.getDeclaringClass().getName()
                        + '.' + wrappedBody.getName());
    }

    private static String addBodyMethod(CtClassType clazz,
                                        ClassFile classfile,
                                        CtMethod src)
        throws BadBytecode, CannotCompileException
    {
        Map<CtMethod,String> bodies = clazz.getHiddenMethods();
        String bodyname = bodies.get(src);
        if (bodyname == null) {
            do {
                bodyname = addedWrappedMethod + clazz.getUniqueNumber();
            } while (classfile.getMethod(bodyname) != null);
            ClassMap map = new ClassMap();
            map.put(src.getDeclaringClass().getName(), clazz.getName());
            MethodInfo body = new MethodInfo(classfile.getConstPool(),
                                             bodyname, src.getMethodInfo2(),
                                             map);
            int acc = body.getAccessFlags();
            body.setAccessFlags(AccessFlag.setPrivate(acc));
            body.addAttribute(new SyntheticAttribute(classfile.getConstPool()));
            // a stack map is copied.  rebuilding it is not needed.
            classfile.addMethod(body);
            bodies.put(src, bodyname);
            CtMember.Cache cache = clazz.hasMemberCache();
            if (cache != null)
                cache.addMethod(new CtMethod(body, clazz));
        }

        return bodyname;
    }

    /* compileParameterList() returns the stack size used
     * by the produced code.
     *
     * @param regno     the index of the local variable in which
     *                  the first argument is received.
     *                  (0: static method, 1: regular method.)
     */
    static int compileParameterList(Bytecode code,
                                    CtClass[] params, int regno) {
        return JvstCodeGen.compileParameterList(code, params, regno);
    }

    /*
     * The produced codes cosume 1 or 2 stack entries.
     */
    private static void compileReturn(Bytecode code, CtClass type) {
        if (type.isPrimitive()) {
            CtPrimitiveType pt = (CtPrimitiveType)type;
            if (pt != CtClass.voidType) {
                String wrapper = pt.getWrapperName();
                code.addCheckcast(wrapper);
                code.addInvokevirtual(wrapper, pt.getGetMethodName(),
                                      pt.getGetMethodDescriptor());
            }

            code.addOpcode(pt.getReturnOp());
        }
        else {
            code.addCheckcast(type);
            code.addOpcode(Bytecode.ARETURN);
        }
    }
}
