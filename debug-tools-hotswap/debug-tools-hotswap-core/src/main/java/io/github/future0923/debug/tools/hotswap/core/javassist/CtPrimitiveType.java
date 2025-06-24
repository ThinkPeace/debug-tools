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


/**
 * An instance of <code>CtPrimitiveType</code> represents a primitive type.
 * It is obtained from <code>CtClass</code>.
 */
public final class CtPrimitiveType extends CtClass {
    private char descriptor;
    private String wrapperName;
    private String getMethodName;
    private String mDescriptor;
    private int returnOp;
    private int arrayType;
    private int dataSize;

    CtPrimitiveType(String name, char desc, String wrapper,
                    String methodName, String mDesc, int opcode, int atype,
                    int size) {
        super(name);
        descriptor = desc;
        wrapperName = wrapper;
        getMethodName = methodName;
        mDescriptor = mDesc;
        returnOp = opcode;
        arrayType = atype;
        dataSize = size;
    }

    /**
     * Returns <code>true</code> if this object represents a primitive
     * Java type: boolean, byte, char, short, int, long, float, double,
     * or void.
     */
    @Override
    public boolean isPrimitive() { return true; }

    /**
     * Returns the modifiers for this type.
     * For decoding, use <code>javassist.Modifier</code>.
     *
     * @see Modifier
     */
    @Override
    public int getModifiers() {
        return Modifier.PUBLIC | Modifier.FINAL;
    }

    /**
     * Returns the descriptor representing this type.
     * For example, if the type is int, then the descriptor is I.
     */
    public char getDescriptor() { return descriptor; }

    /**
     * Returns the name of the wrapper class.
     * For example, if the type is int, then the wrapper class is
     * <code>java.lang.Integer</code>.
     */
    public String getWrapperName() { return wrapperName; }

    /**
     * Returns the name of the method for retrieving the value
     * from the wrapper object.
     * For example, if the type is int, then the method name is
     * <code>intValue</code>.
     */
    public String getGetMethodName() { return getMethodName; }

    /**
     * Returns the descriptor of the method for retrieving the value
     * from the wrapper object.
     * For example, if the type is int, then the method descriptor is
     * <code>()I</code>.
     */
    public String getGetMethodDescriptor() { return mDescriptor; }

    /**
     * Returns the opcode for returning a value of the type.
     * For example, if the type is int, then the returned opcode is
     * <code>javassit.bytecode.Opcode.IRETURN</code>.
     */
    public int getReturnOp() { return returnOp; }

    /**
     * Returns the array-type code representing the type.
     * It is used for the newarray instruction.
     * For example, if the type is int, then this method returns
     * <code>javassit.bytecode.Opcode.T_INT</code>.
     */
    public int getArrayType() { return arrayType; }

    /**
     * Returns the data size of the primitive type.
     * If the type is long or double, this method returns 2.
     * Otherwise, it returns 1.
     */
    public int getDataSize() { return dataSize; }
}
