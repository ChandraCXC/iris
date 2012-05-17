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
package cfa.vo.sed.builder.gui;

import cfa.vo.iris.sed.ExtSed;
import cfa.vo.iris.sed.SedlibSedManager;
import cfa.vo.sed.builder.SedBuilder;
import cfa.vo.sed.gui.SedBuilderMainView;
import cfa.vo.sed.test.App;
import cfa.vo.sed.test.DesktopWs;
import cfa.vo.sed.test.Oracle;
import cfa.vo.sedlib.Sed;
import cfa.vo.sedlib.Segment;
import cfa.vo.sedlib.io.SedFormat;
import java.net.URL;
import javax.swing.JDesktopPane;
import junit.framework.Assert;
import org.uispec4j.Window;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.uispec4j.Button;
import org.uispec4j.Desktop;
import org.uispec4j.Table;
import org.uispec4j.UISpecTestCase;

/**
 *
 * @author olaurino
 */
public class BuilderMainViewTest extends UISpecTestCase {

    private Window mainWindow;
    private Window configurationWindow;

    public BuilderMainViewTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
        mainWindow = null;
        configurationWindow = null;
    }

    @Test
    public void testNewSegment() throws Exception {

        Oracle oracle = new Oracle();

        SedBuilder builder = new SedBuilder();
        builder.init(new App(), new DesktopWs());

        SedlibSedManager manager = (SedlibSedManager) SedBuilder.getWorkspace().getSedManager();

        SedBuilderMainView mainView = new SedBuilderMainView(manager, SedBuilder.getWorkspace().getRootFrame());

        //Test sedFrame name resolver
        mainWindow = new Window(mainView);

        Button newSed = mainWindow.getButton("jButton8");

        newSed.click();

        Thread.sleep(2000);

        Assert.assertEquals(1, manager.getSeds().size());

        ExtSed sed = manager.getSelected();

        Segment s = Sed.read(getClass().getResource("/test_data/3c066aNED.vot").openStream(), SedFormat.VOT).getSegment(0);

        sed.addSegment(s);

        s = Sed.read(getClass().getResource("/test_data/mine.vot").openStream(), SedFormat.VOT).getSegment(0);

        sed.addSegment(s);

        Table table = mainWindow.getTable();

        assertTrue(table.contentEquals(new String[][]{
                    {"35.665, 43.036", "NASA/IPAC Extragalactic Database (NED)", "33"},
                    {"35.665, 43.036", "Me", "3"}
                }));

        Button newSegment = mainWindow.getButton("jButton3");

        newSegment.click();

        JDesktopPane desk = SedBuilder.getWorkspace().getDesktop();

        Desktop desktop = new Desktop(desk);

        Assert.assertTrue(desktop.containsWindow("Load an input File").isTrue());

        Window loadWindow = desktop.getWindow("Load an input File");

        Assert.assertTrue(loadWindow.isVisible().isTrue());

        URL fileUrl = getClass().getResource("/test_data/3c273.dat");

        loadWindow.getRadioButton("Location on Disk:").click();

        loadWindow.getTextBox("diskTextBox").setText(fileUrl.getPath());

        loadWindow.getComboBox("fileFormat").select("ASCII TABLE");

        loadWindow.getButton("Load").click();

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
                    {"35.665, 43.036", "NASA/IPAC Extragalactic Database (NED)", "33"},
                    {"35.665, 43.036", "Me", "3"},
                    {"0.1, 0.2", "Me", "455"}
                }));


//
//        //Test configuration window creation
//        URL fileURL = URLTestConverter.getURL("test:///test_data/3c273.csv");
//
//        ISegmentMetadata metadata = SegmentImporter.getSegmentsMetadata(fileURL, NativeFileFormat.CSV).get(0);

//        SetupFrame configurationFrame = new SetupFrame("test segment", metadata, sedFrame, fileURL.getFile(), NativeFileFormat.CSV, 0);
//
//        configurationWindow = new Window(configurationFrame);
//
//        //Test target information
//        targetRa = configurationWindow.getInputTextBox("targetRa");
//        targetDec = configurationWindow.getInputTextBox("targetDec");
//        targetName = configurationWindow.getInputTextBox("targetName");
//
//        Assert.assertEquals(targetRa.getText().split("\\.")[0], "187");
//        Assert.assertEquals(targetDec.getText().split("\\.")[0], "2");
//        Assert.assertEquals(targetName.getText(), "3c273");

//        //Test name resolver
////         resolveButton = configurationWindow.getButton("Resolve");
////         targetName.setText("m1");
////         resolveButton.click();
//
////         Assert.assertEquals(targetRa.getText().split("\\.")[0], "83");
////         Assert.assertEquals(targetDec.getText().split("\\.")[0], "22");
//
//        oracle.put("targetName", "3c273");
////         oracle.put("targetRa", "83.633125");
//
//        //Test Initial Validation messages
//        TextBox validation = configurationWindow.getTextBox("validation");
//        Assert.assertTrue(validation.getText().contains("Please enter a valid quantity for X values (current quantity: null)"));
//        Assert.assertTrue(validation.getText().contains("Please enter a valid quantity for Y values (current quantity: null)"));
//        Assert.assertTrue(validation.getText().contains("Please choose a Y Error type."));
//
//        ComboBox xColumnCombo = configurationWindow.getComboBox("xColumn");
//        ComboBox xQuantityCombo = configurationWindow.getComboBox("xQuantity");
//        ComboBox xUnitsCombo = configurationWindow.getComboBox("xUnits");
//
//        ComboBox yColumnCombo = configurationWindow.getComboBox("yColumn");
//        ComboBox yQuantityCombo = configurationWindow.getComboBox("yQuantity");
//        ComboBox yUnitsCombo = configurationWindow.getComboBox("yUnits");
//
//        //Test Column contents
//        ISegmentColumn xColumn0 = (ISegmentColumn) xColumnCombo.getAwtComponent().getItemAt(0);
//        ISegmentColumn xColumn7 = (ISegmentColumn) xColumnCombo.getAwtComponent().getItemAt(7);
//        Assert.assertEquals("DataPointNumber", xColumn0.getName());
//        Assert.assertEquals("DataFluxStatErr", xColumn7.getName());
//        ISegmentColumn yColumn0 = (ISegmentColumn) yColumnCombo.getAwtComponent().getItemAt(0);
//        ISegmentColumn yColumn7 = (ISegmentColumn) yColumnCombo.getAwtComponent().getItemAt(7);
//        Assert.assertEquals("DataPointNumber", yColumn0.getName());
//        Assert.assertEquals("DataFluxStatErr", yColumn7.getName());
//
//        //Select options
//        xColumnCombo.select("DataSpectralValue");
//        oracle.put("xAxisColumnNumber", 5);
//        xQuantityCombo.select("FREQUENCY");
//        oracle.put("xAxisQuantity", "FREQUENCY");
//        Assert.assertFalse(validation.getText().contains("Please enter a valid quantity for X values (current quantity: null)"));
//        xUnitsCombo.select("Hz");
//        oracle.put("xAxisUnit", "HERTZ");
//
//        yColumnCombo.select("DataFluxValue");
//        oracle.put("yAxisColumnNumber", 6);
//        yQuantityCombo.select("FLUXDENSITY");
//        oracle.put("yAxisQuantity", "FLUXDENSITY");
//        Assert.assertFalse(validation.getText().contains("Please enter a valid quantity for Y values (current quantity: null)"));
//        yUnitsCombo.select("Jy");
//        oracle.put("yAxisUnit", "FLUXDENSITYFREQ1");
//
//
//        RadioButton constantValue = configurationWindow.getRadioButton("constantValue");
//        constantValue.click();
//
//        Assert.assertTrue(validation.getText().contains("The ErrorType is ConstantValue but no value has been provided."));
//        Assert.assertTrue(validation.getText().contains("Invalid ConstantErrorValue"));
//
//        TextBox constantValueValue = configurationWindow.getInputTextBox("constantValueValue");
//
//        //Test wrong constant value
//        constantValueValue.setText("pippo");
//        Assert.assertTrue(validation.getText().contains("Invalid ConstantErrorValue"));
//
//        //Test correct constant value
//        constantValueValue.setText("2.0");
//        System.out.println(validation.getText());
//        Assert.assertTrue(validation.getText().isEmpty());
//
//        //Test symmetric column
//        configurationWindow.getRadioButton("symmetricColumn").click();
//        configurationWindow.getComboBox("symmetricColumnValue").select("DataFluxStatErr");
//        Assert.assertTrue(validation.getText().isEmpty());
//
//        //Test symmetric parameter
////        RadioButton symmetricParameter = configurationWindow.getRadioButton("symmetricParameter");
////        symmetricParameter.click();
////        configurationWindow.getComboBox("symmetricParameterValue").select("DataFluxStatErr");
////        Assert.assertTrue(validation.getText().isEmpty());
//
//        //Test configuration saving
//        URL outUrl = getClass().getResource("/test_data/");
//        final String outfile = outUrl.getFile() + "test.ini";
//
//        WindowInterceptor inter = WindowInterceptor.init(configurationWindow.getButton("Save").triggerClick());
//
//        inter.process(new WindowHandler() {
//
//            @Override
//            public Trigger process(Window window) throws Exception {
//                WindowInterceptor.init(window.getButton("Browse...").triggerClick()).process(FileChooserHandler.init().assertAcceptsFilesOnly().select(outfile)).run();
//
//
//                return window.getButton("Save").triggerClick();
//
//            }
//        }).run();
//
//        outUrl = getClass().getResource("/test_data/test.ini");
//        List<ISetup> confs = SetupManager.read(outUrl);
//
//        oracle.test(confs.get(0));
//
////        SedImporterApp.sampShutdown();
//
//        Thread.sleep(3000);

    }
}
