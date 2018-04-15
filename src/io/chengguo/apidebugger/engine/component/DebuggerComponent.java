package io.chengguo.apidebugger.engine.component;

import com.google.common.eventbus.Subscribe;
import com.intellij.ide.util.PropertiesComponent;
import com.intellij.openapi.actionSystem.*;
import com.intellij.openapi.components.AbstractProjectComponent;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.openapi.components.StoragePathMacros;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowManager;
import com.intellij.openapi.wm.ex.ToolWindowManagerEx;
import com.intellij.openapi.wm.ex.ToolWindowManagerListener;
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentFactory;
import io.chengguo.apidebugger.engine.eventbus.DebuggerEventBus;
import io.chengguo.apidebugger.engine.eventbus.event.NoActionSessionsEvent;
import io.chengguo.apidebugger.engine.log.Log;
import io.chengguo.apidebugger.ui.DebuggerToolWindowFactory;
import io.chengguo.apidebugger.ui.DebuggerToolWindowPanel;
import io.chengguo.apidebugger.ui.ITabbedDebuggerWidget;
import io.chengguo.apidebugger.ui.TabbedDebuggerWidget;
import io.chengguo.apidebugger.ui.action.AddTabAction;
import io.chengguo.apidebugger.ui.action.CloseTabAction;
import io.chengguo.apidebugger.ui.action.SettingsTabAction;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;

/**
 * Created by fingerart on 17/2/19.
 */
@State(name = "ApiDebugger", storages = {@Storage(StoragePathMacros.PROJECT_FILE)})
public class DebuggerComponent extends AbstractProjectComponent {

    private Project mProject;

    public DebuggerComponent(Project project) {
        super(project);
        mProject = project;
        DebuggerEventBus.getDefault().register(this);
    }

    public static DebuggerComponent getInstance(@NotNull Project project) {
        return project.getComponent(DebuggerComponent.class);
    }

    /**
     * 初始化
     *
     * @param toolWindow
     */
    public void initApiDebugger(ToolWindow toolWindow) {
        Content content = createApiDebuggerContentPanel(toolWindow);
        content.setCloseable(true);
        toolWindow.getContentManager().addContent(content);
        ((ToolWindowManagerEx) ToolWindowManager.getInstance(mProject)).addToolWindowManagerListener(createToolWindowListener());
    }

    /**
     * 监听`ToolWindow`的状态，以便在必要时重新进行初始化
     *
     * @return
     */
    private ToolWindowManagerListener createToolWindowListener() {
        return new ToolWindowManagerListener() {
            @Override
            public void toolWindowRegistered(@NotNull String s) {
                Log.d("DebuggerComponent.toolWindowRegistered");
            }

            @Override
            public void stateChanged() {
                ToolWindow toolWindow = ToolWindowManager.getInstance(mProject).getToolWindow(DebuggerToolWindowFactory.TOOL_WINDOW_ID);
                if (toolWindow != null) {
                    if (toolWindow.isVisible() && toolWindow.getContentManager().getContentCount() == 0) {
                        Log.d("DebuggerComponent.isVisible ContentCount>0");
                        initApiDebugger(toolWindow);
                    }
                }
            }
        };
    }

    /**
     * ApiDebugger的整体内容面板
     *
     * @param toolWindow
     * @return
     */
    private Content createApiDebuggerContentPanel(ToolWindow toolWindow) {
        toolWindow.setToHideOnEmptyContent(true);

        DebuggerToolWindowPanel panel = new DebuggerToolWindowPanel(PropertiesComponent.getInstance(mProject), toolWindow);
        Content content = ContentFactory.SERVICE.getInstance().createContent(panel, "", false);

        ITabbedDebuggerWidget debuggerWidget = createContent(content);
        ActionToolbar toolBar = createToolBar(debuggerWidget);

        panel.setToolbar(toolBar.getComponent());
        panel.setContent(debuggerWidget.getComponent());

        return content;
    }

    /**
     * 工具栏
     *
     * @param debuggerWidget
     * @return
     */
    private ActionToolbar createToolBar(ITabbedDebuggerWidget debuggerWidget) {
        DefaultActionGroup group = new DefaultActionGroup();
        group.addAll(
                new AddTabAction(debuggerWidget),
                new CloseTabAction(debuggerWidget),
                new SettingsTabAction()
        );
        ActionToolbar toolbar = ActionManager.getInstance().createActionToolbar(ActionPlaces.UNKNOWN, group, false);
        toolbar.setOrientation(SwingConstants.VERTICAL);
        return toolbar;
    }

    /**
     * ApiDebuger的主要内容面板
     *
     * @param content
     * @return
     */
    private ITabbedDebuggerWidget createContent(Content content) {
        ITabbedDebuggerWidget debuggerWidget = new TabbedDebuggerWidget(mProject, content);
        debuggerWidget.createDebuggerSession();
        return debuggerWidget;
    }

    /**
     * 接受`NoActionSessionsEvent`事件，移除掉窗口中的内容
     *
     * @param event
     */
    @Subscribe
    public void onNoActiveSessions(NoActionSessionsEvent event) {
        Log.d("DebuggerComponent.onNoActionSessions");
        ToolWindowManager.getInstance(mProject).getToolWindow(DebuggerToolWindowFactory.TOOL_WINDOW_ID).getContentManager().removeAllContents(true);
    }

    @Override
    public void projectOpened() {
        Log.d("DebuggerComponent.projectOpened");
    }

    @Override
    public void projectClosed() {
        Log.d("DebuggerComponent.projectClosed");
    }

    @Override
    public void initComponent() {
        Log.d("DebuggerComponent.initComponent");
    }

    @Override
    public void disposeComponent() {
        Log.d("DebuggerComponent.disposeComponent");
    }

    @NotNull
    @Override
    public String getComponentName() {
        return DebuggerComponent.class.getSimpleName();
    }
}
