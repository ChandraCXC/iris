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
 * IrisImporterApp.java
 */
package cfa.vo.iris;

import cfa.vo.iris.test.TestBuilder;
import cfa.vo.iris.test.TestLogger;
import cfa.vo.iris.test.TestSSAServer;
import cfa.vo.iris.test.r.RComponent;
import cfa.vo.iris.test.vizier.VizierClient;
import cfa.vo.sed.builder.SedBuilder;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JDialog;
import javax.swing.UIManager;
import spv.components.BarePlotterTestComponent;

/**
 * The main class of the application.
 */
public class Iris extends AbstractIrisApplication {

    private List<IrisComponent> components = ComponentLoader.instantiateComponents();

    @Override
    public URL getSAMPIcon() {
        return getClass().getResource("/iris_button_tiny.png");
    }

    @Override
    public List<IrisComponent> getComponents() throws IOException {
        return components;
    }

    @Override
    public JDialog getAboutBox() {
        return new About(false);
    }

    public static void main(String[] args) {
        launch(Iris.class, args);
    }

    @Override
    public String getName() {
        return "Iris";
    }

    @Override
    public String getDescription() {
        return "The VAO SED Analysis Tool";
    }

    @Override
    public URL getDesktopIcon() {
        return getClass().getResource("/Iris_logo.png");
    }

    @Override
    public URL getHelpURL() {
        try {
            return new URL("http://cxc.cfa.harvard.edu/iris/v1.1/");
        } catch (MalformedURLException ex) {
            Logger.getLogger(Iris.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }

    @Override
    public void setProperties(List<String> properties) {
        Logger.getLogger("").setLevel(Level.OFF);
        for (String prop : properties) {
            if (prop.equals("test")) {
                TestBuilder tb = new TestBuilder();
                components.add(tb);
                TestLogger tl = new TestLogger();
                components.add(tl);
                TestSSAServer th = new TestSSAServer();
                components.add(th);
                components.add(new BarePlotterTestComponent());
            }

            if (prop.equals("r")) {
                components.add(new RComponent());
            }

            if (prop.equals("lnf")) {
                try {
                    System.out.println("Setting cross platform Look and Feel...");
                    UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
                } catch (Exception ex) {
                    System.out.println("Failed to set the Look and Feel");
                    Logger.getLogger(Iris.class.getName()).log(Level.SEVERE, null, ex);
                }
            }

            if (prop.equals("vizier")) {
                components.add(new VizierClient());
            }


            if (prop.equals("debug")) {
                Logger.getLogger("").setLevel(Level.ALL);
            }
            if (prop.equals("ssa")) {
                SedBuilder.SSA = true;
            }
        }

    }
}
