/**
 * Copyright (C) 2015 Smithsonian Astrophysical Observatory
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
package cfa.vo.iris.test.unit;

import cfa.vo.iris.IrisComponent;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.uispec4j.Desktop;
import org.uispec4j.UISpecTestCase;
import org.uispec4j.Window;

import java.util.List;

/**
 * Abstract test case for Iris GUI tests. Sets up an Iris workspace,
 * adds whatever components we would like to test, and sets up a 
 * UISpec4j window adapter on the iris application to simulate and verify
 * GUI operations.
 *
 * Implementing classes must return the list of components (instances)
 * to be initialized. The class will initialize the components under the hood
 * using bare bone IrisApplication and IWorkspace instances.
 *
 * Implementing classes can use the protected fields <pre>window</pre> and <pre>desktop</pre>
 * as hooks to start testing their components.
 *
 */
public abstract class AbstractGUITest extends UISpecTestCase {

    protected Desktop desktop;
    protected Window window;
    private ApplicationStub app;
    
    @BeforeClass
    public static void before() {}
    
    @Before
    public void setUp() throws Exception {
        super.setUp();
        System.setProperty("samp", "false");

        StubAdapter adapter = new StubAdapter();
        setAdapter(adapter);
        app = adapter.getIrisApplication();
        
        // Apparently in eclipse you have to execute this manually.
        // IrisApplicationStub.main(new String[0]);
        
        window = getMainWindow();
        desktop = window.getDesktop();
        initComponents();
    }

    private void initComponents() {
        for (IrisComponent c : getComponents()) {
            c.init(app, app.getWorkspace());
            c.initCli(app);
            app.addComponent(c);
        }
    }

    /**
     * Method used to get, from the implementing test cases, the list of components to be initialized
     *
     * @return a {@link List} of {@link IrisComponent}s.
     */
    protected abstract List<IrisComponent> getComponents();

    @After
    public void teardown() {
    }

}
