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
package io.github.future0923.debug.tools.hotswap.core.javassist;

import io.github.future0923.debug.tools.hotswap.core.javassist.compiler.CompileError;

/**
 * Thrown when bytecode transformation has failed.
 */
public class CannotCompileException extends Exception {
    /** default serialVersionUID */
    private static final long serialVersionUID = 1L;
    private Throwable myCause;

    /**
     * Gets the cause of this throwable.
     * It is for JDK 1.3 compatibility.
     */
    @Override
    public synchronized Throwable getCause() {
        return (myCause == this ? null : myCause);
    }

    /**
     * Initializes the cause of this throwable.
     * It is for JDK 1.3 compatibility.
     */
    @Override
    public synchronized Throwable initCause(Throwable cause) {
        myCause = cause;
        return this;
    }

    private String message;

    /**
     * Gets a long message if it is available.
     */
    public String getReason() {
        if (message != null)
            return message;
        return this.toString();
    }

    /**
     * Constructs a CannotCompileException with a message.
     *
     * @param msg       the message.
     */
    public CannotCompileException(String msg) {
        super(msg);
        message = msg;
        initCause(null);
    }

    /**
     * Constructs a CannotCompileException with an <code>Exception</code>
     * representing the cause.
     *
     * @param e     the cause.
     */
    public CannotCompileException(Throwable e) {
        super("by " + e.toString());
        message = null;
        initCause(e);
    }

    /**
     * Constructs a CannotCompileException with a detailed message
     * and an <code>Exception</code> representing the cause.
     *
     * @param msg   the message.
     * @param e     the cause.
     */
    public CannotCompileException(String msg, Throwable e) {
        this(msg);
        initCause(e);
    }

    /**
     * Constructs a CannotCompileException with a
     * <code>NotFoundException</code>.
     */
    public CannotCompileException(NotFoundException e) {
        this("cannot find " + e.getMessage(), e);
    }

    /**
     * Constructs a CannotCompileException with an <code>CompileError</code>.
     */
    public CannotCompileException(CompileError e) {
        this("[source error] " + e.getMessage(), e);
    }

    /**
     * Constructs a CannotCompileException
     * with a <code>ClassNotFoundException</code>.
     */
    public CannotCompileException(ClassNotFoundException e, String name) {
        this("cannot find " + name, e);
    }

    /**
     * Constructs a CannotCompileException with a ClassFormatError.
     */
    public CannotCompileException(ClassFormatError e, String name) {
        this("invalid class format: " + name, e);
    }
}
