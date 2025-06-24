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
package io.github.future0923.debug.tools.hotswap.core.javassist.tools.reflect;

/**
 * An interface to access a metaobject and a class metaobject.
 * This interface is implicitly implemented by the reflective
 * class.
 */
public interface Metalevel {
    /**
     * Obtains the class metaobject associated with this object.
     */
    ClassMetaobject _getClass();

    /**
     * Obtains the metaobject associated with this object.
     */
    Metaobject _getMetaobject();

    /**
     * Changes the metaobject associated with this object.
     */
    void _setMetaobject(Metaobject m);
}
