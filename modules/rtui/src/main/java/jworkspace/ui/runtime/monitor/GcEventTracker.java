package jworkspace.ui.runtime.monitor;

import java.lang.management.GarbageCollectorMXBean;
import java.lang.management.ManagementFactory;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import javax.management.NotificationEmitter;

public final class GcEventTracker {

    private static final List<Long> GC_TIMESTAMPS = new CopyOnWriteArrayList<>();

    static {
        for (GarbageCollectorMXBean bean : ManagementFactory.getGarbageCollectorMXBeans()) {

            if (bean instanceof NotificationEmitter emitter) {
                emitter.addNotificationListener((n, hb) -> {
                    if (n.getType().contains("gc")) {
                        GC_TIMESTAMPS.add(System.currentTimeMillis());
                    }
                }, null, null);
            }
        }
    }

    private GcEventTracker() {}

    public static List<Long> recentEvents(long windowMs) {
        long now = System.currentTimeMillis();
        GC_TIMESTAMPS.removeIf(t -> now - t > windowMs);
        return GC_TIMESTAMPS;
    }
}
