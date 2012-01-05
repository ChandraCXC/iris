/**
 * Copyright (C) 2011 Smithsonian Astrophysical Observatory
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

import java.io.IOException;
import java.net.URL;
import java.util.List;
import javax.swing.JDialog;

/**
 * The main class of the application.
 */
public class Iris extends IrisAbstractApplication {

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

}
