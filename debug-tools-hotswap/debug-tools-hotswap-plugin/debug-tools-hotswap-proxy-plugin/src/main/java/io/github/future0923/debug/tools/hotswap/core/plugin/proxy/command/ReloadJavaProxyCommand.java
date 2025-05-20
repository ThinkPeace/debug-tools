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
package io.github.future0923.debug.tools.hotswap.core.plugin.proxy.command;

import io.github.future0923.debug.tools.base.logging.Logger;
import io.github.future0923.debug.tools.hotswap.core.command.Command;
import io.github.future0923.debug.tools.hotswap.core.command.MergeableCommand;
import io.github.future0923.debug.tools.hotswap.core.config.PluginManager;
import io.github.future0923.debug.tools.hotswap.core.plugin.proxy.ProxyPlugin;
import io.github.future0923.debug.tools.hotswap.core.plugin.proxy.utils.ProxyClassSignatureHelper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 将后续的代理重新定义命令连接在一起，并保证执行顺序。
 * @author future0923
 */
public class ReloadJavaProxyCommand extends MergeableCommand {

    private static final Logger LOGGER = Logger.getLogger(ReloadJavaProxyCommand.class);

    private final ClassLoader classLoader;
    private final String className;
    private final Map<String, String> signatureMapOrig;

    public ReloadJavaProxyCommand(ClassLoader classLoader, String className, Map<String, String> signatureMapOrig) {
        this.classLoader = classLoader;
        this.className = className;
        this.signatureMapOrig = signatureMapOrig;
    }

    @Override
    public void executeCommand() {
        try {
            executeSingleCommand();
            List<Command> commands = new ArrayList<>(getMergedCommands());
            for (Command command: commands) {
                ((ReloadJavaProxyCommand) command).executeSingleCommand();
            }
        } finally {
            ProxyPlugin.reloadFlag = false;
        }
    }

    public void executeSingleCommand() {
        try {
            Class<?> clazz = classLoader.loadClass(className);
            Map<String, String> signatureMap = ProxyClassSignatureHelper.getNonSyntheticSignatureMap(clazz);
            LOGGER.debug("executeSingleCommand class:{}, signature equals:{}", className, signatureMap.equals(signatureMapOrig));
            if (!signatureMap.equals(signatureMapOrig) || !isImplementInterface(signatureMap, clazz)) {
                byte[] generateProxyClass = ProxyGenerator.generateProxyClass(className, clazz.getInterfaces());
                Map<Class<?>, byte[]> reloadMap = new HashMap<>();
                reloadMap.put(clazz, generateProxyClass);
                PluginManager.getInstance().hotswap(reloadMap);
                LOGGER.reload("Class '{}' has been reloaded.", className);
            }
        } catch (ClassNotFoundException e) {
            LOGGER.error("Error redefining java proxy {}", e, className);
        }
    }

    /**
     * 接口的所有方法是否都已实现
     */
    private boolean isImplementInterface(Map<String, String> signatureMap, Class<?> clazz) {
        String clazzSignature;
        try {
            clazzSignature = ProxyClassSignatureHelper.getJavaClassSignature(clazz).replaceAll("final ", "");
            LOGGER.debug("clazzSignature: {}", clazzSignature);
        } catch (Exception e) {
            LOGGER.error("Error getJavaClassSignature {}", clazz, e);
            return true;
        }
        for (Map.Entry<String, String> entry : signatureMap.entrySet()) {
            if(clazzSignature.contains(entry.getKey()) && entry.getValue().contains("public abstract")) {
                LOGGER.debug("{} Signature: {}", entry.getKey(), entry.getValue());
                String[] methodSignatures = entry.getValue().replaceAll("abstract ", "").split(";");
                for (String methodSignature : methodSignatures) {
                    if(!clazzSignature.contains(methodSignature)) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ReloadJavaProxyCommand that = (ReloadJavaProxyCommand) o;

        return classLoader.equals(that.classLoader);
    }

    @Override
    public int hashCode() {
        return classLoader.hashCode();
    }

    @Override
    public String toString() {
        return "ReloadJavaProxyCommand{" + "classLoader=" + classLoader + '}';
    }

}
