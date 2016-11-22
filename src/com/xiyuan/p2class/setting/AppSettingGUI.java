package com.xiyuan.p2class.setting;

import com.xiyuan.p2class.app.App;

import javax.swing.*;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

/**
 * Created by xiyuan_fengyu on 2016/11/21.
 */
public class AppSettingGUI implements ItemListener {

    private JPanel root;

    private JCheckBox regenerate;

    private JCheckBox delete;

    private KeyValues keyValues;

    AppSettingGUI() {
        keyValues = new KeyValues();
        regenerate.addItemListener(this);
        delete.addItemListener(this);
        updateValueAndUi();
        updateAppSetting();
    }

    boolean isModified() {
        return keyValues.isModified();
    }

    private void updateAppSetting() {
        App.settingRegenerate = (Boolean) keyValues.getCurValue(Keys.regenerate);
        App.settingDelete = (Boolean) keyValues.getCurValue(Keys.delete);
    }

    void apply() {
        keyValues.apply();
        updateValueAndUi();
        updateAppSetting();
    }

    void reset() {
        keyValues.reset();
        updateValueAndUi();
    }

    private void updateValueAndUi() {
        regenerate.setSelected((Boolean) keyValues.getCurValue(Keys.regenerate));
        delete.setSelected((Boolean) keyValues.getCurValue(Keys.delete));
        root.updateUI();
    }

    JPanel getRoot() {
        return root;
    }

    @Override
    public void itemStateChanged(ItemEvent e) {
        if (keyValues != null) {
            Object item = e.getItem();
            if (item == regenerate) {
                keyValues.setCurValue(Keys.regenerate, regenerate.isSelected());
            }
            else if (item == delete) {
                keyValues.setCurValue(Keys.delete, delete.isSelected());
            }
        }
    }

}
