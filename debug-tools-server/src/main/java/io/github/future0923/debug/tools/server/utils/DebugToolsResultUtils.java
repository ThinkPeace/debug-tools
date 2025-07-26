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
package io.github.future0923.debug.tools.server.utils;

import io.github.future0923.debug.tools.base.hutool.core.collection.CollUtil;
import io.github.future0923.debug.tools.base.hutool.core.util.ArrayUtil;
import io.github.future0923.debug.tools.base.hutool.core.util.ClassUtil;
import io.github.future0923.debug.tools.base.logging.Logger;
import io.github.future0923.debug.tools.base.utils.DebugToolsStringUtils;
import io.github.future0923.debug.tools.common.dto.RunResultDTO;
import io.github.future0923.debug.tools.common.enums.ResultVarClassType;
import io.github.future0923.debug.tools.base.utils.DebugToolsClassUtils;
import io.github.future0923.debug.tools.common.utils.JdkUnsafeUtils;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author future0923
 */
public class DebugToolsResultUtils {

    private static final Map<String, Object> CACHE = new ConcurrentHashMap<>();

    private static final Logger log = Logger.getLogger(DebugToolsResultUtils.class);

    public static void putCache(String offsetPath, Object object) {
        if (offsetPath == null || object == null) {
            return;
        }
        CACHE.put(offsetPath, object);
    }

    public static void removeCache(String offsetPath) {
        if (DebugToolsStringUtils.isNotBlank(offsetPath)) {
            CACHE.remove(offsetPath);
        }
    }

    public static Object getValueByOffset(String offsetPath) {
        if (DebugToolsStringUtils.isBlank(offsetPath)) {
            return null;
        }
        int index = offsetPath.indexOf("/");
        if (index == -1) {
            return CACHE.get(offsetPath);
        } else {
            Object object = CACHE.get(offsetPath.substring(0, index));
            if (object == null) {
                return null;
            }
            return getValueByOffset(object, offsetPath);
        }
    }

    public static Object getValueByField(Object object, Field field) {
        return JdkUnsafeUtils.getObject(object, JdkUnsafeUtils.getObjectFieldOffset(field), ResultVarClassType.getByClass(field.getType()));
    }

    public static String getObjectFieldOffset(Field field) {
        return JdkUnsafeUtils.getObjectFieldOffset(field) + "@" + ResultVarClassType.getByClass(field.getType());
    }

    public static Object getValueByOffset(Object object, String offsetPath) {
        String[] offsetPathArr;
        try {
            offsetPathArr = offsetPath.split("/");
        } catch (NumberFormatException e) {
            log.error("getValueByOffset error", e);
            return null;
        }
        if (ArrayUtil.isNotEmpty(offsetPathArr)) {
            String[] offsetArr = Arrays.copyOfRange(offsetPathArr, 1, offsetPathArr.length);// 1~length
            Object result = object;
            for (String offsetStr : offsetArr) {
                String[] split = offsetStr.split("@");
                if (split.length == 2) {
                    long offset = Long.parseLong(split[0]);
                    String type = split[1];
                    result = getValueByOffset(result, offset, type);
                }
            }
            return result;
        }
        return null;
    }

    public static Object getValueByOffset(Object object, long offset, String type) {
        if (ClassUtil.isBasicType(object.getClass())) {
            return object;
        }
        if (object instanceof Map<?, ?>) {
            return ((Map<?, ?>) object)
                    .entrySet()
                    .stream()
                    .filter(h -> System.identityHashCode(h.getKey()) == offset)
                    .findFirst()
                    .orElse(null);
        } else if (object instanceof Map.Entry<?, ?>) {
            Map.Entry<?, ?> entry = (Map.Entry<?, ?>) object;
            return offset == 0 ? entry.getKey() : entry.getValue();
        } else if (object instanceof Collection<?>) {
            return CollUtil.get((Collection<?>) object, Math.toIntExact(offset));
        } else if (ArrayUtil.isArray(object)) {
            return ArrayUtil.get(object, Math.toIntExact(offset));
        } else {
            return JdkUnsafeUtils.getObject(object, offset, type);
        }
    }

    public static List<RunResultDTO> convertRunResultDTO(Object object, String filedOffset) {
        if (object == null) {
            return Collections.emptyList();
        }
        if (ClassUtil.isBasicType(object.getClass())) {
            return Collections.singletonList(new RunResultDTO(null, object, RunResultDTO.Type.SIMPLE, filedOffset));
        }
        if (object instanceof Map<?, ?>) {
            Map<?, ?> map = (Map<?, ?>) object;
            return map(map, filedOffset);
        }
        if (object instanceof Map.Entry<?, ?>) {
            Map.Entry<?, ?> entry = (Map.Entry<?, ?>) object;
            return Arrays.asList(
                    new RunResultDTO("key", entry.getKey(), RunResultDTO.Type.MAP_ENTRY, filedOffset + "/0@" + ResultVarClassType.MAP_ENTRY.getType()),
                    new RunResultDTO("value", entry.getValue(), RunResultDTO.Type.MAP_ENTRY, filedOffset + "/1@" + ResultVarClassType.MAP_ENTRY.getType())
            );
        }
        if (object instanceof Collection<?>) {
            Collection<?> coll = (Collection<?>) object;
            Object[] array = coll.toArray();
            return array(array, filedOffset);
        }
        if (ArrayUtil.isArray(object)) {
            Object[] array = (Object[]) object;
            return array(array, filedOffset);
        }
        return object(object, filedOffset);
    }

    private static List<RunResultDTO> object(Object object, String filedOffset) {
        List<Field> declaredFields = DebugToolsClassUtils.getAllDeclaredFields(object.getClass());
        List<RunResultDTO> result = new ArrayList<>(declaredFields.size());
        for (Field declaredField : declaredFields) {
            if (Modifier.isStatic(declaredField.getModifiers())) {
                continue;
            }
            Object value = getValueByField(object, declaredField);
            result.add(new RunResultDTO(declaredField.getName(), value, RunResultDTO.Type.PROPERTY, filedOffset + "/" + getObjectFieldOffset(declaredField)));
        }
        return result;
    }

    private static List<RunResultDTO> array(Object[] array, String filedOffset) {
        List<RunResultDTO> result = new ArrayList<>(array.length);
        for (int i = 0; i < array.length; i++) {
            String index = java.lang.String.valueOf(i);
            result.add(new RunResultDTO(index, array[i], RunResultDTO.Type.COLLECTION, filedOffset + "/" + index + "@" + ResultVarClassType.COLLECTION.getType()));
        }
        return result;
    }

    private static List<RunResultDTO> map(Map<?, ?> map, String filedOffset) {
        List<RunResultDTO> result = new ArrayList<>(map.size());
        for (Map.Entry<?, ?> entry : map.entrySet()) {
            result.add(new RunResultDTO(entry.getKey(), entry.getValue(), RunResultDTO.Type.MAP, filedOffset + "/" + System.identityHashCode(entry.getKey()) + "@" + ResultVarClassType.MAP));
        }
        return result;
    }
}
