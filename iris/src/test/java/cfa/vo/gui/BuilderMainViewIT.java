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
package cfa.vo.gui;

import cfa.vo.iris.IrisComponent;
import cfa.vo.iris.sed.ExtSed;
import cfa.vo.iris.sed.SedlibSedManager;
import cfa.vo.iris.test.unit.AbstractComponentGUITest;
import cfa.vo.sed.builder.SedBuilder;
import cfa.vo.sedlib.Sed;
import cfa.vo.sedlib.Segment;
import cfa.vo.sedlib.io.SedFormat;
import java.net.URL;
import junit.framework.Assert;
import org.uispec4j.*;


public class BuilderMainViewIT extends AbstractComponentGUITest {

    public void testNewSegment() throws Exception {

        SedlibSedManager manager = (SedlibSedManager) SedBuilder.getWorkspace().getSedManager();

        window.getMenuBar()
                .getMenu("Tools")
                .getSubMenu("SED Builder")
                .getSubMenu("SED Builder")
                .click();

        desktop.containsWindow("SED Builder");

        Window window = desktop.getWindow("SED Builder");

        Button newSed = window.getButton("jButton8");

        newSed.click();

        Assert.assertEquals(2, manager.getSeds().size());

        ExtSed sed = manager.getSelected();

        Segment s = Sed.read(getClass().getResource("/test_data/3c066aNED.vot").openStream(), SedFormat.VOT).getSegment(0);

        sed.addSegment(s);

        s = Sed.read(getClass().getResource("/test_data/mine.vot").openStream(), SedFormat.VOT).getSegment(0);

        sed.addSegment(s);

        Table table = window.getTable();

        assertTrue(table.contentEquals(new String[][]{
                    {"3C 066A", "35.665, 43.036", "NASA/IPAC Extragalactic Database (NED)", "33"},
                    {"3C 066A", "35.665, 43.036", "Me", "3"}
                }));

        Button newSegment = window.getButton("jButton15");

        newSegment.click();

        Assert.assertTrue(desktop.containsWindow("Load an input File").isTrue());

        Window loadWindow = desktop.getWindow("Load an input File");

        Assert.assertTrue(loadWindow.isVisible().isTrue());

        URL fileUrl = getClass().getResource("/test_data/3c273.dat");

        loadWindow.getRadioButton("Location on Disk:").click();

        loadWindow.getTextBox("diskTextBox").setText(fileUrl.getPath());

        loadWindow.getComboBox("fileFormat").select("ASCII TABLE");

        loadWindow.getButton("Load Spectrum/SED").click();

        Assert.assertTrue(desktop.containsWindow("Import Setup Frame").isTrue());

        Window setupWindow = desktop.getWindow("Import Setup Frame");

        Assert.assertTrue(setupWindow.isVisible().isTrue());

        setupWindow.getComboBox("xColumn").select("DataSpectralValue");

        setupWindow.getComboBox("xQuantity").select("Frequency");

        setupWindow.getComboBox("yColumn").select("DataFluxValue");

        setupWindow.getComboBox("yQuantity").select("FLUXDENSITY");

        setupWindow.getComboBox("yUnit").select("Jy");

        setupWindow.getRadioButton("symmetricColumn").click();

        setupWindow.getComboBox("symmetricColumnValue").select("DataFluxStatErr");

        setupWindow.getTextBox("targetName").setText("Test");

        setupWindow.getTextBox("targetRa").setText("0.1");

        setupWindow.getTextBox("targetDec").setText("0.2");

        setupWindow.getTextBox("publisherText").setText("Me");

        setupWindow.getButton("Add Segment to SED").click();

        assertTrue(table.contentEquals(new String[][]{
                    {"3C 066A", "35.665, 43.036", "NASA/IPAC Extragalactic Database (NED)", "33"},
                    {"3C 066A", "35.665, 43.036", "Me", "3"},
                    {"Test", "0.1, 0.2", "Me", "455"}
                }));

    }

    @Override
    protected IrisComponent getComponent() {
        return new SedBuilder();
    }
}
