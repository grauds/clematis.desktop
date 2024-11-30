package jworkspace.runtime;

import java.io.OutputStream;
import java.util.Date;

import static com.hyperrealm.kiwi.util.KiwiUtils.MILLISEC_IN_SECOND;
import com.hyperrealm.kiwi.runtime.Task;

import lombok.Getter;
import lombok.Setter;
/**
 * Abstract observable task
 */
@Getter
@Setter
public abstract class AbstractTask extends Task {

    private String name;

    private Date startTime;

    AbstractTask(String name) {
        this.name = name;
    }

    /**
     * Returns time, elapsed from process start.
     */
    public long getElapsedTime() {
        return (System.currentTimeMillis() - getStartTime().getTime()) / MILLISEC_IN_SECOND;
    }

    public abstract OutputStream getLogs();
}
