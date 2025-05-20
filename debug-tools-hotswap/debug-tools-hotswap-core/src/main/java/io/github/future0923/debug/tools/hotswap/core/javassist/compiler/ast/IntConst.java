/*
 * Copyright 2024-2025 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.github.future0923.debug.tools.hotswap.core.javassist.compiler.ast;

import io.github.future0923.debug.tools.hotswap.core.javassist.compiler.CompileError;
import io.github.future0923.debug.tools.hotswap.core.javassist.compiler.TokenId;

/**
 * Integer constant.
 */
public class IntConst extends ASTree {
    /** default serialVersionUID */
    private static final long serialVersionUID = 1L;
    protected long value;
    protected int type;

    public IntConst(long v, int tokenId) { value = v; type = tokenId; }

    public long get() { return value; }

    public void set(long v) { value = v; }

    /* Returns IntConstant, CharConstant, or LongConstant.
     */
    public int getType() { return type; }

    @Override
    public String toString() { return Long.toString(value); }

    @Override
    public void accept(Visitor v) throws CompileError {
        v.atIntConst(this);
    }

    public ASTree compute(int op, ASTree right) {
        if (right instanceof IntConst)
            return compute0(op, (IntConst)right);
        else if (right instanceof DoubleConst)
            return compute0(op, (DoubleConst)right);
        else
            return null;
    }

    private IntConst compute0(int op, IntConst right) {
        int type1 = this.type;
        int type2 = right.type;
        int newType;
        if (type1 == TokenId.LongConstant || type2 == TokenId.LongConstant)
            newType = TokenId.LongConstant;
        else if (type1 == TokenId.CharConstant
                 && type2 == TokenId.CharConstant)
            newType = TokenId.CharConstant;
        else
            newType = TokenId.IntConstant;

        long value1 = this.value;
        long value2 = right.value;
        long newValue;
        switch (op) {
        case '+' :
            newValue = value1 + value2;
            break;
        case '-' :
            newValue = value1 - value2;
            break;
        case '*' :
            newValue = value1 * value2;
            break;
        case '/' :
            newValue = value1 / value2;
            break;
        case '%' :
            newValue = value1 % value2;
            break;
        case '|' :
            newValue = value1 | value2;
            break;
        case '^' :
            newValue = value1 ^ value2;
            break;
        case '&' :
            newValue = value1 & value2;
            break;
        case TokenId.LSHIFT :
            newValue = value << (int)value2;
            newType = type1;
            break;
        case TokenId.RSHIFT :
            newValue = value >> (int)value2;
            newType = type1;
            break;
        case TokenId.ARSHIFT :
            newValue = value >>> (int)value2;
            newType = type1;
            break;
        default :
            return null;
        }

        return new IntConst(newValue, newType);
    }

    private DoubleConst compute0(int op, DoubleConst right) {
        double value1 = this.value;
        double value2 = right.value;
        double newValue;
        switch (op) {
        case '+' :
            newValue = value1 + value2;
            break;
        case '-' :
            newValue = value1 - value2;
            break;
        case '*' :
            newValue = value1 * value2;
            break;
        case '/' :
            newValue = value1 / value2;
            break;
        case '%' :
            newValue = value1 % value2;
            break;
        default :
            return null;
        }

        return new DoubleConst(newValue, right.type);
    }
}
