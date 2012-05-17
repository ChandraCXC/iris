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
