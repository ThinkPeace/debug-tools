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
package io.github.future0923.debug.tools.idea.navigation;

import com.intellij.navigation.DirectNavigationProvider;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiClassType;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiField;
import com.intellij.psi.PsiParameter;
import com.intellij.psi.PsiParameterList;
import io.github.future0923.debug.tools.idea.ui.main.MainJsonEditor;
import io.github.future0923.debug.tools.idea.utils.DebugToolsJsonElementUtil;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

/**
 * @author future0923
 */
@SuppressWarnings("UnstableApiUsage")
public class DebugToolsJsonEditorDirectNavigationProvider implements DirectNavigationProvider {

    @Override
    public @Nullable PsiElement getNavigationElement(@NotNull PsiElement element) {
        if (!element.getContainingFile().getName().equals(MainJsonEditor.FILE_NAME)) {
            return null;
        }

        // 非key不做处理
        if (!DebugToolsJsonElementUtil.isJsonKey(element)) {
            return null;
        }

        PsiParameterList psiParameterList = element.getContainingFile().getUserData(MainJsonEditor.DEBUG_POWER_EDIT_CONTENT);
        if (psiParameterList == null) {
            return null;
        }

        // 导航到方法参数上
        String text = StringUtils.removeEnd(StringUtils.removeStart(element.getText(), "\""), "\"");
        for (int i = 0; i < psiParameterList.getParametersCount(); i++) {
            PsiParameter parameter = Objects.requireNonNull(psiParameterList.getParameter(i));
            if (Objects.equals(parameter.getName(), text)) {
                return parameter;
            }
            // 若是负责类，尝试导航到参数类里面
            if (parameter.getType() instanceof PsiClassType) {
                PsiClass psiClass = ((PsiClassType) parameter.getType()).resolve();
                if (psiClass != null) {
                    for (PsiField field : psiClass.getFields()) {
                        if (Objects.equals(field.getName(), text)) {
                            return field;
                        }
                    }
                }
            }
        }
        return null;
    }
}
