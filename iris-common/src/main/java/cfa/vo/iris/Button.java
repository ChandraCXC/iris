/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package cfa.vo.iris;

import javax.swing.Icon;
import javax.swing.ImageIcon;

/**
 *
 * @author olaurino
 */
public class Button implements IButton {

    private Icon icon;
    private Icon thumbnail;

    public Button(Icon icon, Icon thumbnail) {
        this.icon = icon;
        this.thumbnail = thumbnail;
    }

    public Button(String iconResourcePath, String thumbnailResourcePath) {
        icon = new ImageIcon(getClass().getResource(iconResourcePath));
        thumbnail = new ImageIcon(getClass().getResource(thumbnailResourcePath));
    }

    @Override
    public Icon getIcon() {
        return icon;
    }

    @Override
    public Icon getThumbnail() {
        return thumbnail;
    }

}
