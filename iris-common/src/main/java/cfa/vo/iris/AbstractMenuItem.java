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
