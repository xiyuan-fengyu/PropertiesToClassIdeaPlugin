package com.xiyuan.p2class.action;

import com.intellij.ide.util.PackageChooserDialog;
import com.intellij.ide.util.PropertiesComponent;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.ProjectRootManager;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.VirtualFileManager;
import com.intellij.psi.PsiPackage;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Properties;

/**
 * Created by xiyuan_fengyu on 2016/11/1.
 */
public class PropertiesToClass extends AnAction {

    /**
     * 点击按钮后
     * @param e
     */
    @Override
    public void actionPerformed(AnActionEvent e) {
        Project project = CommonDataKeys.PROJECT.getData(e.getDataContext());
        VirtualFile file = CommonDataKeys.VIRTUAL_FILE.getData(e.getDataContext());
        if (project != null && file != null) {
            String filePath = file.getPath();
            String fileShortPath = filePath;

            final ProjectRootManager projectRootManager = ProjectRootManager.getInstance(project);
            final VirtualFile[] sourceRoots = projectRootManager.getContentSourceRoots();

            for (VirtualFile item: sourceRoots) {
                String itemPath = item.getPath();
                int index = filePath.indexOf(itemPath + "/");
                if (index > -1) {
                    fileShortPath = filePath.substring(index + itemPath.length() + 1);
                }
            }

            PropertiesComponent propertiesComponent = PropertiesComponent.getInstance();
            String historyPath = propertiesComponent.getValue(filePath + ".classPath");
            String packageName = propertiesComponent.getValue(filePath + ".package");

            String fileName = file.getName();
            String className = className(fileName);
            String classPath = historyPath + "/" + className + ".java";
            File classFile = new File(classPath);
            if (!classFile.exists()) {
                //弹框选择包路径
                PackageChooserDialog dialog = new PackageChooserDialog("Select a package", project);
                dialog.show();
                PsiPackage psiPackage = dialog.getSelectedPackage();
                if (psiPackage == null) {
                    return;
                }

                String selectedPath = null;
                String psiPackageStr = psiPackage.getQualifiedName();
                if (psiPackageStr.contains(".")) {
                    psiPackageStr = psiPackageStr.replaceAll("\\.", "/");
                }
                for (VirtualFile item: sourceRoots) {
                    String itemPath = item.getPath();
                    if (new File(itemPath + "/" + psiPackageStr).exists()) {
                        selectedPath = itemPath + "/" + psiPackageStr;
                    }
                }

                if (selectedPath != null) {
                    classPath = selectedPath + "/" + className + ".java";
                    packageName = psiPackage.getQualifiedName();
                    propertiesComponent.setValue(filePath + ".classPath", selectedPath);
                    propertiesComponent.setValue(filePath + ".package", packageName);
                }
                classFile = new File(classPath);
            }

            String classStr = propertiesToClassStr(className, packageName, file, fileShortPath);
            if (classStr != null) {
                try (FileOutputStream out = new FileOutputStream(classFile)) {
                    out.write(classStr.getBytes("utf-8"));
                    out.flush();

                    //刷新文件，让生成的文件在视图中显示出来
                    VirtualFileManager.getInstance().asyncRefresh(null);
                }
                catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        }
    }

    /**
     * 如果文件后缀名使.properties，则显示该菜单，否则不显示
     * @param e
     */
    @Override
    public void update(AnActionEvent e) {
        VirtualFile file = CommonDataKeys.VIRTUAL_FILE.getData(e.getDataContext());
        e.getPresentation().setEnabledAndVisible(isPropertiesFile(file));
    }

    private static boolean isPropertiesFile(@Nullable VirtualFile file) {
        return file != null && file.getName().endsWith(".properties");
    }

    private static String className(String fileName) {
        if (fileName == null || fileName.isEmpty()) {
            return fileName;
        }

        fileName = fileName.split("\\.")[0];
        char firstC = fileName.charAt(0);
        if (firstC >= 'a' && firstC <= 'z') {
            return (char) (firstC - 32) + fileName.substring(1);
        }
        else {
            return fileName;
        }
    }

    private static final String rLongOrInt = "-[0-9]{1,19}|[+]{0,1}[0-9]{1,19}";
    private static final String rDouble = "[-+]{0,1}[0-9]+\\.[0-9]+";
    private static final String rBoolean = "true|false";


    private static String propertiesToClassStr(String className, String packageStr, VirtualFile file, String fileShortPath) {
        Properties properties = new Properties();
        try {
            properties.load(file.getInputStream());

            StringBuilder strBld = new StringBuilder();
            for (Object keyObj: properties.keySet()) {
                String key = (String) keyObj;
                String value = properties.getProperty(key);
                String keyInJava = key.replaceAll("\\.", "_");

                if (value.matches(rBoolean)) {
                    strBld.append("\tpublic static final boolean ").append(keyInJava).append(" = Boolean.parseBoolean(properties.getProperty(\"").append(key).append("\"));\n\n");
                }
                else if (value.matches(rDouble)) {
                    strBld.append("\tpublic static final double ").append(keyInJava).append(" = Double.parseDouble(properties.getProperty(\"").append(key).append("\"));\n\n");
                }
                else if (value.matches(rLongOrInt)) {
                    try {
                        long tempL = Long.parseLong(value);
                        if (tempL >= Integer.MIN_VALUE && tempL <= Integer.MAX_VALUE) {
                            strBld.append("\tpublic static final int ").append(keyInJava).append(" = Integer.parseInt(properties.getProperty(\"").append(key).append("\"));\n\n");
                        }
                        else {
                            strBld.append("\tpublic static final long ").append(keyInJava).append(" = Long.parseLong(properties.getProperty(\"").append(key).append("\"));\n\n");
                        }
                    }
                    catch (Exception e) {
                        strBld.append("\tpublic static final String ").append(keyInJava).append(" = properties.getProperty(\"").append(key).append("\");\n\n");
                    }
                }
                else {
                    strBld.append("\tpublic static final String ").append(keyInJava).append(" = properties.getProperty(\"").append(key).append("\");\n\n");
                }
            }

            StringBuilder classStrBld = new StringBuilder();
            if (packageStr != null && !packageStr.isEmpty()) {
                classStrBld.append("package ").append(packageStr).append(";\n\n");
            }
            classStrBld.append("import java.util.Properties;\n\n");
            classStrBld.append("public class ").append(className).append(" {\n\n");
            classStrBld.append("\tprivate static final Properties properties;\n\n");
            classStrBld.append("\tstatic {\n");
            classStrBld.append("\t\tproperties = new Properties();\n");
            classStrBld.append("\t\ttry {\n");
            classStrBld.append("\t\t\tproperties.load(").append(className).append(".class.getClassLoader().getResourceAsStream(\"").append(fileShortPath).append("\"));\n");
            classStrBld.append("\t\t}\n");
            classStrBld.append("\t\tcatch (Exception e) {\n");
            classStrBld.append("\t\t\te.printStackTrace();\n");
            classStrBld.append("\t\t}\n");
            classStrBld.append("\t}\n\n");
            classStrBld.append(strBld.toString());
            classStrBld.append("}");

            return classStrBld.toString();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

}
