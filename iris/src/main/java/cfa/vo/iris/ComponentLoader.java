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

package cfa.vo.iris;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author olaurino
 */
public class ComponentLoader {

    private static URL componentsURL = ComponentLoader.class.getResource("/components");

    public static void setComponentsURL(URL url) {
        componentsURL = url;
    }

    public static List<IrisComponent> instantiateComponents() {
        List<IrisComponent> components = new ArrayList();
        
        String compOverride = System.getProperty("compFile");

        try {

            if(compOverride!=null) {
                    File f = new File(compOverride);
                    componentsURL = new URL("file:"+f.getAbsolutePath());
            }

            InputStream is = componentsURL.openStream();

            BufferedReader r = new BufferedReader(new InputStreamReader(is));

            String line;

            while((line = r.readLine()) != null) {
                try {
                    Class componentClass = Class.forName(line);
                    components.add((IrisComponent) componentClass.newInstance());
                } catch (Exception ex) {
                    System.out.println("Can't find or instantiate class: "+line);
                }
            }

            r.close();
        } catch (Exception ex) {
            System.out.println("Can't read"+componentsURL);
            Logger.getLogger(ComponentLoader.class.getName()).log(Level.SEVERE, null, ex);
            return new ArrayList();
        }

        return components;
    }
}
