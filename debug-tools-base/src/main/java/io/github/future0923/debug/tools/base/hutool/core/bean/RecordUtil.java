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
package io.github.future0923.debug.tools.base.hutool.core.bean;

import io.github.future0923.debug.tools.base.hutool.core.bean.copier.ValueProvider;
import io.github.future0923.debug.tools.base.hutool.core.util.ClassUtil;
import io.github.future0923.debug.tools.base.hutool.core.util.JdkUtil;
import io.github.future0923.debug.tools.base.hutool.core.util.ReflectUtil;

import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.AbstractMap;
import java.util.Map;

/**
 * java.lang.Record 相关工具类封装<br>
 * 来自于FastJSON2的BeanUtils
 *
 * @author fastjson2, Looly
 * @since 5.8.38
 */
public class RecordUtil {

	private static volatile Class<?> RECORD_CLASS;

	private static volatile Method METHOD_GET_RECORD_COMPONENTS;
	private static volatile Method METHOD_COMPONENT_GET_NAME;
	private static volatile Method METHOD_COMPONENT_GET_GENERIC_TYPE;

	/**
	 * 判断给定类是否为Record类
	 *
	 * @param clazz 类
	 * @return 是否为Record类
	 */
	public static boolean isRecord(final Class<?> clazz) {
		if (JdkUtil.JVM_VERSION < 14) {
			// JDK14+支持Record类
			return false;
		}
		final Class<?> superClass = clazz.getSuperclass();
		if (superClass == null) {
			return false;
		}

		if (RECORD_CLASS == null) {
			// 此处不使用同步代码，重复赋值并不影响判断
			final String superclassName = superClass.getName();
			if ("java.lang.Record".equals(superclassName)) {
				RECORD_CLASS = superClass;
				return true;
			} else {
				return false;
			}
		}

		return superClass == RECORD_CLASS;
	}

	/**
	 * 获取Record类中所有字段名称，getter方法名与字段同名
	 *
	 * @param recordClass Record类
	 * @return 字段数组
	 */
	@SuppressWarnings("unchecked")
	public static Map.Entry<String, Type>[] getRecordComponents(final Class<?> recordClass) {
		if (JdkUtil.JVM_VERSION < 14) {
			// JDK14+支持Record类
			return new Map.Entry[0];
		}
		if (null == METHOD_GET_RECORD_COMPONENTS) {
			METHOD_GET_RECORD_COMPONENTS = ReflectUtil.getMethod(Class.class, "getRecordComponents");
		}

		final Class<Object> recordComponentClass = ClassUtil.loadClass("java.lang.reflect.RecordComponent");
		if (METHOD_COMPONENT_GET_NAME == null) {
			METHOD_COMPONENT_GET_NAME = ReflectUtil.getMethod(recordComponentClass, "getName");
		}
		if (METHOD_COMPONENT_GET_GENERIC_TYPE == null) {
			METHOD_COMPONENT_GET_GENERIC_TYPE = ReflectUtil.getMethod(recordComponentClass, "getGenericType");
		}

		final Object[] components = ReflectUtil.invoke(recordClass, METHOD_GET_RECORD_COMPONENTS);
		final Map.Entry<String, Type>[] entries = new Map.Entry[components.length];
		for (int i = 0; i < components.length; i++) {
			entries[i] = new AbstractMap.SimpleEntry<>(
				ReflectUtil.invoke(components[i], METHOD_COMPONENT_GET_NAME),
				ReflectUtil.invoke(components[i], METHOD_COMPONENT_GET_GENERIC_TYPE)
			);
		}

		return entries;
	}

	/**
	 * 实例化Record类
	 *
	 * @param recordClass   类
	 * @param valueProvider 参数值提供器
	 * @return Record类
	 */
	public static Object newInstance(final Class<?> recordClass, final ValueProvider<String> valueProvider) {
		final Map.Entry<String, Type>[] recordComponents = getRecordComponents(recordClass);
		final Object[] args = new Object[recordComponents.length];
		for (int i = 0; i < args.length; i++) {
			args[i] = valueProvider.value(recordComponents[i].getKey(), recordComponents[i].getValue());
		}

		return ReflectUtil.newInstance(recordClass, args);
	}
}
