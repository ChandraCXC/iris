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

import org.junit.Test;
import org.uispec4j.*;

import javax.swing.*;

import static org.junit.Assert.*;

public class BuilderMainViewTest extends AbstractComponentGUITest {
    private Table table;

    @Test
    public void testNewSegment() throws Exception {

        SedlibSedManager manager = (SedlibSedManager) SedBuilder.getWorkspace().getSedManager();

        window.getMenuBar()
                .getMenu("Tools")
                .getSubMenu("SED Builder")
                .getSubMenu("SED Builder")
                .click();

        assertTrue(desktop.containsWindow("SED Builder").isTrue());

        final Window builder = desktop.getWindow("SED Builder");

        Button newSed = builder.getButton("jButton8");

        newSed.click();

        assertEquals(2, manager.getSeds().size());

        builder.getButton("Load File").click();
        assertTrue(desktop.containsWindow("Load an input File").isTrue());
        Window loadFile = desktop.getWindow("Load an input File");

        loadFile.getTextBox("diskTextBox").setText(getClass().getResource("/test_data/3c066aNED.vot").getPath().toString());
        loadFile.getButton("Load Spectrum/SED").click();

        builder.getButton("Load File").click();
        assertTrue(desktop.containsWindow("Load an input File").isTrue());
        loadFile = desktop.getWindow("Load an input File");

        loadFile.getTextBox("diskTextBox").setText(getClass().getResource("/test_data/mine.vot").getPath().toString());
        loadFile.getButton("Load Spectrum/SED").click();

        SwingUtilities.invokeAndWait(new Runnable() {
            @Override
            public void run() {
                table = builder.getTable();
            }
        });

        assertTrue(table.contentEquals(new String[][]{
                {"3C 066A", "35.665, 43.036", "NASA/IPAC Extragalactic Database (NED)", "33"},
                {"3C 066A", "35.665, 43.036", "Me", "3"}
        }).isTrue());

        Button newSegment = builder.getButton("jButton15");

        newSegment.click();

        assertTrue(desktop.containsWindow("Load an input File").isTrue());

        Window loadWindow = desktop.getWindow("Load an input File");

        assertTrue(loadWindow.isVisible().isTrue());

        URL fileUrl = getClass().getResource("/test_data/3c273.dat");

        loadWindow.getRadioButton("Location on Disk:").click();

        loadWindow.getTextBox("diskTextBox").setText(fileUrl.getPath());

        loadWindow.getComboBox("fileFormat").select("ASCII TABLE");

        loadWindow.getButton("Load Spectrum/SED").click();

        assertTrue(desktop.containsWindow("Import Setup Frame").isTrue());

        final Window setupWindow = desktop.getWindow("Import Setup Frame");

        assertTrue(setupWindow.isVisible().isTrue());

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

        SwingUtilities.invokeAndWait(new Runnable() {
            @Override
            public void run() {
                assertTrue(table.contentEquals(new String[][]{
                        {"3C 066A", "35.665, 43.036", "NASA/IPAC Extragalactic Database (NED)", "33"},
                        {"3C 066A", "35.665, 43.036", "Me", "3"},
                        {"Test", "0.1, 0.2", "Me", "455"}
                }).isTrue());

            }
        });
    }

    @Override
    protected IrisComponent getComponent() {
        return new SedBuilder();
    }
}
