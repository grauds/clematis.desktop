package jworkspace.ui.util;
/* ----------------------------------------------------------------------------
   Java Workspace
   Copyright (C) 2026 Anton Troshin

   This file is part of Java Workspace.

   This application is free software; you can redistribute it and/or
   modify it under the terms of the GNU Library General Public
   License as published by the Free Software Foundation; either
   version 2 of the License, or (at your option) any later version.

   This application is distributed in the hope that it will be useful,
   but WITHOUT ANY WARRANTY; without even the implied warranty of
   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
   Library General Public License for more details.

   You should have received a copy of the GNU Library General Public
   License along with this application; if not, write to the Free
   Software Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.

   The author may be contacted at:

   anton.troshin@gmail.com
  ----------------------------------------------------------------------------
*/
import java.awt.AWTEvent;
import java.awt.Component;
import java.awt.Toolkit;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JPopupMenu;
import javax.swing.SwingUtilities;

import lombok.extern.java.Log;


@SuppressWarnings({
    "checkstyle:regexp",
    "checkstyle:MagicNumber",
    "checkstyle:MultipleStringLiterals",
    "checkstyle:HideUtilityClassConstructor"
})
@Log
public class MacPopupManager {

    private long lastPopupTime = 0;

    private final JPopupMenu popup;

    private final boolean enableGlobalLogging;

    public MacPopupManager(
        JPopupMenu popup,
        boolean enableGlobalLogging
    ) {
        this.popup = popup;
        this.enableGlobalLogging = enableGlobalLogging;
    }

    public void install(Component component) {

        MouseAdapter adapter = new MouseAdapter() {

            @Override
            public void mousePressed(MouseEvent e) {
                log(e, "PRESSED");
                maybeShow(e);
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                log(e, "RELEASED");
                maybeShow(e);
            }

            @Override
            public void mouseClicked(MouseEvent e) {
                log(e, "CLICKED");
            }
        };

        component.addMouseListener(adapter);

        if (enableGlobalLogging) {
            installGlobalLogger();
        }
    }

    @SuppressWarnings({"checkstyle:ReturnCount", "checkstyle:MagicNumber"})
    private void maybeShow(MouseEvent e) {

        if (!e.isPopupTrigger()) {
            return;
        }

        long now = System.currentTimeMillis();
        if (now - lastPopupTime < 150 && enableGlobalLogging) {
            log.info("IGNORED DUPLICATE POPUP");
            return;
        }

        lastPopupTime = now;
        if (enableGlobalLogging) {
            log.info(String.format(
                "SHOW POPUP x=%d y=%d button=%d%n",
                e.getX(),
                e.getY(),
                e.getButton()
            ));
        }

        SwingUtilities.invokeLater(() -> {
            popup.setVisible(false); // defensive reset
            popup.show(
                e.getComponent(),
                e.getX(),
                e.getY()
            );
        });
    }

    @SuppressWarnings("checkstyle:ReturnCount")
    private void installGlobalLogger() {

        Toolkit.getDefaultToolkit().addAWTEventListener(
            event -> {

                if (!(event instanceof MouseEvent e)) {
                    return;
                }

                String type = switch (e.getID()) {
                    case MouseEvent.MOUSE_PRESSED -> "PRESSED";
                    case MouseEvent.MOUSE_RELEASED -> "RELEASED";
                    case MouseEvent.MOUSE_CLICKED -> "CLICKED";
                    case MouseEvent.MOUSE_DRAGGED -> "DRAGGED";
                    default -> null;
                };

                if (type == null) {
                    return;
                }

                Component source = e.getComponent();

                log.info(String.format(
                    "GLOBAL %-10s button=%d popup=%s consumed=%s source=%s x=%d y=%d%n",
                    type,
                    e.getButton(),
                    e.isPopupTrigger(),
                    e.isConsumed(),
                    source == null ? "null" : source.getClass().getSimpleName(),
                    e.getX(),
                    e.getY()
                ));
            },
            AWTEvent.MOUSE_EVENT_MASK | AWTEvent.MOUSE_MOTION_EVENT_MASK
        );
    }

    private void log(MouseEvent e, String phase) {
        if (enableGlobalLogging) {
            log.info(String.format(
                "%s | button=%d | popup=%s | x=%d y=%d%n",
                phase,
                e.getButton(),
                e.isPopupTrigger(),
                e.getX(),
                e.getY()
            ));
        }
    }
}