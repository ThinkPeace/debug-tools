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
package io.github.future0923.debug.tools.base.hutool.core.lang.ansi;

import io.github.future0923.debug.tools.base.hutool.core.lang.Assert;

/**
 * ANSI 8-bit前景或背景色（即8位编码，共256种颜色（2^8） ）<br>
 * <ul>
 *     <li>0-7：                        标准颜色（同ESC [ 30–37 m）</li>
 *     <li>8-15：                       高强度颜色（同ESC [ 90–97 m）</li>
 *     <li>16-231（6 × 6 × 6 共 216色）： 16 + 36 × r + 6 × g + b (0 ≤ r, g, b ≤ 5)</li>
 *     <li>232-255：                    从黑到白的24阶灰度色</li>
 * </ul>
 *
 * <p>来自Spring Boot</p>
 *
 * @author Toshiaki Maki, Phillip Webb
 * @see #foreground(int)
 * @see #background(int)
 * @since 5.8.0
 */
public final class Ansi8BitColor implements AnsiElement {

	private static final String PREFIX_FORE = "38;5;";
	private static final String PREFIX_BACK = "48;5;";

	/**
	 * 前景色ANSI颜色实例
	 *
	 * @param code 颜色代码(0-255)
	 * @return 前景色ANSI颜色实例
	 */
	public static Ansi8BitColor foreground(int code) {
		return new Ansi8BitColor(PREFIX_FORE, code);
	}

	/**
	 * 背景色ANSI颜色实例
	 *
	 * @param code 颜色代码(0-255)
	 * @return 背景色ANSI颜色实例
	 */
	public static Ansi8BitColor background(int code) {
		return new Ansi8BitColor(PREFIX_BACK, code);
	}

	private final String prefix;
	private final int code;

	/**
	 * 构造
	 *
	 * @param prefix 前缀
	 * @param code   颜色代码(0-255)
	 * @throws IllegalArgumentException 颜色代码不在0~255范围内
	 */
	private Ansi8BitColor(String prefix, int code) {
		Assert.isTrue(code >= 0 && code <= 255, "Code must be between 0 and 255");
		this.prefix = prefix;
		this.code = code;
	}

	/**
	 * 获取颜色代码(0-255)
	 *
	 * @return 颜色代码(0 - 255)
	 */
	@Override
	public int getCode() {
		return this.code;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null || getClass() != obj.getClass()) {
			return false;
		}
		Ansi8BitColor other = (Ansi8BitColor) obj;
		return this.prefix.equals(other.prefix) && this.code == other.code;
	}

	@Override
	public int hashCode() {
		return this.prefix.hashCode() * 31 + this.code;
	}

	@Override
	public String toString() {
		return this.prefix + this.code;
	}
}
