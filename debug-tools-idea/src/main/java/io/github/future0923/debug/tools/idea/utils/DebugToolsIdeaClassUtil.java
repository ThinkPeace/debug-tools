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
package io.github.future0923.debug.tools.idea.utils;

import com.intellij.openapi.project.Project;
import com.intellij.psi.*;
import com.intellij.psi.search.GlobalSearchScope;

import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DebugToolsIdeaClassUtil {

    private static final Pattern pattern = Pattern.compile("^\\s*package\\s+([\\w.]+)\\s*;", Pattern.MULTILINE);

    public static String getPackageName(String content) {
        Matcher matcher = pattern.matcher(content);
        if (matcher.find()) {
            return matcher.group(1);
        } else {
            return null;
        }
    }

    public static String getMethodQualifiedName(PsiMethod psiMethod) {
        // 获取方法所在的Psi类， 在代码分析、重构和导航时非常有用，因为它允许你获取方法所属的类，从而可以执行各种操作，比如检查类的属性、调用其他方法等。
        PsiClass containingClass = psiMethod.getContainingClass();
        if (containingClass != null) {
            StringBuilder fullQualifiedName = new StringBuilder(containingClass.getQualifiedName() + "#" + psiMethod.getName());
            PsiParameter[] parameters = psiMethod.getParameterList().getParameters();
            if (parameters.length > 0) {
                fullQualifiedName.append("(");
                for (int i = 0; i < parameters.length; i++) {
                    fullQualifiedName.append(parameters[i].getType().getCanonicalText());
                    if (i < parameters.length - 1) {
                        fullQualifiedName.append(",");
                    }
                }
                fullQualifiedName.append(")");
            }
            return fullQualifiedName.toString();
        } else {
            return psiMethod.getName();
        }
    }

    public static String getSimpleMethodName(String qualifiedMethodName) {
        String methodName = qualifiedMethodName.substring(qualifiedMethodName.lastIndexOf("#") + 1);
        if (methodName.contains("(")) {
            return methodName.substring(0, methodName.indexOf("("));
        }
        return methodName;

    }

    /**
     * 在给定的项目中查找指定名称的 Java 类
     *
     * @param project            搜索项目
     * @param qualifiedClassName 类标识符
     * @return PsiClass信息
     */
    public static PsiClass findClass(Project project, String qualifiedClassName) {
        return JavaPsiFacade.getInstance(project).findClass(qualifiedClassName, GlobalSearchScope.allScope(project));
    }

    /**
     * 在给定的项目中查找指定名称的 Method 类
     *
     * @param project             搜索项目
     * @param qualifiedMethodName 方法标识符
     * @return PsiMethod信息
     */
    public static PsiMethod findMethod(Project project, String qualifiedMethodName) {
        PsiClass psiClass = findClass(project, qualifiedMethodName.substring(0, qualifiedMethodName.lastIndexOf("#")));
        if (Objects.nonNull(psiClass)) {
            PsiMethod[] methods = psiClass.findMethodsByName(getSimpleMethodName(qualifiedMethodName), false);
            for (PsiMethod method : methods) {
                if (Objects.equals(getMethodQualifiedName(method), qualifiedMethodName)) {
                    return method;
                }
            }
        }
        return null;
    }

    /**
     * 获取类名，处理多级内部类。io.github.Test.User.Name -> io.github.Test$User$Name
     */
    public static String tryInnerClassName(PsiClass psiClass) {
        PsiClass originalClass = psiClass;
        StringBuilder classNameBuilder = new StringBuilder();
        // 构建类的层级结构（包括嵌套类）
        while (psiClass != null) {
            if (!classNameBuilder.isEmpty()) {
                classNameBuilder.insert(0, "$");  // 如果已经有内容，表示嵌套类，插入$
            }
            classNameBuilder.insert(0, psiClass.getName());  // 插入当前类的名称
            // 查找父类，如果当前类的父类是PsiClass，则跳出循环
            PsiElement parent = psiClass.getParent();
            if (parent instanceof PsiClass) {
                psiClass = (PsiClass) parent;
            } else {
                break;
            }
        }
        if (originalClass != null) {
            // 获取包名并插入类名之前
            PsiFile psiFile = originalClass.getContainingFile();
            if (psiFile instanceof PsiJavaFile) {
                String packageName = ((PsiJavaFile) psiFile).getPackageName();
                if (!packageName.isEmpty()) {
                    classNameBuilder.insert(0, ".");  // 插入包名的分隔符
                    classNameBuilder.insert(0, packageName);  // 插入包名
                }
            }
        }
        return classNameBuilder.toString();
    }

}
