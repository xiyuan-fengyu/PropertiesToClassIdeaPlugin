package com.xiyuan.p2class.action;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.xiyuan.p2class.util.PropertiesToClass;

/**
 * Created by xiyuan_fengyu on 2016/11/1.
 */
public class P2ClassAction extends AnAction {

    /**
     * 点击按钮后
     * @param e
     */
    @Override
    public void actionPerformed(AnActionEvent e) {
        Project project = CommonDataKeys.PROJECT.getData(e.getDataContext());
        VirtualFile file = CommonDataKeys.VIRTUAL_FILE.getData(e.getDataContext());
        PropertiesToClass.generate(project, file);
    }

    /**
     * 如果文件后缀名使.properties，则显示该菜单，否则不显示
     * @param e
     */
    @Override
    public void update(AnActionEvent e) {
        VirtualFile file = CommonDataKeys.VIRTUAL_FILE.getData(e.getDataContext());
        e.getPresentation().setEnabledAndVisible(PropertiesToClass.isPropertiesFile(file));
    }


}
