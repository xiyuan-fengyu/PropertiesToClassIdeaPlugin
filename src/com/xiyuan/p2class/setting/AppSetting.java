package com.xiyuan.p2class.setting;

import com.intellij.openapi.options.Configurable;
import com.intellij.openapi.options.ConfigurationException;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

/**
 * Created by xiyuan_fengyu on 2016/11/21.
 */
public class AppSetting implements Configurable {

    private AppSettingGUI gui;

    @Nls
    @Override
    public String getDisplayName() {
        return "Properties To Class";
    }

    @Nullable
    @Override
    public String getHelpTopic() {
        return null;
    }

    @Nullable
    @Override
    public JComponent createComponent() {
        gui = new AppSettingGUI();
        return gui.getRoot();
    }

    @Override
    public boolean isModified() {
        return gui.isModified();
    }

    @Override
    public void apply() throws ConfigurationException {
        gui.apply();
    }

    @Override
    public void reset() {
        gui.reset();
    }

    @Override
    public void disposeUIResources() {

    }

}
