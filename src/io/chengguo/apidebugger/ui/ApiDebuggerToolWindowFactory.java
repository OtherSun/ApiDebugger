package io.chengguo.apidebugger.ui;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowFactory;
import org.jetbrains.annotations.NotNull;

/**
 * Created by fingerart on 17/2/19.
 */
public class ApiDebuggerToolWindowFactory implements ToolWindowFactory {
    public static final String TOOL_WINDOW_ID = "Api Debugger";

    @Override
    public void createToolWindowContent(@NotNull Project project, @NotNull ToolWindow toolWindow) {
        ApiDebuggerView apiDebuggerView = ApiDebuggerView.getInstance(project);
        apiDebuggerView.initApiDebugger(toolWindow);
    }
}
