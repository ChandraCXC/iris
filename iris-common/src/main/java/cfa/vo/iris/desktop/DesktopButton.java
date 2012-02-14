/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package cfa.vo.iris.desktop;

import cfa.vo.iris.IMenuItem;
import java.awt.Color;
import java.awt.event.MouseAdapter;
import javax.swing.JLabel;
import javax.swing.border.LineBorder;

/**
 *
 * @author olaurino
 */
public class DesktopButton extends JLabel {
    public DesktopButton(final IMenuItem item) {
        super();
        this.setForeground(new java.awt.Color(255, 255, 255));
        this.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        this.setIcon(item.getButton().getIcon());
        String title = item.getTitle();
        if(title.contains("|"))
            title = title.split("\\|")[1];
        this.setText(title);
        this.setToolTipText(item.getDescription());
        this.setVerticalAlignment(javax.swing.SwingConstants.TOP);
        this.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        this.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        this.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        this.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                item.onClick();
            }
        });
        
    }


}
