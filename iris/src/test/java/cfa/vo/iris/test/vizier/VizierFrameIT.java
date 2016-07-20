package cfa.vo.iris.test.vizier;

import cfa.vo.iris.test.IrisAppResource;
import cfa.vo.iris.test.unit.AbstractUISpecTest;
import cfa.vo.iris.test.unit.TestUtils;
import junit.framework.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.uispec4j.Window;
import org.uispec4j.assertion.UISpecAssert;
import org.uispec4j.interception.BasicHandler;
import org.uispec4j.interception.WindowInterceptor;
import uk.ac.starlink.table.StoragePolicy;
import uk.ac.starlink.table.TableSequence;
import uk.ac.starlink.util.FileDataSource;
import uk.ac.starlink.votable.VOTableBuilder;

import static org.junit.Assert.*;

/**
 * Copyright (C) 2016 Smithsonian Astrophysical Observatory
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p/>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p/>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
public class VizierFrameIT extends AbstractUISpecTest {

    @Rule
    public IrisAppResource iris = new IrisAppResource(false, false);

    @Test
    public void testLoad() throws Exception {
        final Window window = iris.getAdapter().getMainWindow();
        TestUtils.invokeWithRetry(50, 100, new Runnable() {
            @Override
            public void run() {
                window.getMenuBar().getMenu("Tools").getSubMenu("VizierClient").getSubMenu("Vizier SED Client").click();
            }
        });

        window.getInputTextBox("jtextField1").setText("3c273");
        window.getInputTextBox("jtextField2").setText("5");
        window.getComboBox().select("Create");
        window.getButton("load").click();

        window.getMenuBar().getMenu("Tools").getSubMenu("SED Builder").getSubMenu("SED Builder").click();
        Window builder = window.getDesktop().getWindow("SED Builder");

        String publisher = (String) builder.getTable().getContentAt(0, 2);
        assert publisher.startsWith("Vizier - CDS");
    }

    @Test
    public void testError() throws Exception {
        final Window window = iris.getAdapter().getMainWindow();
        TestUtils.invokeWithRetry(50, 100, new Runnable() {
            @Override
            public void run() {
                window.getMenuBar().getMenu("Tools").getSubMenu("VizierClient").getSubMenu("Vizier SED Client").click();
            }
        });

        window.getInputTextBox("jtextField1").setText("foo");
        window.getComboBox().select("Create");


        WindowInterceptor.init(window.getButton("load").triggerClick())
                .process(
                        BasicHandler.init()
                                .assertContainsText("Cannot find data for target foo")
                                .triggerButtonClick("OK"))
                .run();
    }

    @Test
    public void testErrorRadius() throws Exception {
        final Window window = iris.getAdapter().getMainWindow();
        TestUtils.invokeWithRetry(50, 100, new Runnable() {
            @Override
            public void run() {
                window.getMenuBar().getMenu("Tools").getSubMenu("VizierClient").getSubMenu("Vizier SED Client").click();
            }
        });

        window.getInputTextBox("jtextField1").setText("3c273");
        window.getInputTextBox("jtextField2").setText("foo");
        window.getComboBox().select("Create");
        WindowInterceptor.init(window.getButton("load").triggerClick())
                .process(
                        BasicHandler.init()
                                .assertContainsText("Not a valid search radius: foo")
                                .triggerButtonClick("OK"))
                .run();
    }
}
