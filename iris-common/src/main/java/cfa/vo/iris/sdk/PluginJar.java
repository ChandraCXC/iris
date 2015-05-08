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

