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
package io.github.future0923.debug.tools.hotswap.core.javassist.bytecode.annotation;

import java.io.IOException;
import java.lang.reflect.Method;

import io.github.future0923.debug.tools.hotswap.core.javassist.ClassPool;
import io.github.future0923.debug.tools.hotswap.core.javassist.bytecode.BadBytecode;
import io.github.future0923.debug.tools.hotswap.core.javassist.bytecode.ConstPool;
import io.github.future0923.debug.tools.hotswap.core.javassist.bytecode.Descriptor;
import io.github.future0923.debug.tools.hotswap.core.javassist.bytecode.SignatureAttribute;

/**
 * Class value.
 *
 * @author <a href="mailto:bill@jboss.org">Bill Burke</a>
 * @author Shigeru Chiba
 */
public class ClassMemberValue extends MemberValue {
    int valueIndex;

    /**
     * Constructs a class value.  The initial value is specified
     * by the constant pool entry at the given index.
     *
     * @param index the index of a CONSTANT_Utf8_info structure.
     */
    public ClassMemberValue(int index, ConstPool cp) {
        super('c', cp);
        this.valueIndex = index;
    }

    /**
     * Constructs a class value.
     *
     * @param className         the initial value.
     */
    public ClassMemberValue(String className, ConstPool cp) {
        super('c', cp);
        setValue(className);
    }

    /**
     * Constructs a class value.
     * The initial value is java.lang.Class.
     */
    public ClassMemberValue(ConstPool cp) {
        super('c', cp);
        setValue("java.lang.Class");
    }

    @Override
    Object getValue(ClassLoader cl, ClassPool cp, Method m)
            throws ClassNotFoundException {
        final String classname = getValue();
        if (classname.equals("void"))
            return void.class;
        else if (classname.equals("int"))
            return int.class;
        else if (classname.equals("byte"))
            return byte.class;
        else if (classname.equals("long"))
            return long.class;
        else if (classname.equals("double"))
            return double.class;
        else if (classname.equals("float"))
            return float.class;
        else if (classname.equals("char"))
            return char.class;
        else if (classname.equals("short"))
            return short.class;
        else if (classname.equals("boolean"))
            return boolean.class;
        else
            return loadClass(cl, classname);
    }

    @Override
    Class<?> getType(ClassLoader cl) throws ClassNotFoundException {
        return loadClass(cl, "java.lang.Class");
    }

    /**
     * Obtains the value of the member.
     *
     * @return fully-qualified class name.
     */
    public String getValue() {
        String v = cp.getUtf8Info(valueIndex);
        try {
            return SignatureAttribute.toTypeSignature(v).jvmTypeName();
        } catch (BadBytecode e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Sets the value of the member.
     *
     * @param newClassName      fully-qualified class name.
     */
    public void setValue(String newClassName) {
        String setTo = Descriptor.of(newClassName);
        valueIndex = cp.addUtf8Info(setTo);
    }

    /**
     * Obtains the string representation of this object.
     */
    @Override
    public String toString() {
        return getValue().replace('$', '.') + ".class";
    }

    /**
     * Writes the value.
     */
    @Override
    public void write(AnnotationsWriter writer) throws IOException {
        writer.classInfoIndex(cp.getUtf8Info(valueIndex));
    }

    /**
     * Accepts a visitor.
     */
    @Override
    public void accept(MemberValueVisitor visitor) {
        visitor.visitClassMemberValue(this);
    }
}
