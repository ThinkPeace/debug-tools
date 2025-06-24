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
package io.github.future0923.debug.tools.hotswap.core.watch;

import io.github.future0923.debug.tools.base.utils.DebugToolsOSUtils;
import io.github.future0923.debug.tools.hotswap.core.watch.nio.TreeWatcherNIO;
import io.github.future0923.debug.tools.hotswap.core.watch.nio.WatcherNIO2;

import java.io.IOException;

/**
 * 观察者工厂
 */
public class WatcherFactory {

    public static double JAVA_VERSION = getVersion();

    static double getVersion() {
        String version = System.getProperty("java.version");

        int pos = 0;
        boolean decimalPart = false;

        for (; pos < version.length(); pos++) {
            char c = version.charAt(pos);
            if ((c < '0' || c > '9') && c != '.') break;
            if (c == '.') {
                if (decimalPart) break;
                decimalPart = true;
            }
        }
        return Double.parseDouble(version.substring(0, pos));
    }

    public Watcher getWatcher() throws IOException {
        if (JAVA_VERSION >= 1.7) {
            if (DebugToolsOSUtils.isWindows()) {
                return new TreeWatcherNIO();
            } else {
                return new WatcherNIO2();
            }
        } else {
            throw new UnsupportedOperationException("Watcher is implemented only for Java 1.7 (NIO2). " +
                    "JNotify implementation should be added in the future for older Java version support.");
        }

    }
}
