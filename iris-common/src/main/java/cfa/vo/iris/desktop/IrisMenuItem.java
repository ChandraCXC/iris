/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package cfa.vo.iris.desktop;

import cfa.vo.iris.IMenuItem;
import javax.swing.JMenuItem;

/**
 *
 * @author olaurino
 */
public class IrisMenuItem extends JMenuItem {
    private String menu = "Tools";

    public String getMenu() {
        return menu;
    }

    public IrisMenuItem(final IMenuItem item) {
        super();
        this.setIcon(item.getButton().getThumbnail());
        String title = item.getTitle();
        if(title.contains("|")) {
            menu = title.split("\\|")[0];
            title = title.split("\\|")[1];
        }
        this.setText(title);
        this.setToolTipText(item.getDescription());
        this.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                item.onClick();
            }
        });
    }
}
