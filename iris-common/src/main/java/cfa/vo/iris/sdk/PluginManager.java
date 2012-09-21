/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cfa.vo.iris.sdk;

import cfa.vo.iris.IMenuItem;
import cfa.vo.iris.IWorkspace;
import cfa.vo.iris.IrisApplication;
import cfa.vo.iris.IrisComponent;
import cfa.vo.iris.events.PluginJarEvent;
import cfa.vo.iris.events.SedCommand;
import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.io.FileUtils;

/**
 *
 * @author olaurino
 */
public class PluginManager extends AbstractIrisComponent {

    private PluginManagerFrame gui;
    private List<PluginJar> jars = new ArrayList();
    private List<IMenuItem> menus = new ArrayList();
    
    public List<PluginJar> getPluginJars() {
        return jars;
    }

    @Override
    public void init(IrisApplication app, IWorkspace workspace) {
        super.init(app, workspace);
        
        menus.add(new PluginMenuItem());
    }
    
    public void load() {
        File componentsDir = new File(app.getConfigurationDir().getAbsolutePath() + "/components");

        File[] jarFiles = componentsDir.listFiles();

        if (jarFiles != null) {
            for (File jar : jarFiles) {
                loadJar("file:" + jar.getAbsolutePath());                
            }
        }
    }
    
    @Override
    public List<IMenuItem> getMenus() {
        return menus;
    }

    public void loadJar(String path) {
        try {
            URL url = new URL(path);
            loadJar(url);
        } catch (Exception ex) {
            Logger.getLogger(PluginManager.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void loadJar(URL url) {
        try {
            PluginJar jar = new PluginJar(url);
            jar.load();
            if (!jar.getPlugins().isEmpty()) {
                String[] paths = url.getFile().split("/");
                String name = paths[paths.length -1];
                File dest = new File(app.getConfigurationDir() + "/components/" + name);
                if(!dest.exists())
                    FileUtils.copyURLToFile(url, dest);
                jars.add(jar);
                jar.setFile(dest);
                for(IrisPlugin p : jar.getPlugins()) {
                    for(IrisComponent c : p.getComponents()) {
                        for(IMenuItem item : c.getMenus()) {
                            item.consolidate(dest);
                        }
                    }
                }
                            
                PluginJarEvent.getInstance().fire(jar, SedCommand.ADDED);
            }
            
        } catch (Exception ex) {
            Logger.getLogger(PluginManager.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void unloadJar(PluginJar jar) {
        PluginJarEvent.getInstance().fire(jar, SedCommand.REMOVED);
        jars.remove(jar);
        FileUtils.deleteQuietly(jar.getFile());
    }

    @Override
    public String getName() {
        return "Plugin Manager";
    }

    @Override
    public String getDescription() {
        return "This component provides the Plugin Management capabilities";
    }

    private class PluginMenuItem extends AbstractPluginMenuItem {

        @Override
        public void onClick() {
            if (gui == null) {
                gui = new PluginManagerFrame(PluginManager.this);
                workspace.addFrame(gui);
            }

            gui.setVisible(true);
        }

        @Override
        public boolean isOnDesktop() {
            return false;
        }

        @Override
        public String getTitle() {
            return "Plugin Manager";
        }

        @Override
        public String getDescription() {
            return "Manage plugins for enabling and disabling additional capabilities";
        }

    }
}
