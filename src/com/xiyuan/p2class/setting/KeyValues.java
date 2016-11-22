package com.xiyuan.p2class.setting;

import clojure.lang.Obj;
import com.intellij.ide.util.PropertiesComponent;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by xiyuan_fengyu on 2016/11/22.
 */
class KeyValues {

    static final String keyPackage = "com.xiyuan.PropertiesToClass.setting.";

    private PropertiesComponent properties = PropertiesComponent.getInstance();

    private Map<String, Object> oldValues = new HashMap<>();

    private Map<String, Object> curValues = new HashMap<>();

    private boolean isModified = false;

    KeyValues() {
        oldValues.put(Keys.regenerate, properties.getBoolean(keyPackage + Keys.regenerate));
        oldValues.put(Keys.delete, properties.getBoolean(keyPackage + Keys.delete));
        curValues.putAll(oldValues);
    }

    boolean isModified() {
        return isModified;
    }

    void setCurValue(String key, Object value) {
        curValues.put(key, value);
        checkValueChanged();
    }

    Object getCurValue(String key) {
        return curValues.get(key);
    }

    void apply() {
        oldValues.clear();
        oldValues.putAll(curValues);
        isModified = false;

        for (String key: curValues.keySet()) {
            Object value = curValues.get(key);
            if (value == null) {
                properties.unsetValue(keyPackage + key);
            }
            else {
                if (value instanceof Boolean) {
                    properties.setValue(keyPackage + key, (Boolean) value);
                }
                else {
                    properties.setValue(keyPackage + key, value.toString());
                }
            }
        }
    }

    void reset() {
        curValues.clear();
        curValues.putAll(oldValues);
        isModified = false;
    }

    private void checkValueChanged() {
        for (String key: oldValues.keySet()) {
            if (!oldValues.get(key).equals(curValues.get(key))) {
                isModified = true;
                break;
            }
        }
    }

}
