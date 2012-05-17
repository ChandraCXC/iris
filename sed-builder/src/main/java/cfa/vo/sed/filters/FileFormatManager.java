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

package cfa.vo.sed.filters;

import cfa.vo.sed.builder.SedBuilder;
import cfa.vo.sed.filters.annotations.Data;
import cfa.vo.sed.filters.annotations.Filter;
import cfa.vo.sed.filters.annotations.Metadata;
import cfa.vo.sed.filters.annotations.FileLocation;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author olaurino
 */
public class FileFormatManager {

    private List<IFileFormat> customFormats = new ArrayList();

    private List<Plugin> pluginList = new ArrayList();

    private Map<Plugin, List<IFileFormat>> customMap = new HashMap();

    private File pluginDir;

    public FileFormatManager() {
       String path = SedBuilder.getApplication().getConfigurationDir().getAbsolutePath()+"/plugins";
       pluginDir = new File(path);
       if(!pluginDir.exists())
           pluginDir.mkdirs();
    }

    public void init() {
        for(File f : pluginDir.listFiles()) {
            try {
                URL url = f.toURI().toURL();
                addFormatsFromJar(url, false);
            } catch (Exception ex) {
                Logger.getLogger(FileFormatManager.class.getName()).log(Level.SEVERE, null, ex);
            }
       }
    }

    public String getPluginDir() {
        return pluginDir.getAbsolutePath();
    }

    public List<Plugin> getPluginList() {
        return pluginList;
    }

    public IFilter addFormat(Plugin plugin, Class<? extends IFilter> filterClass) {
        FileFormat f = new FileFormat(filterClass);
        f.setPlugin(plugin);
        customFormats.add(f);
        try {
            return f.getFilter(null);
        } catch (FilterException ex) {
            Logger.getLogger(FileFormatManager.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }

    IFilter addFormat(Plugin plugin, Class<?> filterClass, boolean byProxy) {

        if(!byProxy) {

            return addFormat(plugin, (Class<? extends IFilter>) filterClass);

        } else {

            

            CustomFilterContainer customContainer = new CustomFilterContainer(filterClass);

            IFilter filter = buildInstance(customContainer, null);

            FileFormat f = new FileFormat(filterClass, filter);
            f.setPlugin(plugin);

            customFormats.add(f);

            FilterCache.put(customContainer);

            return filter;

        }
        
    }

    public static IFilter buildInstance(CustomFilterContainer cont, URL url) {
        IFilter filter = (IFilter) Proxy.newProxyInstance(FileFormatManager.class.getClassLoader(),
                new Class[] {IFilter.class}, new FilterProxy(cont));
        filter.setUrl(url);
        return filter;
    }

    public static FileFormatManager getInstance() {
        return FileFormatManagerHolder.INSTANCE;
    }

    public IFileFormat getFormatByName(String name) {
        name = name.toUpperCase().replaceAll(" ", "");
        try {
            return NativeFileFormat.valueOf(name);
        } catch (IllegalArgumentException ex) {
            for (IFileFormat format : customFormats) {
                if(name.equals(format.getName().toUpperCase().replaceAll(" ", "")))
                    return format;
            }
        }

        return null;
    }

    public List<IFileFormat> getFormats() {
        List<IFileFormat> list = new ArrayList();

        IFileFormat[] nff = NativeFileFormat.values();

        List<IFileFormat> nffList = Arrays.asList(nff);

        list.addAll(nffList);

        list.addAll(customFormats);

        return list;
    }

    public List<IFileFormat> getCustomFormats() {
        return customFormats;
    }

    public final void addFormatsFromJar(URL url, boolean save) throws IOException {
        Plugin plugin = new Plugin(url);
        plugin.load();
        if(save) {
            File file = new File(getPluginDir()+"/plugin-"+UUID.randomUUID());
            ReadableByteChannel rbc = Channels.newChannel(url.openStream());
            FileOutputStream fos = new FileOutputStream(file);
            fos.getChannel().transferFrom(rbc, 0, 1<<24);//FIXME Check that this call doesn't have side effects.
            fos.close();
            plugin.setUrl(new URL("file://"+file.getAbsolutePath()));
        }
        pluginList.add(plugin);
    }

    public IFileFormat[] getFormatsArray() {
        return getFormats().toArray(new IFileFormat[]{});
    }

    public void remove(URL url) {
        if(url.getProtocol().equals("file")) {
            for(Iterator<Plugin> it = pluginList.iterator(); it.hasNext();) {
                Plugin plugin = it.next();
                if(plugin.getUrl().toString().equals(url.toString())) {
                    it.remove();
                    File f = new File(url.getFile());
                    f.delete();
                    for(Iterator<IFileFormat> i = customFormats.iterator(); i.hasNext();) {
                        IFileFormat format = i.next();
                        if(format.getPlugin().getUrl().toString().equals(url.toString()))
                            i.remove();
                    }
                }
            }
        }
    }

    private static class FileFormatManagerHolder {
        public static FileFormatManager INSTANCE = new FileFormatManager();
    }

    

    public class CustomFilterContainer {
        private Method dataMethod;
        private Method metadataMethod;
        private Field urlField;

        private String name;
        private String description;
        private String author;
        private String version;

        private Class filterClass;

        public Method getDataMethod() {
            return dataMethod;
        }

        public void setDataMethod(Method dataMethod) {
            this.dataMethod = dataMethod;
        }

        public Class getFilterClass() {
            return filterClass;
        }

        public void setFilterClass(Class filterClass) {
            this.filterClass = filterClass;
        }

        public Method getMetadataMethod() {
            return metadataMethod;
        }

        public void setMetadataMethod(Method metadataMethod) {
            this.metadataMethod = metadataMethod;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getAuthor() {
            return author;
        }

        public void setAuthor(String author) {
            this.author = author;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public String getVersion() {
            return version;
        }

        public void setVersion(String version) {
            this.version = version;
        }

        public Field getUrlField() {
            return urlField;
        }

        public void setUrlField(Field urlField) {
            this.urlField = urlField;
        }


        public CustomFilterContainer(Class filterClass) {

            this.filterClass = filterClass;

            Method[] methods = filterClass.getMethods();

            for(Method m : methods) {
                if(m.isAnnotationPresent(Metadata.class))
                    metadataMethod = m;
                if(m.isAnnotationPresent(Data.class))
                    dataMethod = m;
            }

            Field[] fields = filterClass.getFields();

            for(Field field : fields) {
                if(field.isAnnotationPresent(FileLocation.class))
                    urlField=field;
            }

            Filter f = (Filter) filterClass.getAnnotation(Filter.class);
            name = f.name();
            author = f.author();
            version = f.version();
            description = f.description();
        }
    }

}
