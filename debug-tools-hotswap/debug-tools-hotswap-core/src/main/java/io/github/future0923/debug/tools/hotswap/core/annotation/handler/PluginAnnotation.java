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
package io.github.future0923.debug.tools.hotswap.core.annotation.handler;

import io.github.future0923.debug.tools.hotswap.core.annotation.Plugin;
import lombok.Getter;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * 插件注解信息
 */
public class PluginAnnotation<T extends Annotation> {

    /**
     * 插件Class信息
     */
    @Getter
    private final Class<?> pluginClass;

    /**
     * 插件对象
     */
    @Getter
    private final Object plugin;

    /**
     * 注解信息
     */
    @Getter
    private final T annotation;

    /**
     * 注解所在的字段（在方法时为null）
     */
    @Getter
    private Field field;

    /**
     * 注解所在的方法（在字段时为null）
     */
    @Getter
    private Method method;

    /**
     * 插件组信息
     */
    @Getter
    private final String group;

    private final boolean fallback;

    public PluginAnnotation(Class<?> pluginClass, Object plugin, T annotation, Method method) {
        this.pluginClass = pluginClass;
        this.plugin = plugin;
        this.annotation = annotation;
        this.method = method;
        Plugin pluginAnnotation = pluginClass.getAnnotation(Plugin.class);
        this.group = (pluginAnnotation.group() != null && !pluginAnnotation.group().isEmpty()) ? pluginAnnotation.group() : null;
        this.fallback = pluginAnnotation.fallback();
    }

    public PluginAnnotation(Class<?> pluginClass, Object plugin, T annotation, Field field) {
        this.pluginClass = pluginClass;
        this.plugin = plugin;
        this.annotation = annotation;
        this.field = field;
        this.fallback = false;
        this.group = null;
    }

    /**
     * @return true, if plugin is fallback
     */
    public boolean isFallBack() {
        return fallback;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        PluginAnnotation<?> that = (PluginAnnotation<?>) o;

        if (!annotation.equals(that.annotation)) return false;
        if (field != null ? !field.equals(that.field) : that.field != null) return false;
        if (method != null ? !method.equals(that.method) : that.method != null) return false;
        if (!plugin.equals(that.plugin)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = plugin.hashCode();
        result = 31 * result + annotation.hashCode();
        result = 31 * result + (field != null ? field.hashCode() : 0);
        result = 31 * result + (method != null ? method.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "PluginAnnotation{" +
                "plugin=" + plugin +
                ", annotation=" + annotation +
                ", field=" + field +
                ", method=" + method +
                '}';
    }
}
