package io.chengguo.apidebugger.ui.action;

import com.intellij.icons.AllIcons;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import io.chengguo.apidebugger.engine.log.Log;

public class SettingsTabAction extends AnAction {
    public SettingsTabAction() {
        super("Settings", "Set the ApiDebugger", AllIcons.General.Settings);
    }

    @Override
    public void actionPerformed(AnActionEvent anActionEvent) {
        Log.d("SettingsTabAction.actionPerformed is called.");
    }
}