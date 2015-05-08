/**
 * Copyright (C) 2012, 2015 Smithsonian Astrophysical Observatory
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
package cfa.vo.iris.sdk;

import cfa.vo.iris.AbstractMenuItem;
import cfa.vo.iris.Button;
import cfa.vo.iris.IButton;
import java.io.File;

/**
 *
 * @author olaurino
 */
public abstract class AbstractPluginMenuItem extends AbstractMenuItem {
    private IrisPlugin plugin;
    private String iconPath = "/tool.png";
    private String thumbnailPath = "/tool_tiny.png";
    private Button button = new Button(iconPath, thumbnailPath);
    
    public AbstractPluginMenuItem() {
        setTitle("Test");
        setDescription("Test Menu Item Description");
        setIsOnDesktop(true);
    }
    
    public AbstractPluginMenuItem(String title, String description, boolean isOnDesktop, String iconPath, String thumbnailPath) {
        setTitle(title);
        setDescription(description);
        setIsOnDesktop(isOnDesktop);
        setIconPath(iconPath);
        setThumbnailPath(thumbnailPath);
    }
    
    @Override
    public abstract String getTitle();
    
    @Override
    public abstract String getDescription();
    
    public final void setIconPath(String iconPath) {
        this.iconPath = iconPath;
        button = new Button(iconPath, thumbnailPath);
    }
    
    public final void setThumbnailPath(String thumbnailPath) {
        this.thumbnailPath = thumbnailPath;
        button = new Button(iconPath, thumbnailPath);
    }
    
    public void setPlugin(IrisPlugin plugin) {
        this.plugin = plugin;
    }
    
    public IrisPlugin getPlugin() {
        return this.plugin;
    }
    
    @Override
    public IButton getButton() {
        return button;
    }
    
    @Override
    public void consolidate(File file) {
        String path = file.getAbsolutePath();
        iconPath = path+"!"+iconPath;
        thumbnailPath = path+"!"+thumbnailPath;
        button = new Button(iconPath, thumbnailPath);
    }
    
}
