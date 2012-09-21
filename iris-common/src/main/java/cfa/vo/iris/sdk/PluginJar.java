/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cfa.vo.iris.sdk;

import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;

/**
 *
 * @author olaurino
 */
public class PluginJar {

    private List<IrisPlugin> plugins = new ArrayList();
    private URL path;
    private File file;

    public PluginJar(URL path) {
        this.path = path;
    }

    public URL getPath() {
        return path;
    }

    public void setPath(URL path) {
        this.path = path;
    }

    public List<IrisPlugin> getPlugins() {
        return plugins;
    }

    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }
    
    public void add(IrisPlugin plugin) {
        plugins.add(plugin);
    }
    
    public void remove(IrisPlugin plugin) {
        plugins.remove(plugin);
    }

    public void load() throws Exception {
        ClassLoader loader = URLClassLoader.newInstance(
                new URL[]{path},
                PluginJar.class.getClassLoader());


        JarInputStream jis = null;

        try {
            jis = new JarInputStream(path.openStream());

            JarEntry entry;

            while (true) {
                entry = jis.getNextJarEntry();
                if (entry == null) {
                    break;
                }
                String name = entry.getName().replace("/", ".");
                if (name.endsWith(".class")) {
                    name = name.replace(".class", "");
                    Class clazz = loader.loadClass(name);

                    if (IrisPlugin.class.isAssignableFrom(clazz)) {
                        add((IrisPlugin) clazz.newInstance());
                    }



                }
            }
        } finally {
            if (jis != null) {
                jis.close();
            }
        }
    }
    
    @Override
    public String toString() {
        String[] paths = getPath().getFile().split("/");
        return paths[paths.length-1];
    }


    
}

