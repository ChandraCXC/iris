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

import java.net.URL;

/**
 *
 * @author omarlaurino
 */
public class FileFormat implements IFileFormat {

    private Class filterClass;
    private String name;
    private Plugin plugin;

    public FileFormat(String name, Class<? extends IFilter> filterClass) {
        this.name = name;
        this.filterClass = filterClass;
    }

    public FileFormat(Class<? extends IFilter> filterClass) {
        try {
            this.name = FilterCache.getInstance(filterClass, null).getName();
        } catch (FilterException ex) {
            this.name = "UNKNOWN";
        }
        this.filterClass = filterClass;
    }

    public FileFormat(Class<?> filterClass, IFilter filterInstance) {
        this.name = filterInstance.getName();
        this.filterClass = filterClass;
        FilterCache.put(filterClass, filterInstance);
    }

    @Override
    public IFilter getFilter(URL url) throws FilterException {
         return FilterCache.getInstance(filterClass, url);
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public Plugin getPlugin() {
        return plugin;
    }

    @Override
    public void setPlugin(Plugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public String toString() {
        return name;
    }
}
