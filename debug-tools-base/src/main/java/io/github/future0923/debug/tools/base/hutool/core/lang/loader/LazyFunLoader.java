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
package io.github.future0923.debug.tools.base.hutool.core.lang.loader;

import io.github.future0923.debug.tools.base.hutool.core.lang.Assert;

import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * 函数式懒加载加载器<br>
 * 传入用于生成对象的函数，在对象需要使用时调用生成对象，然后抛弃此生成对象的函数。<br>
 * 此加载器常用于对象比较庞大而不一定被使用的情况，用于减少启动时资源占用问题<br>
 * 继承自{@link LazyLoader}，如何实现多线程安全，由LazyLoader完成。
 *
 * @param <T> 被加载对象类型
 * @author Mr.Po
 * @see LazyLoader
 * @since 5.6.1
 */
public class LazyFunLoader<T> extends LazyLoader<T> {
	private static final long serialVersionUID = 1L;

	/**
	 * 用于生成对象的函数
	 */
	private Supplier<T> supplier;

	/**
	 * 静态工厂方法，提供语义性与编码便利性
	 * @param supplier 用于生成对象的函数
	 * @param <T> 对象类型
	 * @return 函数式懒加载加载器对象
	 * @since 5.8.0
	 */
	public static <T> LazyFunLoader<T> on(final Supplier<T> supplier) {
		Assert.notNull(supplier, "supplier must be not null!");
		return new LazyFunLoader<>(supplier);
	}

	/**
	 * 构造
	 *
	 * @param supplier 用于生成对象的函数
	 */
	public LazyFunLoader(Supplier<T> supplier) {
		Assert.notNull(supplier);
		this.supplier = supplier;
	}

	@Override
	protected T init() {
		T t = this.supplier.get();
		this.supplier = null;
		return t;
	}

	/**
	 * 是否已经初始化
	 *
	 * @return 是/否
	 */
	public boolean isInitialize() {
		return this.supplier == null;
	}

	/**
	 * 如果已经初始化，就执行传入函数
	 *
	 * @param consumer 待执行函数
	 */
	public void ifInitialized(Consumer<T> consumer) {
		Assert.notNull(consumer);

		//	已经初始化
		if (this.isInitialize()) {
			consumer.accept(this.get());
		}
	}
}
