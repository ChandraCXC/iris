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
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

/**
 *
 * @author olaurino
 */
public class FilterCache {
    private static Map<Class, Map<String, IFilter>> filterMap = new HashMap();

    private static Map<Class, Map<String, CustomFilterContainer>> customMap = new HashMap();

    public static IFilter getInstance(Class filterClass, URL url) throws FilterException {
        if(!filterMap.containsKey(filterClass)) {//filterClass is not in the cache
            if(IFilter.class.isAssignableFrom(filterClass)) {//filterClass implements IFilter
                try {
                    IFilter instance = (IFilter) filterClass.newInstance();
                    instance.setUrl(url);
                    Map<String, IFilter> newmap = new HashMap();
                    newmap.put(url==null ? null : url.toString(), instance);
                    filterMap.put(filterClass, newmap);
                } catch(Exception ex) {
                    throw new FilterException(ex);
                }
            } else {//filterClass was not put in the cache by fileformatmanager!
                throw new FilterException("Illegal access to the FilterCache.");
            }
        }

        Map<String, IFilter> map = filterMap.get(filterClass);
        if(!map.containsKey(url==null? null : url.toString())) {
            if(IFilter.class.isAssignableFrom(filterClass)) {//filterclass is not in the cache for this url but is a IFilter implementation
                try {
                    IFilter instance = (IFilter) filterClass.newInstance();
                    instance.setUrl(url);
                    if(url!=null)
                        map.put(url.toString(), instance);
                    return instance;
                } catch (Exception ex) {
                    throw new FilterException(ex);
                }
            } else {//filterclass is not in the cache for this url and is a proxied filter
                IFilter instance = FileFormatManager.buildInstance(customMap.get(filterClass).get(null), url);
                instance.setUrl(url);
                if(url!=null)
                    filterMap.get(filterClass).put(url.toString(), instance);
                return instance;
            }
        } else {
            IFilter instance = filterMap.get(filterClass).get(url==null? null : url.toString());
            if(instance.wasModified()) {
                remove(url);
                return getInstance(filterClass, url);
            }

            return instance;
        }
    }

    static void put(Class filterClass, IFilter filterInstance) {
        if(filterMap.containsKey(filterClass)) {
            filterMap.get(filterClass).put(filterInstance.getUrl().toString(), filterInstance);
        } else {
            Map<String, IFilter> newmap = new HashMap();
            URL url = filterInstance.getUrl();
            newmap.put(url==null ? null: url.toString(), filterInstance);
            filterMap.put(filterClass, newmap);
        }
    }

    static void put(CustomFilterContainer cont) {
        Map<String, CustomFilterContainer> newmap = new HashMap();
        newmap.put(null, cont);
        customMap.put(cont.getFilterClass(), newmap);
    }

    public static void remove(URL url) {
        for(Entry entry : filterMap.entrySet()) {
            Map map = (Map) entry.getValue();
            map.remove(url.toString());
        }
    }

}
