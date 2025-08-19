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
package io.github.future0923.debug.tools.idea.ui.convert;

import io.github.future0923.debug.tools.idea.bundle.DebugToolsBundle;
import lombok.Getter;

/**
 * @author future0923
 */
@Getter
public enum ConvertType {

    IMPORT("convert.type.import.title", "convert.type.import.ok.button.text", "convert.type.import.description"),

    EXPORT("convert.type.export.title", "convert.type.export.ok.button.text", "convert.type.export.description"),
    ;
    private final String title;

    private final String okButtonText;

    private final String description;

    ConvertType(String titleKey, String okButtonTextKey, String descriptionKey) {
        this.title = DebugToolsBundle.message(titleKey);
        this.okButtonText = DebugToolsBundle.message(okButtonTextKey);
        this.description = DebugToolsBundle.message(descriptionKey);
    }
}
