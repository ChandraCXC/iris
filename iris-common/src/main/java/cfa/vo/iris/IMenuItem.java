/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package cfa.vo.iris;

/**
 * Interface describing the menu items contributed by a component.
 *
 * @author olaurino
 */
public interface IMenuItem {
    /**
     * String rendered on the menu bar or on the desktop for this menu item
     *
     * @return The title of the menu item
     */
    String getTitle();
    /**
     * Tooltip text to be rendered on the menu bar or on the desktop for this menu item.
     * @return The description of this item
     */
    String getDescription();
    /**
     * Callback that get invoked when the item is selected by the user.
     */
    void onClick();
    /**
     * The value returned by this method determines whether or not a desktop button is created for this
     * item.
     *
     * @return True if the item has to be rendered as a desktop button.
     */
    boolean isOnDesktop();
    /**
     * Graphical representation of the item.
     *
     * @return an IButton instance representing the item.
     */
    IButton getButton();
}
