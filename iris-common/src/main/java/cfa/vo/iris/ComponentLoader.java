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
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.lang.StringUtils;

public class ComponentLoader {

    protected URL componentsURL;
    protected List<IrisComponent> irisComponents = new ArrayList<IrisComponent>();
    protected List<String> failures = new ArrayList<String>();
    
    protected static final String COMP_OVERRIDE_SYS_PROP = "compFile";

    public ComponentLoader() {
        componentsURL = this.getClass().getResource("/components");
    }
    
    public ComponentLoader(URL componentsURL) {
        this.componentsURL = componentsURL;
    }

    public List<IrisComponent> instantiateComponents() {

        String compOverride = System.getProperty(COMP_OVERRIDE_SYS_PROP);
        if (!StringUtils.isEmpty(compOverride)) {
            try {
                System.out.println(compOverride);
                File f = new File(compOverride);
                componentsURL = new URL("file:" + f.getAbsolutePath());
            } catch (MalformedURLException ex) {
                String message = "Invalid URL:" + componentsURL;
                System.err.println(message);
                Logger.getLogger(ComponentLoader.class.getName()).log(Level.SEVERE, message, ex);
                return irisComponents;
            }
        }

        List<String> componentsList;
        try {
            componentsList = readComponentsFile();
        } catch (IOException ex) {
            String message = "Cannot read components file at: " + componentsURL;
            System.err.println(message);
            Logger.getLogger(ComponentLoader.class.getName()).log(Level.SEVERE, message, ex);
            return irisComponents;
        }

        initComponents(componentsList);
        
        return irisComponents;
    }
    
    /**
     * @return list of components to load
     * @throws IOException
     *      for issues related to reading componentsURL.
     */
    private List<String> readComponentsFile() throws IOException {
        
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
    
    /**
     * Tries to find and construct the classes enumerated in the components list. Components that cannot
     * be constructed will be logged and skipped.
     */
    private void initComponents(List<String> componentsList) {

        for (String className : componentsList) {
            IrisComponent component = null;
            
            try {
                Logger.getLogger(ComponentLoader.class.getName()).log(Level.INFO, "Loading class: " + className);
                Class componentClass = Class.forName(className);
                component = (IrisComponent) componentClass.newInstance();
            } catch (Exception ex) {
                String message = "Could not construct component " + className;
                System.err.println(message);
                Logger.getLogger(ComponentLoader.class.getName()).log(Level.SEVERE, message, ex);
                failures.add(className);
                continue;
            }
            
            irisComponents.add(component);
        }
    }
}
