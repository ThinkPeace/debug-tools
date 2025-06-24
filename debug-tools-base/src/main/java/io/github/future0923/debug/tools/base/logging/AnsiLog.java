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
package io.github.future0923.debug.tools.base.logging;

import java.util.logging.Level;
import java.util.regex.Matcher;

/**
 * @author future0923
 */
public class AnsiLog {

    public static java.util.logging.Level LEVEL = java.util.logging.Level.CONFIG;

    private static final String TRACE_COLOR_PREFIX = "[" + ColorConsole.getBlue("INFO") + "] ";
    private static final String DEBUG_COLOR_PREFIX = "[" + ColorConsole.getBlueGreen("DEBUG") + "] ";
    private static final String INFO_COLOR_PREFIX = "[" + ColorConsole.getGreen("INFO") + "] ";
    private static final String WARN_COLOR_PREFIX = "[" + ColorConsole.getYellow("WARN") + "] ";
    private static final String ERROR_COLOR_PREFIX = "[" + ColorConsole.getRed("ERROR") + "] ";

    public static Level level(Level level) {
        Level old = LEVEL;
        LEVEL = level;
        return old;
    }

    public static Level level() {
        return LEVEL;
    }

    public static void trace(String msg) {
        if (canLog(Level.FINEST)) {
            System.out.println(TRACE_COLOR_PREFIX + msg);
        }
    }

    public static void trace(String format, Object... arguments) {
        if (canLog(Level.FINEST)) {
            trace(format(format, arguments));
        }
    }

    public static void trace(Throwable t) {
        if (canLog(Level.FINEST)) {
            t.printStackTrace(System.out);
        }
    }

    public static void debug(String msg) {
        if (canLog(Level.FINER)) {
            System.out.println(DEBUG_COLOR_PREFIX + msg);
        }
    }

    public static void debug(String format, Object... arguments) {
        if (canLog(Level.FINER)) {
            debug(format(format, arguments));
        }
    }

    public static void debug(Throwable t) {
        if (canLog(Level.FINER)) {
            t.printStackTrace(System.out);
        }
    }

    public static void info(String msg) {
        if (canLog(Level.CONFIG)) {
            System.out.println(INFO_COLOR_PREFIX + msg);
        }
    }

    public static void info(String format, Object... arguments) {
        if (canLog(Level.CONFIG)) {
            info(format(format, arguments));
        }
    }

    public static void info(Throwable t) {
        if (canLog(Level.CONFIG)) {
            t.printStackTrace(System.out);
        }
    }

    public static void warn(String msg) {
        if (canLog(Level.WARNING)) {
            System.out.println(WARN_COLOR_PREFIX + msg);
        }
    }

    public static void warn(String format, Object... arguments) {
        if (canLog(Level.WARNING)) {
            warn(format(format, arguments));
        }
    }

    public static void warn(Throwable t) {
        if (canLog(Level.WARNING)) {
            t.printStackTrace(System.out);
        }
    }

    public static void error(String msg) {
        if (canLog(Level.SEVERE)) {
            System.out.println(ERROR_COLOR_PREFIX + msg);
        }
    }

    public static void error(String format, Object... arguments) {
        if (canLog(Level.SEVERE)) {
            error(format(format, arguments));
        }
    }

    public static void error(Throwable t) {
        if (canLog(Level.SEVERE)) {
            t.printStackTrace(System.out);
        }
    }

    private static String format(String from, Object... arguments) {
        if (from != null) {
            String computed = from;
            if (arguments != null && arguments.length != 0) {
                for (Object argument : arguments) {
                    computed = computed.replaceFirst("\\{\\}", Matcher.quoteReplacement(String.valueOf(argument)));
                }
            }
            return computed;
        }
        return null;
    }

    private static boolean canLog(Level level) {
        return level.intValue() >= LEVEL.intValue();
    }
}
