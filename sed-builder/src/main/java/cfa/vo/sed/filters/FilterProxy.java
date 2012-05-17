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

import cfa.vo.sed.filters.FileFormatManager.CustomFilterContainer;
import cfa.vo.sed.builder.ISegmentMetadata;
import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author olaurino
 */
public final class FilterProxy implements InvocationHandler {

    private Object filterInstance;
    private Map<String, Method> methodMap = new HashMap();
    private Map<String, Field> fieldMap = new HashMap();
    private String name;
    private String version;
    private String description;
    private String author;

    private long lastModified;
    private URL url;

    public FilterProxy(Class filterClass) {
        try {
            this.filterInstance = filterClass.newInstance();
            methodMap.put("data", filterClass.getDeclaredMethod("getData", int.class, int.class));
            methodMap.put("metadata", filterClass.getDeclaredMethod("getMetadata"));
            methodMap.put("url", filterClass.getDeclaredMethod("setUrl", URL.class));
            methodMap.put("description", filterClass.getDeclaredMethod("getDescription"));
            methodMap.put("author", filterClass.getDeclaredMethod("getAuthor"));
            methodMap.put("version", filterClass.getDeclaredMethod("getVersion"));
        } catch (Exception ex) {
            Logger.getLogger(FilterProxy.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public FilterProxy(CustomFilterContainer cont) {
        try {

            Class filterClass = cont.getFilterClass();

            Method dataMethod = cont.getDataMethod();
            Method metadataMethod = cont.getMetadataMethod();

            Field urlField = cont.getUrlField();

            String _name = cont.getName();
            String _author = cont.getAuthor();
            String _description = cont.getDescription();
            String _version = cont.getVersion();

            this.filterInstance = filterClass.newInstance();
            methodMap.put("data", dataMethod);
            methodMap.put("metadata", metadataMethod);
            fieldMap.put("url", urlField);
            this.name = _name;
            this.author = _author;
            this.description = _description;
            this.version = _version;
        } catch (Exception ex) {
            Logger.getLogger(FilterProxy.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private Number[] getData(int segment, int column) throws Exception {
        return (Number[]) methodMap.get("data").invoke(filterInstance, segment, column);
    }

    private List<ISegmentMetadata> getMetadata() throws Exception {
        return (List<ISegmentMetadata>) methodMap.get("metadata").invoke(filterInstance);
    }

    private String getAuthor() throws Exception {
        if(filterInstance instanceof IFilter)
            return (String) methodMap.get("author").invoke(filterInstance);
        else
            return author;
    }

    private String getVersion() throws Exception {
        if(filterInstance instanceof IFilter)
            return (String) methodMap.get("version").invoke(filterInstance);
        else
            return version;
    }

    private void setUrl(URL url) throws Exception {
        if(filterInstance instanceof IFilter)
            methodMap.get("url").invoke(filterInstance, url);
        else
            fieldMap.get("url").set(filterInstance, url);

        this.url = url;
    }

    private boolean wasModified() {
        if(url.getProtocol().equals("file")) {
            File file = new File(url.getFile());
            return lastModified == file.lastModified();
        } else
            return false;

    }

    private String getDescription() throws Exception {
        if(filterInstance instanceof IFilter)
            return (String) methodMap.get("description").invoke(filterInstance);
        else
            return description;
    }

    private String getName() {
        return name;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        if(method.getName().equals("getData"))
            return getData((Integer)args[0], (Integer)args[1]);
        else if(method.getName().equals("getMetadata"))
            return getMetadata();
        else if(method.getName().equals("getDescription"))
            return getDescription();
        else if(method.getName().equals("setUrl"))
            setUrl((URL)args[0]);
        else if(method.getName().equals("getName"))
            return getName();
        else if(method.getName().equals("getAuthor"))
            return getAuthor();
        else if(method.getName().equals("getVersion"))
            return getVersion();
        else if(method.getName().equals("toString"))
            return getName();
        else if(method.getName().equals("wasModified"))
            return wasModified();
        return null;
    }
    
}
