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
package io.github.future0923.debug.tools.hotswap.core.util.classloader;

import io.github.future0923.debug.tools.base.logging.Logger;
import io.github.future0923.debug.tools.base.utils.DebugToolsStringUtils;
import io.github.future0923.debug.tools.hotswap.core.util.ReflectionHelper;

import java.io.IOException;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.Enumeration;

/**
 * Utility method for classloaders.
 */
public class ClassLoaderHelper {
    private static Logger LOGGER = Logger.getLogger(ClassLoaderHelper.class);

    public static Method findLoadedClass;

    static {
        try {
            findLoadedClass = ClassLoader.class.getDeclaredMethod("findLoadedClass", new Class[] { String.class });
            findLoadedClass.setAccessible(true);
        } catch (NoSuchMethodException e) {
            LOGGER.error("Unexpected: failed to get ClassLoader findLoadedClass method", e);
        }
    }


    /**
     * Check if the class was already loaded by the classloader. It does not try to load the class
     * (opposite to Class.forName()).
     *
     * @param classLoader classLoader to check
     * @param className fully qualified class name
     * @return true if the class was loaded
     */
    public static boolean isClassLoaded(ClassLoader classLoader, String className) {
        try {
            return findLoadedClass.invoke(classLoader, className) != null;
        } catch (Exception e) {
            LOGGER.error("Unable to invoke findLoadedClass on classLoader {}, className {}", e, classLoader, className);
            return false;
        }
    }

    /**
     * Some class loader has activity state. e.g. WebappClassLoader must be started before it can be used
     *
     * @param classLoader the class loader
     * @return true, if is class loader active
     */
    public static boolean isClassLoaderStarted(ClassLoader classLoader) {

        String classLoaderClassName = (classLoader != null) ? classLoader.getClass().getName() : null;

        // TODO: use interface instead of this hack
        if ("org.glassfish.web.loader.WebappClassLoader".equals(classLoaderClassName)||
            "org.apache.catalina.loader.WebappClassLoader".equals(classLoaderClassName) ||
            "org.apache.catalina.loader.ParallelWebappClassLoader".equals(classLoaderClassName) ||
            "org.apache.tomee.catalina.TomEEWebappClassLoader".equals(classLoaderClassName) ||
            "org.springframework.boot.web.embedded.tomcat.TomcatEmbeddedWebappClassLoader".equals(classLoaderClassName)
            )
        {
            try {
                Class<?> clazz = classLoader.getClass();
                boolean isStarted;
                if ("org.apache.catalina.loader.WebappClassLoaderBase".equals(clazz.getSuperclass().getName())) {
                    clazz = clazz.getSuperclass();
                    isStarted = "STARTED".equals((String) ReflectionHelper.invoke(classLoader, clazz, "getStateName", new Class[] {}, null));
                } else {
                    isStarted = (boolean) ReflectionHelper.invoke(classLoader, clazz, "isStarted", new Class[] {}, null);
                }
                return isStarted;
            } catch (Exception e) {
                LOGGER.warning("isClassLoderStarted() : {}", e.getMessage());
            }
        }
        return true;
    }

    /**
     * 通过ClassLoader获取Package的资源信息
     */
    public static Enumeration<URL> getResources(ClassLoader appClassLoader, String basePackage) throws IOException {
        String resourceName = DebugToolsStringUtils.getClassNameRemoveStar(basePackage);
        resourceName = resourceName.replace('.', '/');
        return appClassLoader.getResources(resourceName);
    }
}
