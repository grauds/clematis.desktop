package jworkspace.runtime.logging;
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
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Consumer;

/**
 * Multiplexes log chunks to multiple registered listeners.
 */
public class BroadcastLogListener implements Consumer<String> {
    private final List<Consumer<String>> targets = new CopyOnWriteArrayList<>();

    public void addTarget(Consumer<String> target) {
        targets.add(target);
    }

    public void removeTarget(Consumer<String> target) {
        targets.remove(target);
    }

    @Override
    public void accept(String textChunk) {
        for (Consumer<String> target : targets) {
            target.accept(textChunk);
        }
    }

    public boolean isEmpty() {
        return targets.isEmpty();
    }
}

