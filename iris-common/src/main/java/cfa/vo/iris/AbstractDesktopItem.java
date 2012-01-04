/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package cfa.vo.iris;

import javax.swing.Icon;

/**
 *
 * 
 *
 * @author olaurino
 */
public abstract class AbstractDesktopItem extends AbstractMenuItem {
    public AbstractDesktopItem(String title, String description, Icon icon, Icon thumbnail) {
        super(title, description, true, icon, thumbnail);
    }

    public AbstractDesktopItem(String title, String description, String iconPath, String thumbnailPath) {
        super(title, description, true, iconPath, thumbnailPath);
    }
    
    public AbstractDesktopItem(String title, String description, IButton button) {
        super(title, description, true, button);
    }
}
