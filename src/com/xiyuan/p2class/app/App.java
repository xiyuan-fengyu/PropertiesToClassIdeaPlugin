package com.xiyuan.p2class.app;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.ApplicationComponent;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectManager;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.VirtualFileManager;
import com.intellij.openapi.vfs.newvfs.BulkFileListener;
import com.intellij.openapi.vfs.newvfs.events.VFileContentChangeEvent;
import com.intellij.openapi.vfs.newvfs.events.VFileDeleteEvent;
import com.intellij.openapi.vfs.newvfs.events.VFileEvent;
import com.intellij.util.messages.MessageBusConnection;
import com.xiyuan.p2class.util.PropertiesToClass;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Created by xiyuan_fengyu on 2016/11/21.
 */
public class App implements ApplicationComponent, BulkFileListener {

    public static boolean settingRegenerate;

    public static boolean settingDelete;

    private final MessageBusConnection connection;

    public App() {
        this.connection = ApplicationManager.getApplication().getMessageBus().connect();
    }

    @Override
    public void initComponent() {
        connection.subscribe(VirtualFileManager.VFS_CHANGES, this);
    }

    @Override
    public void disposeComponent() {
        connection.disconnect();
    }

    @NotNull
    @Override
    public String getComponentName() {
        return "PropertiesToClass";
    }

    @Override
    public void before(@NotNull List<? extends VFileEvent> list) {

    }

    @Override
    public void after(@NotNull List<? extends VFileEvent> list) {
        if (settingDelete || settingRegenerate) {
            for (VFileEvent event: list) {
                VirtualFile file = event.getFile();
                if (PropertiesToClass.isPropertiesFile(file)) {
                    if (settingRegenerate && event instanceof VFileContentChangeEvent) {
                        onContentChange(event);
                    }
                    else if (settingDelete && event instanceof VFileDeleteEvent) {
                        onDelete(event);
                    }
                }
            }
        }
    }

    private void onContentChange(VFileEvent event) {
        VirtualFile file = event.getFile();
        Project curProject = getCurrentProject(file);
        if (curProject != null) {
            PropertiesToClass.generate(curProject, file, true);
        }
    }

    private void onDelete(VFileEvent event) {
        VirtualFile file = event.getFile();
        Project curProject = getCurrentProject(file);
        if (curProject != null) {
            PropertiesToClass.delete(curProject, file);
        }
    }

    private static Project getCurrentProject(VirtualFile file) {
        if (file == null) {
            return null;
        }

        String filePath = file.getPath();
        for (Project project: ProjectManager.getInstance().getOpenProjects()) {
            String projectPath = project.getBasePath();
            if (projectPath != null && filePath.indexOf(projectPath) == 0) {
                return project;
            }
        }

        return null;
    }

}
