package jworkspace.util.sort;

/* ----------------------------------------------------------------------------
   Java Workspace
   Copyright (C) 1999-2003 Anton Troshin

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

import java.util.Vector;

/**
 *
 * Implements the QuickSort algorythm to sort a vector of objects.
 * Objects can be of class String, Integer or any IComparable implementer.
 *
 */
public class QuickSort
{
    private Sorter sorter = null;
    private boolean inverseOrder = false;

    /**
     *
     * Constructs an instance of QuickSort with a default sorter,
     * wich relies on the 'Comparable' interface in the sorted object
     * themselves or sorts String an Integer objects using their
     * natural compare methods.
     *
     */
    public QuickSort()
    {

        this(null);

    }

    /**
     *
     * Constructs an instance of QuickSort with a given sorter. If
     * sorter is not null, 'Comparable' interface is ignored in
     * the sorted objects. In other words, explicit specification of
     * a sorter class will override any functionality behind the
     * Comparable interface of the objects.
     *
     */

    public QuickSort(Sorter sorter)
    {

        super();
        this.sorter = sorter;

    }

    /**
     *
     * Constructs an instance of QuickSort with a default sorter,
     * wich relies on the 'Comparable' interface in the sorted object
     * themselves or sorts String an Integer objects using their
     * natural compare methods.
     *
     */
    public QuickSort(boolean inverseOrder)
    {

        this(null);
        this.inverseOrder = inverseOrder;
    }

    /**
     *
     * The comparing method.
     *
     */
    private int compare(Object obj1, Object obj2)
    {

        int result = 0;

        if (obj1 == obj2) return 0;

        if (sorter != null)
            result = sorter.compare(obj1, obj2);
        else if (obj1 instanceof Comparable)
            result = ((Comparable) obj1).compareTo(obj2);
        else if (obj1 instanceof String)
            result = -((String) obj1).compareTo((String) obj2);
        else if (obj1 instanceof Integer)
            result = ((Integer) obj1).intValue() - ((Integer) obj2).intValue();

        return inverseOrder ? -result : result;

    }

    /**
     *
     *
     *
     * @return boolean
     */
    public boolean getInverseOrder()
    {
        return inverseOrder;
    }

    /**
     *
     *
     *
     * @param newValue boolean
     */
    public void setInverseOrder(boolean newValue)
    {
        this.inverseOrder = newValue;
    }

    public void sort(Vector objects)
    {

        sort(objects, 0, objects.size() - 1, null);

    }

    public void sort(Vector objects, Vector[] data)
    {

        sort(objects, 0, objects.size() - 1, data);

    }

    public void sort(Vector objects, int left, int right)
    {

        sort(objects, left, right, null);

    }

    public void sort(Vector objects, int left, int right, Vector[] data)
    {

        if (left > right) return;

        int i, j, k;
        Object object;
        do
        {
            i = left;
            j = right;
            object = objects.elementAt((left + right) / 2);
            do
            {
                while (compare(object, objects.elementAt(i)) < 0) i++;
                while (compare(object, objects.elementAt(j)) > 0) j--;
                if (i <= j)
                {
                    Object tmp = objects.elementAt(i);
                    objects.setElementAt(objects.elementAt(j), i);
                    objects.setElementAt(tmp, j);
                    if (data != null)
                        for (k = 0; k < data.length; k++)
                        {
                            tmp = data[k].elementAt(i);
                            data[k].setElementAt(data[k].elementAt(j), i);
                            data[k].setElementAt(tmp, j);
                        }
                    i++;
                    j--;
                }
            }
            while (i <= j);
            if (left < j) sort(objects, left, j, data);
            left = i;
        }
        while (i < right);

    }
}