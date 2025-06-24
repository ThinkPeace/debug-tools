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
package io.github.future0923.debug.tools.hotswap.core.plugin.spring.transformer;

import io.github.future0923.debug.tools.base.constants.ProjectConstants;
import io.github.future0923.debug.tools.base.logging.Logger;
import io.github.future0923.debug.tools.hotswap.core.annotation.FileEvent;
import io.github.future0923.debug.tools.hotswap.core.command.Scheduler;
import io.github.future0923.debug.tools.hotswap.core.plugin.spring.scanner.ClassPathBeanDefinitionScannerAgent;
import io.github.future0923.debug.tools.hotswap.core.plugin.spring.scanner.ClassPathBeanRefreshCommand;
import io.github.future0923.debug.tools.hotswap.core.plugin.spring.scanner.RemoveBeanDefinitionCommand;
import io.github.future0923.debug.tools.hotswap.core.util.IOUtils;
import io.github.future0923.debug.tools.hotswap.core.util.classloader.ClassLoaderHelper;
import io.github.future0923.debug.tools.hotswap.core.watch.WatchEventListener;
import io.github.future0923.debug.tools.hotswap.core.watch.WatchFileEvent;

import java.io.IOException;
import java.util.Objects;

/**
 * SpringBean监听者Watch到新增的class新文件，创建{@link ClassPathBeanRefreshCommand}调用{@link ClassPathBeanDefinitionScannerAgent#refreshClass(String, byte[])}进行Spring环境class重载
 *
 * @author future0923
 */
public class SpringBeanWatchEventListener implements WatchEventListener {

    private static final Logger logger = Logger.getLogger(SpringBeanWatchEventListener.class);

    /**
     * 合并延迟执行时间
     */
    private static final int WAIT_ON_ANALYSIS_PATH = 500;
    private static final int WAIT_ON_CREATE = 1000;

    private final Scheduler scheduler;
    private final ClassLoader appClassLoader;
    private final String basePackage;

    public SpringBeanWatchEventListener(Scheduler scheduler, ClassLoader appClassLoader, String basePackage) {
        this.scheduler = scheduler;
        this.appClassLoader = appClassLoader;
        this.basePackage = basePackage;
    }

    @Override
    public void onEvent(WatchFileEvent event) {
        if (ProjectConstants.DEBUG) {
            logger.info("{}, {}", event.getEventType(), event.getURI().toString());
        }
        // 文件都删除时，返回的是文件夹目录删除事件，不会给
        if (event.isDirectory() && FileEvent.DELETE.equals(event.getEventType())) {
            ClassPathBeanDefinitionScannerAgent.removeBeanDefinitionByDirPath(event.getURI().getPath());
        }
        if (event.isFile() && event.getURI().toString().endsWith(".class")) {
            scheduler.scheduleCommand(new RemoveBeanDefinitionCommand(event), WAIT_ON_ANALYSIS_PATH);
            // 创建了class新文件
            if (FileEvent.CREATE.equals(event.getEventType())) {
                // 检查该类尚未被类加载器加载（避免重复重新加载）。
                String className;
                try {
                    className = IOUtils.urlToClassName(event.getURI());
                } catch (IOException e) {
                    logger.trace("Watch event on resource '{}' skipped, probably Ok because of delete/create event sequence (compilation not finished yet).", e, event.getURI());
                    return;
                }
                if (!ClassLoaderHelper.isClassLoaded(appClassLoader, className)) {
                    logger.info("watch add class event, start reloading spring bean, class name:{}", className);
                    // 只刷新spring中新产生的classes
                    scheduler.scheduleCommand(new ClassPathBeanRefreshCommand(appClassLoader, basePackage, className, event), WAIT_ON_CREATE);
                }
            }
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SpringBeanWatchEventListener that = (SpringBeanWatchEventListener) o;
        return Objects.equals(appClassLoader, that.appClassLoader) && Objects.equals(basePackage, that.basePackage);
    }

    @Override
    public int hashCode() {
        return Objects.hash(appClassLoader, basePackage);
    }
}
