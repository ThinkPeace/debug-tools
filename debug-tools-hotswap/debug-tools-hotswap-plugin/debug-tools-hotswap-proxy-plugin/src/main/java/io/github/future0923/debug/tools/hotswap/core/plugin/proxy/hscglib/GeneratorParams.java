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
package io.github.future0923.debug.tools.hotswap.core.plugin.proxy.hscglib;

import io.github.future0923.debug.tools.hotswap.core.util.ReflectionHelper;

/**
 * 新创建 Cglib 代理的参数
 *
 * @author future0923
 */
public class GeneratorParams {

    private Object generator;
    private Object param;

    public GeneratorParams(Object generator, Object param) {
        this.generator = generator;
        this.param = param;
    }

    public Object getGenerator() {
        return generator;
    }

    public void setGenerator(Object generator) {
        this.generator = generator;
    }

    public Object getParam() {
        return param;
    }

    public void setParam(Object params) {
        this.param = params;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result
                + ((generator == null) ? 0 : generator.hashCode());
        result = prime * result + ((param == null) ? 0 : param.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        GeneratorParams other = (GeneratorParams) obj;
        if (generator == null) {
            if (other.generator != null)
                return false;
        } else if (!generator.equals(other.generator))
            return false;
        if (param == null) {
            if (other.param != null)
                return false;
        } else if (!param.equals(other.param))
            return false;
        return true;
    }

    /**
     * 在这个类加载器中返回一个实例。
     */
    public static GeneratorParams valueOf(Object paramsFromOtherClassLoader) throws Exception {
        if (paramsFromOtherClassLoader.getClass().getClassLoader() == GeneratorParams.class.getClassLoader()) {
            return (GeneratorParams) paramsFromOtherClassLoader;
        }
        Object params = ReflectionHelper.get(paramsFromOtherClassLoader,"param");
        Object generator = ReflectionHelper.get(paramsFromOtherClassLoader, "generator");
        return new GeneratorParams(generator, params);
    }
}
