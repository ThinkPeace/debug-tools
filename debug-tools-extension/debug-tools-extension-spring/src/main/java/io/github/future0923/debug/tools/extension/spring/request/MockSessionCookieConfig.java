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
package io.github.future0923.debug.tools.extension.spring.request;

import org.springframework.lang.Nullable;

import javax.servlet.SessionCookieConfig;

/**
 * @author future0923
 */
public class MockSessionCookieConfig implements SessionCookieConfig {

    @Nullable
    private String name;

    @Nullable
    private String domain;

    @Nullable
    private String path;

    @Nullable
    private String comment;

    private boolean httpOnly;

    private boolean secure;

    private int maxAge = -1;


    @Override
    public void setName(@Nullable String name) {
        this.name = name;
    }

    @Override
    @Nullable
    public String getName() {
        return this.name;
    }

    @Override
    public void setDomain(@Nullable String domain) {
        this.domain = domain;
    }

    @Override
    @Nullable
    public String getDomain() {
        return this.domain;
    }

    @Override
    public void setPath(@Nullable String path) {
        this.path = path;
    }

    @Override
    @Nullable
    public String getPath() {
        return this.path;
    }

    @Override
    public void setComment(@Nullable String comment) {
        this.comment = comment;
    }

    @Override
    @Nullable
    public String getComment() {
        return this.comment;
    }

    @Override
    public void setHttpOnly(boolean httpOnly) {
        this.httpOnly = httpOnly;
    }

    @Override
    public boolean isHttpOnly() {
        return this.httpOnly;
    }

    @Override
    public void setSecure(boolean secure) {
        this.secure = secure;
    }

    @Override
    public boolean isSecure() {
        return this.secure;
    }

    @Override
    public void setMaxAge(int maxAge) {
        this.maxAge = maxAge;
    }

    @Override
    public int getMaxAge() {
        return this.maxAge;
    }

}