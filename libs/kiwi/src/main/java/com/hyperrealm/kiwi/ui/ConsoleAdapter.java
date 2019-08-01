/* ----------------------------------------------------------------------------
   The Kiwi Toolkit - A Java Class Library
   Copyright (C) 1998-2008 Mark A. Lindner

   This library is free software; you can redistribute it and/or
   modify it under the terms of the GNU General Public License as
   published by the Free Software Foundation; either version 2 of the
   License, or (at your option) any later version.

   This library is distributed in the hope that it will be useful,
   but WITHOUT ANY WARRANTY; without even the implied warranty of
   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
   General Public License for more details.

   You should have received a copy of the GNU General Public License
   along with this library; if not, see <http://www.gnu.org/licenses/>.
   ----------------------------------------------------------------------------
*/

package com.hyperrealm.kiwi.ui;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

import com.hyperrealm.kiwi.io.OutputLoop;
import com.hyperrealm.kiwi.util.LoggingEndpoint;

/**
 * Adapter for using a logging endpoint with the standard output stream.
 * <p>
 * This class allows an arbitrary logging endpoint to be connected (via an
 * <code>OutputLoop</code>) to the standard output stream,
 * <code>System.out</code>. The class starts a separate thread which reads
 * messages from the pipe and writes them to the logging endpoint. Message
 * severity is specified using a message prefix; "warning:", "status:",
 * "info:", or "error:". The default severity is <code>STATUS</code>. For
 * example:
 * <p>
 * <code>LOG.warn("info:Program started.");</code>
 * <p>
 * will log the message "Program started." as an <code>INFO</code> message.
 *
 * @author Mark Lindner
 * @see com.hyperrealm.kiwi.ui.ConsoleFrame
 * @see com.hyperrealm.kiwi.io.OutputLoop
 */

public class ConsoleAdapter {

    private static final String STATUS = "status:";

    private static final String ERROR = "error:";

    private static final String INFO = "info:";

    private static final String WARNING = "warning:";

    private LoggingEndpoint log;

    private BufferedReader reader;

    /**
     * Construct a new <code>ConsoleAdapter</code> for the specified logging
     * endpoint.
     *
     * @param log The <code>LoggingEndpoint</code> to use.
     * @throws java.io.IOException If the output loop could not be created.
     */

    public ConsoleAdapter(LoggingEndpoint log) throws IOException {
        this.log = log;

        OutputLoop pipe = new OutputLoop();
        pipe.setActive(true);

        reader = new BufferedReader(new InputStreamReader(pipe.getInputStream(), StandardCharsets.UTF_8));

        Runnable r = this::run;

        Thread thread = new Thread(r);
        thread.start();
    }

    /* thread body */

    private void run() {
        String s;
        int type;

        try {
            while ((s = reader.readLine()) != null) {
                type = log.STATUS;

                if (s.startsWith(STATUS)) {
                    s = s.substring(STATUS.length());
                    type = log.STATUS;
                } else if (s.startsWith(ERROR)) {
                    s = s.substring(ERROR.length());
                    type = log.ERROR;
                } else if (s.startsWith(INFO)) {
                    s = s.substring(INFO.length());
                    type = log.INFO;
                } else if (s.startsWith(WARNING)) {
                    s = s.substring(WARNING.length());
                    type = log.WARNING;
                }

                log.logMessage(type, s);
            }
        } catch (IOException ex) {
            log.close();
        }
    }

}
