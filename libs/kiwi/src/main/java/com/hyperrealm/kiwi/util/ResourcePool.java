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

package com.hyperrealm.kiwi.util;

/**
 * An abstract class that represents a pool of instances of some resource.
 * See <code>TimerPool</code> for an example concrete implementation. Accesses
 * to the pool are threadsafe so there is no possibility of contention for
 * the resource.
 *
 * @param <R>
 * @author Mark Lindner
 * @see com.hyperrealm.kiwi.util.Resource
 * @see com.hyperrealm.kiwi.util.TimerPool
 */

public abstract class ResourcePool<R extends Resource> {

    private final int size;

    private final Stack<R> reservedList;
    private final Stack<R> availableList;

    /**
     * Construct a new <code>ResourcePool</code> of the given size.
     *
     * @param size The number of instances of a resource to preallocate in this
     *             pool.
     */

    public ResourcePool(int size) {
        this.size = size;
        reservedList = new Stack<>();
        availableList = new Stack<>();

        for (int i = 0; i < size; i++) {
            availableList.push(construct());
        }
    }

    /**
     * Reserve one instance of the resource. If all instances are currently in
     * use, this method blocks until one becomes available.
     *
     * @return An instance of the <code>Resource</code>.
     */

    public synchronized R reserve() throws InterruptedException {
        while (availableList.isEmpty()) {
            System.err.println("All resource instances in use; waiting...");
            wait();
        }

        R resource = availableList.pop();
        reservedList.push(resource);
        resource.reserve();

        return (resource);
    }

    /**
     * Release the given resource. If the resource is not currently reserved,
     * this method does nothing. Note that it is the caller's responsibility
     * to pass the correct resource to this method; the method does not check
     * if the calling thread actually has the specified resource reserved.
     *
     * @param resource The <code>Resource</code> to release.
     */

    public synchronized void release(R resource) {
        if (!(reservedList.contains(resource))) {
            throw (new IllegalArgumentException(
                "Resource not managed by this pool!"));
        }

        reservedList.remove(resource);
        resource.release();
        availableList.push(resource);
        notify();
    }

    /**
     * Construct an instance of the resource that is managed by this pool.
     * The constructor calls this method repeatedly to pre-build the number
     * of instances specified as its argument.
     *
     * @return The newly-constructed <code>Resource</code> instance.
     */

    abstract R construct();

    /**
     * Get the total number of resource instances in this pool.
     *
     * @return The total number of instances.
     */

    public int getTotalCount() {
        return (size);
    }

    /**
     * Get the number of resource instances that are currently in use.
     *
     * @return The number of instances that are in use.
     */

    public synchronized int getUsedCount() {
        return (reservedList.size());
    }

    /**
     * Get the number of resource instances that are currently available.
     *
     * @return The number of instances that are available.
     */

    public synchronized int getAvailableCount() {
        return (availableList.size());
    }

}
