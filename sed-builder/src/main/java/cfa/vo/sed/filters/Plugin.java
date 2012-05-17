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

package cfa.vo.sed.filters;

import cfa.vo.sed.filters.annotations.Filter;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author olaurino
 */
public class Plugin {
    private URL url;
    private List<IFilter> filterList = new ArrayList();

    public List<IFilter> getFilterList() {
        return filterList;
    }

    public URL getUrl() {
        return url;
    }

    public void setUrl(URL url) {
        this.url = url;
    }

    public Plugin(URL url) {

        this.url = url;

    }

    public void load() throws IOException {
        ClassLoader loader = URLClassLoader.newInstance(
            new URL[] { url },
            FileFormatManager.class.getClassLoader()
        );

        JarInputStream jis = null;

        try {
            jis = new JarInputStream(url.openStream());

            JarEntry entry;

            while(true) {
                entry = jis.getNextJarEntry();
                if(entry == null)
                    break;
                String name = entry.getName().replace("/", ".");
                if(name.endsWith(".class")) {
                    name = name.replace(".class", "");
                    Class clazz;

                    try {
                        clazz = loader.loadClass(name);

                        if(IFilter.class.isAssignableFrom(clazz)) {
                            filterList.add(FileFormatManager.getInstance().addFormat(this, clazz, false));
                        }

                        if(clazz.isAnnotationPresent(Filter.class)) {
                            filterList.add(FileFormatManager.getInstance().addFormat(this, clazz, true));
                        }


                    } catch (ClassNotFoundException ex) {
                        Logger.getLogger(FileFormatManager.class.getName()).log(Level.SEVERE, null, ex);
                    }


                }
            }
        } finally {
            if(jis!=null)
                jis.close();
        }
    }

    @Override
    public String toString() {
        String path = url.getFile();
        String name = path.substring(path.lastIndexOf("/"));
        return name;
    }
}
