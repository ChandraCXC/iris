/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package cfa.vo.iris;

import javax.swing.Icon;

/**
 * A simple interface for buttons
 *
 * @author olaurino
 */
public interface IButton {
    /**
     * The image that graphically represents this button.
     * @return an Icon instance
     */
    Icon getIcon();
    /**
     * The thumbnail that graphically represents this button.
     * @return an Icon instance
     */
    Icon getThumbnail();
}
