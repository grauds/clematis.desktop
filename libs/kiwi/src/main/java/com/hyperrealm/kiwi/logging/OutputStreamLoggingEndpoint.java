package com.hyperrealm.kiwi.logging;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
/**
 * An implementation of {@link LoggingEndpoint} for output streams.
 *
 * @author Anton Troshin
 */
public class OutputStreamLoggingEndpoint implements LoggingEndpoint {

    private final OutputStream outputStream;

    public OutputStreamLoggingEndpoint(OutputStream outputStream) {
        this.outputStream = outputStream;
    }

    @Override
    public void logMessage(Types type, String message) {
        try {
            outputStream.write(
                (type.getType() + " - " + message).getBytes(StandardCharsets.UTF_8)
            );
        } catch (IOException ignored) {
           // failure to write message is ignored
        }
    }

    @Override
    public void close() {
        try {
            outputStream.close();
        } catch (IOException ignored) {
            // failure to close it is ignored
        }
    }
}
