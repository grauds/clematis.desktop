package jworkspace.ui.runtime.process;

import jworkspace.api.IWorkspaceListener;
import jworkspace.config.ServiceLocator;
import jworkspace.runtime.RuntimeManager;

public class ProcessesController {

    public ProcessesController() {

        ServiceLocator.getInstance().getRuntimeManager().addListener(new IWorkspaceListener() {
            @Override
            public int getCode() {
                return RuntimeManager.BEFORE_EXECUTE_EVENT;
            }

            @Override
            public void processEvent(Integer integer, Object l, Object r) {

            }
        });

        ServiceLocator.getInstance().getRuntimeManager().addListener(new IWorkspaceListener() {
            @Override
            public int getCode() {
                return RuntimeManager.AFTER_EXECUTE_EVENT;
            }

            @Override
            public void processEvent(Integer integer, Object l, Object r) {

            }
        });
    }

    public void kill() {

    }

    public void killAll() {

    }

    public void killAndRemove() {

    }

    public void killAndRemoveAll() {

    }

    public void copyLog() {

    }

    public void update() {

    }
}
