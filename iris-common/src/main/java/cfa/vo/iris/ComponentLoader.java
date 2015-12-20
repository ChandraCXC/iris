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
package cfa.vo.iris;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.lang.StringUtils;

public class ComponentLoader {
    private Logger logger = Logger.getLogger(ComponentLoader.class.getName());

    protected TreeMap<String, IrisComponent> components = new TreeMap<>();
    protected List<String> failures = new ArrayList<>();

    public ComponentLoader(Collection<Class<? extends IrisComponent>> componentList) {
        for (Class c : componentList) {
            loadComponent(c);
        }
    }
    
    public ComponentLoader(URL componentsURL) {
        try {
            initComponents(readComponentsFile(componentsURL));
        } catch (IOException ex) {
            String message = "Cannot read components file at: " + componentsURL;
            System.err.println(message);
            Logger.getLogger(ComponentLoader.class.getName()).log(Level.SEVERE, message, ex);
        }
    }

    public Collection<IrisComponent> getComponents() {
        return components.values();
    }

    IrisComponent getComponent(String name){
        return components.get(name);
    }

    public void initComponents(IrisApplication app, IWorkspace ws) {
        for (IrisComponent component : components.values()) {
            component.init(app, ws);
        }
    }

    public Map<String, IrisComponent> initComponentsCli(IrisApplication app) {
        Map<String, IrisComponent> componentsMap = new HashMap<>();
        try {
            for (IrisComponent component : components.values()) {
                component.initCli(app);
                componentsMap.put(component.getCli().getName(), component);
            }
        } catch (Exception ex) {
            // FIXME Do we want to application to continue if we can't load any components?
            System.err.println("Error reading component file");
            logger.log(Level.SEVERE, "Error reading component file", ex);
        }
        return componentsMap;
    }

    /**
     * @return list of components to load
     * @throws IOException
     *      for issues related to reading componentsURL.
     */
    private List<String> readComponentsFile(URL componentsURL) throws IOException {
        
        if (componentsURL == null) {
            throw new IOException("No components file specified!");
        }

        InputStream is = componentsURL.openStream();
        BufferedReader r = new BufferedReader(new InputStreamReader(is));
        List<String> componentsList = new LinkedList<String>();
        
        String line;
        while (!StringUtils.isEmpty(line = r.readLine())) {
            componentsList.add(line);
        }
        
        r.close();
        return componentsList;
    }

    public final void loadComponent(Class<? extends IrisComponent> componentClass) {
        instantiateComponent(componentClass);
    }

    private IrisComponent instantiateComponent(Class<? extends IrisComponent> componentClass) {
        try {
            Logger.getLogger(ComponentLoader.class.getName()).log(Level.INFO, "Loading class: " + componentClass.getName());
            IrisComponent component = componentClass.newInstance();
            components.put(component.getCli().getName(), component);
            return component;
        } catch (Exception ex) {
            String message = "Could not construct component " + componentClass.getName();
            System.err.println(message);
            Logger.getLogger(ComponentLoader.class.getName()).log(Level.SEVERE, message, ex);
            failures.add(componentClass.getName());
            return null;
        }
    }

    public IrisComponent loadComponent(Class<? extends IrisComponent> componentClass, IrisApplication app, IWorkspace ws) {
        IrisComponent component = instantiateComponent(componentClass);
        if (component != null) {
            component.initCli(app);
            component.init(app, ws);
        }
        return component;
    }
    
    /**
     * Tries to find and construct the classes enumerated in the components list. Components that cannot
     * be constructed will be logged and skipped.
     */
    private void initComponents(List<String> componentsList) {
        for (String className : componentsList) {
            try {
                Class clazz = Class.forName(className);
                loadComponent(clazz);
            } catch (ClassNotFoundException ex) {
                String message = "cannot find class " + className;
                System.err.println(message);
                Logger.getLogger(ComponentLoader.class.getName()).log(Level.SEVERE, message, ex);
                failures.add(className);
            }
        }
    }

    public void shutdown() {
        for (IrisComponent component : components.values()) {
            component.shutdown();
        }
    }
}
