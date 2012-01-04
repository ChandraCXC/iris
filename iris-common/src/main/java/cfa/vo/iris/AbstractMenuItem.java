/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package cfa.vo.iris;

import javax.swing.Icon;

/**
 *
 * @author olaurino
 */
public abstract class AbstractMenuItem implements IMenuItem {

    private String title;
    private String description;
    private boolean isOnDesktop;
    private IButton button;

    public AbstractMenuItem(String title, String description, boolean isOnDesktop, Icon icon, Icon thumbnail) {
        this.title = title;
        this.description = description;
        this.isOnDesktop = isOnDesktop;
        this.button = new Button(icon, thumbnail);
    }

    public AbstractMenuItem(String title, String description, boolean isOnDesktop, String iconPath, String thumbnailPath) {
        this.title = title;
        this.description = description;
        this.isOnDesktop = isOnDesktop;
        this.button = new Button(iconPath, thumbnailPath);
    }

    public AbstractMenuItem(String title, String description, boolean isOnDesktop, IButton button) {
        this.title = title;
        this.description = description;
        this.isOnDesktop = isOnDesktop;
        this.button = button;
    }


    @Override
    public String getTitle() {
        return title;
    }

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public boolean isOnDesktop() {
        return isOnDesktop;
    }

    @Override
    public IButton getButton() {
        return button;
    }

}
