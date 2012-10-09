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
