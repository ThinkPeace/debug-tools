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
package io.github.future0923.debug.tools.base.hutool.json;

/**
 * {@code JSONString}接口定义了一个{@code toJSONString()}<br>
 * 实现此接口的类可以通过实现{@code toJSONString()}方法来改变转JSON字符串的方式。
 *
 * @author Looly
 *
 */
public interface JSONString {

	/**
	 * 自定义转JSON字符串的方法
	 *
	 * @return JSON字符串
	 */
	String toJSONString();
}
