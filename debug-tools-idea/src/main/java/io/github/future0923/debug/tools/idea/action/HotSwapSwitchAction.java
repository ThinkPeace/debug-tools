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
package io.github.future0923.debug.tools.idea.action;

import com.intellij.openapi.actionSystem.ActionUpdateThread;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.Presentation;
import com.intellij.openapi.project.DumbAwareAction;
import com.intellij.openapi.project.Project;
import io.github.future0923.debug.tools.idea.setting.DebugToolsSettingState;
import io.github.future0923.debug.tools.idea.utils.DebugToolsIcons;
import org.jetbrains.annotations.NotNull;

/**
 * @author future0923
 */
public class HotSwapSwitchAction extends DumbAwareAction {

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        Project project = e.getProject();
        if (project == null) {
            return;
        }
        DebugToolsSettingState settingState = DebugToolsSettingState.getInstance(project);
        settingState.setHotswap(!settingState.getHotswap());
    }

    @Override
    public void update(@NotNull AnActionEvent e) {
        Presentation presentation = e.getPresentation();
        Project project = e.getProject();
        if (project == null) {
            return;
        }
        DebugToolsSettingState settingState = DebugToolsSettingState.getInstance(project);
        if (settingState.getHotswap()) {
            presentation.setText("Enable Hot Reload");
            presentation.setDescription("After turning it on, starting the project will load the hot reload plugin");
            presentation.setIcon(DebugToolsIcons.Hotswap.On);
        } else {
            presentation.setText("Disable Hot Reload");
            presentation.setDescription("Disable hot reload to remove plugins after restart");
            presentation.setIcon(DebugToolsIcons.Hotswap.Off);
        }
    }

    @Override
    public @NotNull ActionUpdateThread getActionUpdateThread() {
        return ActionUpdateThread.BGT;
    }
}
