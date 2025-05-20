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
package io.github.future0923.debug.tools.base.hutool.core.util;


/**
 * JDK相关工具类，包括判断JDK版本等<br>
 * 工具部分方法来自fastjson2的JDKUtils
 *
 * @author fastjson, looly
 */
public class JdkUtil {
	/**
	 * JDK版本
	 */
	public static final int JVM_VERSION;
	/**
	 * 是否JDK8<br>
	 * 由于Hutool基于JDK8编译，当使用JDK版本低于8时，不支持。
	 */
	public static final boolean IS_JDK8;
	/**
	 * 是否大于等于JDK17
	 */
	public static final boolean IS_AT_LEAST_JDK17;

	/**
	 * 是否Android环境
	 */
	public static final boolean IS_ANDROID;

	static {
		// JVM版本
		JVM_VERSION = _getJvmVersion();
		IS_JDK8 = 8 == JVM_VERSION;
		IS_AT_LEAST_JDK17 = JVM_VERSION >= 17;

		// JVM名称
		final String jvmName = _getJvmName();
		IS_ANDROID = jvmName.equals("Dalvik");
	}

	/**
	 * 获取JVM名称
	 *
	 * @return JVM名称
	 */
	private static String _getJvmName() {
		return System.getProperty("java.vm.name");
	}

	/**
	 * 根据{@code java.specification.version}属性值，获取版本号
	 *
	 * @return 版本号
	 */
	private static int _getJvmVersion() {
		int jvmVersion = -1;

		try{
			String javaSpecVer = System.getProperty("java.specification.version");
			if (StrUtil.isNotBlank(javaSpecVer)) {
				if (javaSpecVer.startsWith("1.")) {
					javaSpecVer = javaSpecVer.substring(2);
				}
				if (javaSpecVer.indexOf('.') == -1) {
					jvmVersion = Integer.parseInt(javaSpecVer);
				}
			}
		} catch (Throwable ignore){
			// 默认JDK8
			jvmVersion = 8;
		}

		return jvmVersion;
	}
}
