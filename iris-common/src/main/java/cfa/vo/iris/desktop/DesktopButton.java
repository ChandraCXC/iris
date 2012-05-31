/**
 * Copyright (C) 2012 Smithsonian Astrophysical Observatory
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package cfa.vo.iris.desktop;

import cfa.vo.iris.IMenuItem;
import java.awt.EventQueue;
import java.awt.event.MouseAdapter;
import javax.swing.JLabel;

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
        this.setSize(64, 64);
        this.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                EventQueue.invokeLater(new Runnable() {

                    @Override
                    public void run() {
                        item.onClick();
                    }
                });
                
            }
        });
        
    }


}
