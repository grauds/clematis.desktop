package jworkspace.api;

import java.util.ArrayList;
import java.util.List;

public class EventsDispatcher {

    /**
     * Listeners for service events.
     */
    private final List<IWorkspaceListener> listeners = new ArrayList<>();

    public boolean addListener(IWorkspaceListener l) {
        if (l != null && !listeners.contains(l)) {
            return listeners.add(l);
        }
        return false;
    }

    /**
     * Remove workspace listener
     */
    public boolean removeListener(IWorkspaceListener l) {
        return listeners.remove(l);
    }

    /**
     * Deliver event to all the listeners
     */
    public void fireEvent(Integer event, Object lparam, Object rparam) {

        for (IWorkspaceListener listener : listeners) {
            if (event == listener.getCode()) {
                listener.processEvent(event, lparam, rparam);
            }
        }
    }
}
