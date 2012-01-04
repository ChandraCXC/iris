/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package cfa.vo.iris;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author olaurino
 */
public class ComponentLoader {

    private static URL componentsURL = ComponentLoader.class.getResource("/components");

    public static void setComponentsURL(URL url) {
        componentsURL = url;
    }

    public static List<IrisComponent> instantiateComponents() throws IOException {
        List<IrisComponent> components = new ArrayList();
        
        String compOverride = System.getProperty("compFile");
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

        return components;
    }
}
