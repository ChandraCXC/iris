/**
 * Copyright (C) 2015, 2016 Smithsonian Astrophysical Observatory
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
package cfa.vo.iris.visualizer;

import cfa.vo.interop.SAMPFactory;
import cfa.vo.iris.test.unit.AbstractComponentGUITest;
import cfa.vo.iris.IrisComponent;
import cfa.vo.iris.fitting.FitConfiguration;
import cfa.vo.iris.sed.ExtSed;
import cfa.vo.iris.sed.SedlibSedManager;
import cfa.vo.iris.test.unit.TestUtils;
import cfa.vo.sherpa.models.*;

import java.io.File;
import java.nio.charset.Charset;
import java.util.ArrayList;
import cfa.vo.sherpa.optimization.OptimizationMethod;
import cfa.vo.sherpa.stats.Statistic;
import com.google.common.io.Files;
import net.javacrumbs.jsonunit.JsonAssert;
import org.junit.*;

import static org.junit.Assert.*;

import org.junit.rules.TemporaryFolder;
import org.uispec4j.*;
import org.uispec4j.assertion.UISpecAssert;
import org.uispec4j.interception.BasicHandler;
import org.uispec4j.interception.FileChooserHandler;
import org.uispec4j.interception.WindowHandler;
import org.uispec4j.interception.WindowInterceptor;

public class FittingToolComponentTest extends AbstractComponentGUITest {

    private FittingToolComponent comp = new FittingToolComponent();
    private String windowName;
    private SedlibSedManager sedManager;

    @Rule
    public TemporaryFolder tempFolder = new TemporaryFolder();

    @Before
    public void setUp() throws Exception {
        windowName = "Fitting Tool";
        sedManager = (SedlibSedManager) app.getWorkspace().getSedManager();
    }
    
    @After
    public void tearDown() throws Exception {
        if (sedManager.getSeds() != null) {
            for (ExtSed sed : new ArrayList<>(sedManager.getSeds())) {
                sedManager.remove(sed.getId());
            }
        }
    }

    @Override
    protected IrisComponent getComponent() {
        return comp;
    }

    @Test
    public void testFittingNoSed() throws Exception {
        WindowInterceptor wi = WindowInterceptor.init(
            window.getMenuBar()
                .getMenu("Tools")
                .getSubMenu(comp.getName())
                .getSubMenu(comp.getName())
                .triggerClick()
        );
        
        wi.process(BasicHandler.init().triggerButtonClick("OK")).run();
    }
    
    @Test
    public void testFittingEmptySed() throws Exception {
        final Window mainFit = setupFitWindow(sedManager.newSed("TestSed"));

        TextBox parName = mainFit.getTextBox("Par Name");
        parName.textEquals("No Parameter Selected").check();
        assertEquals("No Model", mainFit.getTextBox("modelExpressionField").getText());
    }

    @Test
    public void testSedNameChange() throws Exception {
        final Window mainFit = setupFitWindow(sedManager.newSed("TestSed"));
        assertEquals("TestSed", mainFit.getTextBox("currentSedField").getText());
        sedManager.newSed("TestSed2");
        TestUtils.invokeWithRetry(50, 100, new Runnable(){
            @Override
            public void run() {
                assertEquals("TestSed2", mainFit.getTextBox("currentSedField").getText());
            }
        });
    }

    @Test
    public void testViewNonEmptySed() throws Exception {
        ExtSed sed = sedManager.newSed("TestSed");
        addFit(sed);
        final Window mainFit = setupFitWindow(sed);

        TestUtils.invokeWithRetry(50, 100, new Runnable(){
            @Override
            public void run() {
                assertEquals("m", mainFit.getTextBox("modelExpressionField").getText());
            }
        });

        Tree modelsTree = mainFit.getTree("modelsTree");
        modelsTree.click("polynomial.m/m.c0");

        TestUtils.invokeWithRetry(50, 100, new Runnable(){
            @Override
            public void run() {
                assertEquals("1.0", mainFit.getInputTextBox("Par Val").getText());
            }
        });

        final ComboBox statisticCombo = mainFit.getComboBox("statisticCombo");
        final ComboBox optimizationCombo = mainFit.getComboBox("optimizationCombo");
        TestUtils.invokeWithRetry(50, 100, new Runnable(){
            @Override
            public void run() {
                assertTrue(statisticCombo.selectionEquals("LeastSquares").isTrue());
                assertTrue(optimizationCombo.selectionEquals("LevenbergMarquardt").isTrue());
            }
        });

    }

    @Test
    public void testModelSelection() throws Exception {
        final Window mainFit = setupFitWindow(sedManager.newSed("TestSed"));

        Tree availableTree = mainFit.getTree("availableTree");
        final TextBox descriptionArea = mainFit.getTextBox("descriptionArea");
        availableTree.click("Preset Model Components/absorptionedge");

        TestUtils.invokeWithRetry(50, 100, new Runnable(){
            @Override
            public void run() {
                assertEquals("Optical model of interstellar absorption", descriptionArea.getText());
            }
        });
    }

    @Test
    public void testModelSearch() throws Exception {
        final Window mainFit = setupFitWindow(sedManager.newSed("TestSed"));

        TextBox searchField = mainFit.getInputTextBox("searchField");
        searchField.setText("power");
        mainFit.getButton("searchButton").click();

        Tree availableTree = mainFit.getTree("availableTree");
        assertEquals(4, availableTree.getChildCount("Preset Model Components"));
        assertTrue(availableTree.contains("Preset Model Components/beta1d").isTrue());
        assertTrue(availableTree.contains("Preset Model Components/powerlaw").isTrue());
        assertTrue(availableTree.contains("Preset Model Components/brokenpowerlaw").isTrue());
    }

    @Test
    public void testSaveText() throws Exception {
        ExtSed sed = sedManager.newSed("TestSed");
        addFit(sed);
        final Window mainFit = setupFitWindow(sed);

        testSaveFit("Save Text...", "fit.output", true, mainFit);
    }

    @Test
    public void testLoadJson() throws Exception {
        ExtSed sed = sedManager.newSed("TestSed");
        final Window mainFit = setupFitWindow(sed);

        String path = getClass().getResource("fit.json").getFile();

        WindowInterceptor
                .init(mainFit.getMenuBar().getMenu("File").getSubMenu("Load Json...").triggerClick())
                .process(FileChooserHandler.init().select(path))
                .run()
        ;

        FitConfiguration configuration = createFit();
        configuration.getModel().getParts().get(0).findParameter("c1").setVal(3.25);
        configuration.setMethod(OptimizationMethod.MonteCarlo);
        configuration.setStat(Statistic.Chi2);

        assertEquals(configuration, sed.getFit());

        Tree modelsTree = mainFit.getTree("modelsTree");
        UISpecAssert.waitUntil(modelsTree.contains("polynomial/m.c1"), 1000);
        modelsTree.click("polynomial.m/m.c1");
        TextBox parVal = mainFit.getInputTextBox("Par Val");
        UISpecAssert.waitUntil(parVal.textEquals("3.25"), 1000);
        mainFit.getComboBox("statisticCombo").selectionEquals("Chi2").check();
        mainFit.getComboBox("optimizationCombo").selectionEquals("MonteCarlo").check();
    }

    @Test
    public void testSaveJson() throws Exception {
        ExtSed sed = sedManager.newSed("TestSed");
        addFit(sed);
        final Window mainFit = setupFitWindow(sed);

        Tree modelsTree = mainFit.getTree("modelsTree");
        modelsTree.click("polynomial.m/m.c1");
        TextBox parVal = mainFit.getInputTextBox("Par Val");
        UISpecAssert.waitUntil(parVal.textEquals("0.0"), 1000);
        parVal.setText("3.25");
        mainFit.getComboBox("statisticCombo").select("Chi2");
        mainFit.getComboBox("optimizationCombo").select("MonteCarlo");

        String outputFile = tempFolder.getRoot().getAbsolutePath()+"output.json";

        testSaveFit("Save Json...", "fit.json", false, mainFit);
    }
    
    private void testSaveFit(String menuItemText, String controlFile, boolean text, Window mainFit) throws Exception { 

        String outputFile = tempFolder.getRoot().getAbsolutePath()+controlFile;
        
        WindowInterceptor
                .init(mainFit.getMenuBar().getMenu("File").getSubMenu(menuItemText).triggerClick())
                .process(FileChooserHandler.init()
                        .assertIsSaveDialog()
                        .select(outputFile))
                .run()
        ;
        
        String expected = TestUtils.readFile(getClass(), controlFile);
        checkOutput(text, expected, outputFile); // test
        
        // update the model and try to save the results to the same filename
        mainFit.getComboBox("optimizationCombo").select("NelderMeadSimplex");
        
        WindowInterceptor interceptor = WindowInterceptor
			.init(mainFit.getMenuBar().getMenu("File").getSubMenu(menuItemText).triggerClick());
        
        interceptor.process(FileChooserHandler.init()
                        .assertIsSaveDialog()
                        .select(outputFile))
                .process(new WindowHandler() {
                    public Trigger process(Window window) throws Exception {
                        // Yes, overwrite the file
                        return window.getButton("Yes").triggerClick();
                    }
                })
                .process(new WindowHandler() {
                    public Trigger process(Window window) throws Exception {
                        // file successfully saved message
                        return window.getButton("OK").triggerClick();
                    }
                })
                .run();
        
        // check the file updated
        if (text) {
            expected = expected.replace("LevenbergMarquardt", "NelderMeadSimplex");
        } else {
            expected = expected.replace("MonteCarlo", "NelderMeadSimplex");
        }
        checkOutput(text, expected, outputFile); // test
        
        // update the model, use the same filename, but choose not to override it.
        mainFit.getComboBox("optimizationCombo").select("MonteCarlo");
        
        interceptor = WindowInterceptor
			.init(mainFit.getMenuBar().getMenu("File").getSubMenu(menuItemText).triggerClick());
        
        interceptor.process(FileChooserHandler.init()
                        .assertIsSaveDialog()
                        .select(outputFile))
                .process(new WindowHandler() {
                    public Trigger process(Window window) throws Exception {
                        // No, do not overwrite the file
                        return window.getButton("No").triggerClick();
                    }
                })
                .run();
        
        // check that the file stayed the same as before.
        checkOutput(text, expected, outputFile); // test
    }
    
    private void checkOutput(boolean isTextFile, String expected, String outputFile) throws Exception {
        if (isTextFile) {
            assertEquals(expected, Files.toString(new File(outputFile), Charset.defaultCharset()));
        } else {
            JsonAssert.assertJsonEquals(expected, Files.toString(new File(outputFile), Charset.defaultCharset()));
        }
    }

    @Test
    public void testSaveTextNonExistentFile() throws Exception {
        nonExistentFile("Save Text...");
    }

    @Test
    public void testSaveJsonNonExistentFile() throws Exception {
        nonExistentFile("Save Json...");
    }

    @Test
    public void testLoadJsonNonExistentFile() throws Exception {
        nonExistentFile("Load Json...");
    }

    @Ignore("failing on travis/jdk8")
    @Test
    public void testSetFittingRangesNoPlotter() throws Exception {
        // check that a warning is shown if the user adds a fitting range
        // when the visualizer isn't open / visible
        final Window mainFit = setupFitWindow(sedManager.newSed("TestSed"));
        
        mainFit.getButton("Add Ranges...").click();
        
        final Window rangesWindow = desktop.getWindow("Fitting Ranges Manager");
        
        TestUtils.invokeWithRetry(50, 100, new Runnable() {
            
            @Override
            public void run() {
                WindowInterceptor wi = WindowInterceptor.init(
                        rangesWindow.getButton("Add from plot").triggerClick());
                String message = "The Visualizer must be open before selecting a fitting range.";
                wi.process(BasicHandler.init()
                        .assertContainsText(message)
                        .triggerButtonClick("OK"))
                        .run();
            }
        });
    }
    
    private Window setupFitWindow(final ExtSed sed) throws Exception {
        // Wait for swing EDT to update datastore with the SED
        TestUtils.invokeWithRetry(10, 100, new Runnable() {
            @Override
            public void run() {
                assertNotNull(comp.preferences.getDataStore().getSedModel(sed));
            }
        });
        
        return openWindow();
    }

    private Window openWindow() throws Exception {
        TestUtils.invokeWithRetry(50, 100, new Runnable() {
            @Override
            public void run() {
                window.getMenuBar()
                        .getMenu("Tools")
                        .getSubMenu(comp.getName())
                        .getSubMenu(comp.getName())
                        .click();
            }
        });
        return desktop.getWindow(windowName);
    }

    private void nonExistentFile(String menu) throws Exception {
        ExtSed sed = sedManager.newSed("TestSed");
        addFit(sed);
        Window mainFit = openWindow();

        final WindowInterceptor wi = WindowInterceptor
                .init(mainFit.getMenuBar().getMenu("File").getSubMenu(menu).triggerClick())
                .process(FileChooserHandler.init().select("/foo/bar/baz"));

        WindowInterceptor
                .init(new Trigger() {
                    @Override
                    public void run() throws Exception {
                        wi.run();
                    }
                })
                .process(new WindowHandler() {
                    @Override
                    public Trigger process(Window window) throws Exception {
                        window.titleEquals("Error").check();
                        window.getTextBox("Optionpane.label").textContains("No such file or directory").check();
                        return window.getButton().triggerClick();
                    }
                })
                .run();
    }

    private FitConfiguration addFit(ExtSed sed) {
        FitConfiguration fit = createFit();
        sed.setFit(fit);
        return fit;
    }

    private FitConfiguration createFit() {
        FitConfiguration fit = new FitConfiguration();

        ModelFactory factory = new ModelFactory();
        Model m = factory.getModel("polynomial", "m");
        Parameter c0 = m.findParameter("c0");
        c0.setFrozen(0);

        Parameter c1 = m.findParameter("c1");
        c1.setFrozen(0);

        CompositeModel cm = SAMPFactory.get(CompositeModel.class);
        cm.setName("m");
        cm.addPart(m);
        fit.setModel(cm);

        fit.setMethod(OptimizationMethod.LevenbergMarquardt);
        fit.setStat(Statistic.LeastSquares);

        return fit;
    }
}
