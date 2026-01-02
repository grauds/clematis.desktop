package jworkspace.runtime;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
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

    /**
     * Output stream for process logs
     */
    private final OutputStream logs = new BufferedOutputStream(new ByteArrayOutputStream());

    public AbstractTask(String name) {
        this.name = name;
    }

    protected AbstractTask() {}

    /**
     * Returns time, elapsed from process start.
     */
    public long getElapsedTime() {
        return (System.currentTimeMillis() - getStartTime().getTime()) / MILLISEC_IN_SECOND;
    }
}
