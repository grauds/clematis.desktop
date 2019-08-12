package jworkspace.ui.widgets;

/* ----------------------------------------------------------------------------
   Java Workspace
   Copyright (C) 1999-2002 Anton Troshin

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

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Toolkit;
import java.util.Arrays;
import java.util.Collections;
import java.util.Objects;
import java.util.Vector;

import javax.swing.Action;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.MenuElement;
import javax.swing.SwingUtilities;

import jworkspace.WorkspaceResourceAnchor;

/**
 * @author Anton Troshin
 */
public class JPopupMenuEx extends JPopupMenu {
    /**
     * Take into account most window managers have a task or system
     * bar always on top on the bottom of the screen.  Empirically
     * determined value.
     */
    private static final int TASKBAR_HEIGHT = 55;
    /**
     * More Menu Text - makes changing text easier later
     */
    private static final String MORE = WorkspaceResourceAnchor.getString("JMoreMenu.more");
    /**
     * Manually keep track of height - getPreferredSize/getSize seems
     * to only work properly after the menu has been dispayed at least
     * once.
     */
    private double myHeight;
    /**
     * The maximum number height allowed in a menu
     */
    private double maximumHeight;
    /**
     * "more->" menu - recursive object allows for arbitrarily deep
     * more menus.
     */
    private JMoreMenu moreMenu;

    public JPopupMenuEx() {
        super();
        // Arbitrary Default
        maximumHeight = Toolkit.getDefaultToolkit().
            getScreenSize().getHeight() - TASKBAR_HEIGHT;
        moreMenu = null;
    }

    public JPopupMenuEx(String label) {
        super(label);
        maximumHeight = Toolkit.getDefaultToolkit().
            getScreenSize().getHeight() - TASKBAR_HEIGHT;
        moreMenu = null;
    }

    /**
     * Override the JPopupMenu functionality ....
     * same disclaimer as above
     */
    public JMenuItem add(String string) {
        // Strings do not have a getPreferredSize call
        // Therefore, the only way to determine the height
        // needed is to forcibly insert the item, then remove it.
        JMenuItem retVal = null;

        // Use locals for a convenient reference point when
        // debugging
        double preferredHeight = getPreferredSize().getHeight();
        double menuItemHeight = 0;
        JMenuItem tempMenuItem = super.add(string);
        if (tempMenuItem != null) {
            menuItemHeight = tempMenuItem.getPreferredSize().getHeight();
        }

        super.remove(Objects.requireNonNull(tempMenuItem));

        if ((preferredHeight + menuItemHeight) < maximumHeight) {
            retVal = super.add(string);
        } else {
            createMoreMenu();
            retVal = moreMenu.add(string);
        }
        return retVal;
    }

    /**
     * TEMPORARY - Move to State model once you get height algorithm working
     * Differences between JMenu and JPopup menu make the algorithm somewhat
     * tempermental and a bit unpredictable.  Move into a STATE pattern with
     * message forwarding once you get the algorithm working.
     */
    public JMenuItem add(Action a) {
        // Actions do not have a getPreferredSize call
        // Therefore, the only way to determine the desired height
        // of an action to be inserted is to forcibly insert
        // it and then ask the resulting menu item that
        // is returned for its preferred height.  Then based
        // on that height, determine if we can add it to the
        // base menu or if we must

        JMenuItem retVal;

        // Forcibly insert the item into the menu
        JMenuItem tempMenuItem = super.add(a);
        super.remove(tempMenuItem);

        // Determine if we can insert this into the primary menu
        // or if we must insert into the more menu.
        // Use locals for convenient reference points when debugging
        double preferredHeight = getPreferredSize().getHeight();
        double menuItemHeight = tempMenuItem.getPreferredSize().getHeight();

        if ((preferredHeight + menuItemHeight) < maximumHeight) {
            retVal = super.add(a);
        } else {
            // Create the more menu if necessary
            createMoreMenu();
            // Add item to the More Menu.
            retVal = moreMenu.add(a);
        }
        return retVal;
    }

    /**
     * Override the JPopupMenu functionality, try to provide illusion of a
     * single menu.
     * TEMPORARY - Two methods are temporary, move to a STATE pattern
     * once you get the height algorithm working.
     */
    public JMenuItem add(JMenuItem menuItem) {

        JMenuItem retVal;

        // Use locals for a convenient reference point
        // to check when debugging.
        double menuItemSize = menuItem.getPreferredSize().getHeight();

        if ((myHeight + menuItemSize) < maximumHeight) {
            retVal = super.add(menuItem);
            myHeight += menuItemSize;
        } else {
            // Create the more menu if necessary
            createMoreMenu();
            retVal = moreMenu.add(menuItem);
        }
        return retVal;
    }

    /**
     * Override the JPopup Menu
     */
    public void addSeparator() {
        // Ignore height of separator
        // Not ideal - but typical
        super.addSeparator();
    }

    /**
     * Convenience helper function
     */
    private void createMoreMenu() {
        if (moreMenu == null) {
            moreMenu = new JMoreMenu(MORE);
            moreMenu.setMaximumHeight(maximumHeight);
            super.add(moreMenu);
        }
    }

   /**
     * Get all Components **MINUS THE MORE BUTTONS**
     * Use vectors, they are easier to work with when working with
     * heavily dynamic stuff.
     */
    public Vector getAllSubComponents() {
        Vector<Component> componentVector = new Vector<>();
        Component[] componentArray = getComponents();

        for (Component component : componentArray) {
            // Skip over the more menu item
            if (component instanceof JMenuItem) { // sanity check
                if (component == moreMenu) {
                    componentVector.add(component);
                }
            } else {
                componentVector.add(component);
            }
        }
        if (moreMenu != null) {
            Vector<Component> moreComponents = moreMenu.getAllSubComponents();

            for (int j = 0; j < moreComponents.size(); j++) {
                componentVector.add(moreComponents.elementAt(j));
            }
        }
        return componentVector;
    }

    /**
     * Override the JPopupMenu functionality
     */
    public Component getComponentAtIndex(int i) {
        Component retVal;
        Component[] components = getComponents();

        // I'm not certain what getComponentIndex does here, test.
        if (i < components.length) {
            retVal = super.getComponent(i);
        } else {
            // The extra steps are to make debugging easier.
            retVal = moreMenu.getComponentAtIndex(i - components.length);
        }
        return retVal;
    }

    /**
     * Override the JPopupMenu functionality
     */
    public int getComponentIndex(Component c) {
        int retVal;

        Component[] components = getComponents();
        retVal = super.getComponentIndex(c);

        if (retVal < 0) {
            // Account for more Menu;
            int moreLocation = moreMenu.getComponentIndex(c);
            if (moreLocation != -1) {
                // Account for the more menu item - hence the '-1'
                retVal = (components.length - 1) + moreMenu.getComponentIndex(c);
            }
        }

        return retVal;
    }

    /**
     * Convenience helper - get one of the sub-menus if you
     * want to manipulate it directly (For Example - insert a static
     * item.)
     */
    public JMoreMenu getMoreMenu() {
        return moreMenu;
    }

    /**
     * Override the JPopupMenu functionality
     */
    public MenuElement[] getSubElements() {

        MenuElement[] retVal;

        // Concatenate moreMenu and my elements, but don't
        // include the "more" button item, hence "length-1"
        MenuElement[] subElements = super.getSubElements();
        Vector<MenuElement> elements = new Vector<>(Arrays.asList(subElements).subList(0, subElements.length - 1));

        MenuElement[] moreElements = {};
        if (moreMenu != null) {
            moreElements = moreMenu.getSubElements();
        }
        Collections.addAll(elements, moreElements);

        // Casting caused wierd problems, so we do it the hard way.
        MenuElement[] elementsToReturn = new MenuElement[elements.size()];
        for (int k = 0; k < elements.size(); k++) {
            elementsToReturn[k] = elements.elementAt(k);
        }
        retVal = elementsToReturn;
        return retVal;
    }

    /**
     * Override the JPopupMenu functionality
     */
    public void insert(Component c, int index) {
        int itemCount = getComponentCount();

        if (index < itemCount) {
            super.insert(c, index);
            double componentHeight = c.getPreferredSize().getHeight();
            itemCount += 1; // New Component
            myHeight += componentHeight;
            if (!(myHeight < maximumHeight)) {
                Component componentToMove;
                do {
                    // Get the component closest to the moreMenu item
                    // menu is 0 based, and component is 1 back
                    // hence -2
                    componentToMove = getComponentAtIndex(itemCount - 2);
                    moreMenu.insert(componentToMove, 0);
                    myHeight -= componentToMove.getPreferredSize().getHeight();

                }
                while (myHeight > maximumHeight);
            }
        } else {
            createMoreMenu();
            moreMenu.insert(c, index - itemCount);
        }
    }

    /**
     * Convenience method.  JPopupMenu does not have a insert(String)
     * however, it makes things very convenient
     */
    public void insert(String string, int index) {
        // Strings do not have a getPreferredSize call, hence the onlye
        // way to get their preferred size is to forcibly insert them
        // then ask the resulting JMenuItem what it's size is
        JMenuItem tempItem = super.add(string);
        super.remove(tempItem);
        insert(tempItem, index);
    }

    /**
     * Override the JPopupMenu functionality
     * NOTE - CODE A MUCH MORE COMPLEX THAN I WOULD LIKE.  CONTINUE TO SEARCH
     * FOR A BETTER WAY TO INTEGRATE JPOPUPMENU AND JMENU.
     * The subtle differences between the two is what made this code a
     * nightmare to code up and debug!!!!
     */

    public void insert(Action a, int index) {
        // Actions do not have a getPreferred size call, so we have
        // to forcibly insert it, then ask the result JMenuItem
        // what size it wanted.
        JMenuItem tempItem = super.add(a);
        super.remove(tempItem);
        insert(tempItem, index);
    }

    /**
     * Used to forcibly insert items at the end of the primary
     * menu.  Useful if you have items that you don't want
     * who knows how many levels deep in the more structure.
     */
    public void insertStatic(Component c) {
        // Determine if the Component can fit already
        // If yes
        //   Just do a regular insert, we're done.
        // else
        //   do
        //      Put the standard item closest to the more
        //      menu in the more menu
        //   until the static component (c) can fit in this menu
        //   do a regular insert(component)
        double componentHeight = c.getPreferredSize().getHeight();

        if ((myHeight + componentHeight) < maximumHeight) {
            // Force the component to go below the moreMenu
            super.add(c);
        } else {
            Component componentToMove = null;
            do {
                // indexes are 0 based, the standard item is 1 before
                // the more menu hence -2
                int index = getComponentIndex(moreMenu);
                componentToMove = getComponentAtIndex(index - 1);
                moreMenu.insert(componentToMove, 0);
                super.remove(componentToMove);
                myHeight -= componentToMove.getPreferredSize().getHeight();
            }
            while ((myHeight + componentHeight) > maximumHeight);
            super.add(c);
            myHeight += componentHeight;
        }
    }

    /**
     * Override the JPopupMenu functionality
     * For simplicities sake, do not reshuffle menu for now.
     * Remove is not used as often as add/insert algorithms
     */
    public void remove(int pos) {
        int itemCount = getComponentCount();
        if (pos < itemCount) {
            super.remove(pos);
        } else {
            moreMenu.remove(pos - itemCount);
            // If the more menu is now empty - remove it.
            if (moreMenu.getItemCount() == 0) {
                super.remove(moreMenu);
                moreMenu = null;
            }
        }
    }

    /**
     * Override the JPopupMenu functionality.  For simplicity, do not
     * reshuffle the menu item for now.  Remove is not used as often as
     * add/insert.
     */
    public void remove(Component c) {
        if (getComponentIndex(c) != -1) {
            // We have the component in the main
            // menu
            super.remove(c);
        } else {
            // one of the more menus has it.
            moreMenu.remove(c);
            // If the more menu is now empty - remove it.
            if (moreMenu.getItemCount() == 0) {
                super.remove(moreMenu);
                moreMenu = null;
            }
        }
    }

    /**
     * Override JPopupMenu functionality.  Take into account aut
     */
    public void removeAll() {
        if (moreMenu != null) {
            moreMenu.removeAll();
            moreMenu = null;
        }
        super.removeAll();
        myHeight = 0;
    }

    /**
     * Override the JPopupMenu functionality
     */
    public void setBorderPainted(boolean b) {
        super.setBorderPainted(b);
        if (moreMenu != null) {
            moreMenu.setBorderPainted(b);
        }
    }

    /**
     * Set the maximum height of this menu
     */
    public void setMaximumHeight(double aHeight) {
        maximumHeight = aHeight;
        if (moreMenu != null) {
            moreMenu.setMaximumHeight(aHeight);
        }
    }

    /**
     * Override the JPopupMenu functionality
     */
    public void setSelected(Component c) {
        if (getComponentIndex(c) != -1) {
            // We have the component in the main menu
            super.setSelected(c);
        } else {
            // one of the more menus has it.
            moreMenu.setSelected(c);
        }
    }

    /**
     * Show - Over the JPopupMenu functionality - try to guard against
     * menu going off the screen.  If the max Height is larger than
     * screen area however, we're fried, set the menu size smaller.
     * <p>
     * WARNING - The code here is not very complex, but a bit deceiving
     * Getting this code **CORRECT** is a lot tricker than
     * it looks, primarily due to the fact you don't have
     * easy access to the internal co-ordinate system and
     * associated manipulation routines.  In addition, the
     * getPreferredSize call doesn't seem to work properly
     * until the menu has been displayed at least once.
     * If you don't believe, uncomment out the System.out
     * calls and watch what happens (left in for debugging later).
     * <p>
     * Most of this code is work-around of known problems in Swing
     * with a couple of sequence dependent items. (must be called
     * at the appropriate time).
     * <p>
     * When making mods, proceed with MUCH CAUTION!.
     */
    @SuppressWarnings("checkstyle:MagicNumber")
    public void show(Component invoker, int x, int y) {

        int localX;
        int localY;

        pack(); // Desparation to find out why
        // getPreferredSize() is not  returning correct values

        // WORKAROUND
        // KNOWN PROBLEM IN SWING convertPointToScreen routine,
        // convertPointToScreen only
        // converts one component of the point, not both.
        Point screenWorkAround = invoker.getLocationOnScreen();
        Point screenLocation = new Point((int) screenWorkAround.getX() + x,
            (int) screenWorkAround.getY() + y);
        // END WORKAROUND
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        // KNOWN PROBLEM IN getPreferredSize()
        MenuElement[] elements = getSubElements();

        Component c;
        Dimension componentPreferredSize;

        double computedHeight = 0;
        double computedWidth = 0;

        for (MenuElement element : elements) {
            c = element.getComponent();
            componentPreferredSize = c.getPreferredSize();

            computedHeight += componentPreferredSize.getHeight();
            if (componentPreferredSize.getWidth() > computedWidth) {
                computedWidth = componentPreferredSize.getWidth();
            }
        }
        // Check X location and width
        // if off the screen
        //    reposition the menu
        int maxLocationX = (int) screenLocation.getX() + (int) computedWidth;
        if (maxLocationX > (int) screenSize.getWidth()) {
            // Reposition menu - 25 pixels from the edge just to be safe
            localX = (int) screenSize.getWidth() - (int) computedWidth - 25;
        } else {
            // Nothing to do - save raw screen location for later
            localX = (int) screenLocation.getX();
        }

        // Check Y location and width
        // if off the screen
        //    reposition the menu
        int maxLocationY = (int) screenLocation.getY() + (int) computedHeight;

        if (maxLocationY > (int) screenSize.getHeight() - TASKBAR_HEIGHT) {
            // Reposition - 55 pixels from edge just to be safe
            // Use 55 to accomodate really big Windows Task Bars
            localY = (int) (screenSize.getHeight() - (computedHeight + TASKBAR_HEIGHT));
        } else {
            // Nothing to do - save raw screen location for later
            localY = (int) screenLocation.getY();
        }

        // Final Check - if top of menu is off the screen
        // place menu at the top of the screen
        // Unfortunately, if the final menu is taller
        // than the screen size, we're just plain screwed.
        // Set the maximum height to a smaller height.


        screenLocation.setLocation(localX, localY);
        SwingUtilities.convertPointFromScreen(screenLocation, invoker);
        super.show(invoker, (int) screenLocation.getX(),
            (int) screenLocation.getY());
    }

    /**
     * I ran out of ideas on how to get it to be invisible, so I added
     * some little helpers.  Get Item count but do not include the
     * automatically created more menus.  I could not get the
     * getComponentCount to work transparently.
     */
    public int sizeableGetItemCount() {
        int retVal = 0;
        if (moreMenu != null) {
            retVal = getComponentCount();
        } else {
            int itemCount = getComponentCount();
            retVal = itemCount + moreMenu.sizeableGetItemCount();
        }
        return retVal;
    }
}
