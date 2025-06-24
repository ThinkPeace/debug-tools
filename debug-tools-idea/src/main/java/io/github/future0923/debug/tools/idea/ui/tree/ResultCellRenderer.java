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
package io.github.future0923.debug.tools.idea.ui.tree;

import com.intellij.icons.AllIcons;
import com.intellij.ui.ColoredTreeCellRenderer;
import com.intellij.ui.JBColor;
import com.intellij.ui.SimpleTextAttributes;
import io.github.future0923.debug.tools.base.utils.DebugToolsStringUtils;
import io.github.future0923.debug.tools.common.dto.RunResultDTO;
import io.github.future0923.debug.tools.common.utils.DebugToolsClassUtils;
import io.github.future0923.debug.tools.idea.ui.tree.node.TreeNode;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;

/**
 * @author future0923
 */
public class ResultCellRenderer extends ColoredTreeCellRenderer {

    private static final Color ORANGE = new JBColor(new Color(0x753F3E), new Color(0xE6AE87));

    private static final Color GREEN = new JBColor(new Color(0x377A2A), new Color(0x79A878));

    private static final Color GRAY = new JBColor(new Color(0x818593), new Color(0x707379));

    @Override
    public void customizeCellRenderer(@NotNull JTree tree, Object value, boolean selected, boolean expanded, boolean leaf, int row, boolean hasFocus) {
        RunResultDTO runResultDTO = ((TreeNode) value).getUserObject();
        if (runResultDTO == null) {
            return;
        }
        if (RunResultDTO.Type.ROOT.equals(runResultDTO.getType())) {
            setIcon(AllIcons.Debugger.Value);
            append("result", new SimpleTextAttributes(SimpleTextAttributes.STYLE_PLAIN, ORANGE));
            append(" = ");
        } else if (RunResultDTO.Type.MAP.equals(runResultDTO.getType())) {
            setIcon(AllIcons.Debugger.Value);
            appendClassInfo(runResultDTO.getNameClassName(), runResultDTO.getNameIdentity(), runResultDTO.getName(), runResultDTO.getNameArray());
            append(" ");
            appendValueInfo(runResultDTO.getNameClassName(), runResultDTO.getName(), runResultDTO.getNameChildSize(), runResultDTO.getNameArray());
            append(" -> ");
        } else if (RunResultDTO.Type.MAP_ENTRY.equals(runResultDTO.getType())) {
            setIcon(AllIcons.Debugger.Value);
            append(runResultDTO.getName(), new SimpleTextAttributes(SimpleTextAttributes.STYLE_PLAIN, ORANGE));
            append(" = ");
        } else {
            setIcon(AllIcons.Nodes.Field);
            append(runResultDTO.getName(), new SimpleTextAttributes(SimpleTextAttributes.STYLE_PLAIN, ORANGE));
            append(" = ");
        }
        appendClassInfo(runResultDTO.getValueClassName(), runResultDTO.getValueIdentity(), runResultDTO.getValue(), runResultDTO.getValueArray());
        append(" ");
        appendValueInfo(runResultDTO.getValueClassName(), runResultDTO.getValue(), runResultDTO.getValueChildSize(), runResultDTO.getValueArray());
    }

    private void appendValueInfo(String className, String value, Integer childSize, boolean array) {
        // 有子集，并且不是数组
        if (childSize != null && !array) {
            append(value);
        } else {
            if (value == null) {
                append("null");
            } else {
                Class<?> aClass = getClass(className);
                if (DebugToolsClassUtils.isBasicType(aClass)) {
                    append(value);
                } else if ("java.lang.String".equals(className)) {
                    append("\"" + value + "\"", new SimpleTextAttributes(SimpleTextAttributes.STYLE_PLAIN, GREEN));
                } else {
                    append("\"" + value + "\"");
                }
            }
        }
    }

    private void appendClassInfo(String className, String identity, String value, boolean array) {
        // null不展示
        if (value == null) {
            return;
        }
        // 不是数组，因为数组className格式为"byte[1]"等，肯定不能得到class
        if (!array) {
            // 基本类型不展示
            Class<?> aClass = getClass(className);
            if (aClass != null && aClass.isPrimitive()) {
                return;
            }
        }
        // String不展示
        if ("java.lang.String".equals(className)) {
            return;
        }
        String simpleName = DebugToolsClassUtils.getSimpleName(className);
        if (simpleName == null) {
            simpleName = "Null";
        }
        append("{" + simpleName + "@" + identity + "}", new SimpleTextAttributes(SimpleTextAttributes.STYLE_PLAIN, GRAY));
    }

    private Class<?> getClass(String className) {
        if (DebugToolsStringUtils.isBlank(className)) {
            return null;
        }
        try {
            return Class.forName(className);
        } catch (ClassNotFoundException e) {
            return null;
        }
    }
}
