/*
 * Copyright 2016 Chandra X-Ray Observatory.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package cfa.vo.iris.fitting;

import cfa.vo.iris.IrisComponent;
import cfa.vo.iris.sed.SedlibSedManager;
import cfa.vo.iris.sed.quantities.XUnit;
import cfa.vo.iris.test.unit.AbstractComponentGUITest;
import cfa.vo.iris.test.unit.TestUtils;
import cfa.vo.iris.visualizer.FittingToolComponent;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
import org.uispec4j.Window;
import org.uispec4j.interception.WindowInterceptor;

/**
 *
 * @author jbudynk
 */
public class FittingRangesFrameTest extends AbstractComponentGUITest {
    
    private FittingToolComponent comp = new FittingToolComponent();
    private Window fittingTool;
    private SedlibSedManager sedManager;
    
    @Before
    public void setUp() throws Exception {
        
        // add a SED
        sedManager = (SedlibSedManager) app.getWorkspace().getSedManager();
        sedManager.newSed("sed");
        
        // wait for the SED to be added to the EDT
        TestUtils.invokeWithRetry(20, 100, new Runnable() {

            @Override
            public void run() {
                assertEquals(1, sedManager.getSeds().size());
            }

        });

        String windowName = "Fitting Tool";
        
        // open fitting tool
        window.getMenuBar()
            .getMenu("Tools")
            .getSubMenu(windowName)
            .getSubMenu(windowName)
            .click();
        
        assertTrue(desktop.containsWindow(windowName).isTrue());
        
        // get the fitting tool window
        fittingTool = desktop.getWindow(windowName);
        
    }

    @Test
    public void testFittingRangesManager() throws Exception {
        
        // add a range to the Fit Config
        FittingRange range1 = new FittingRange(1.0, 3.0, XUnit.ANGSTROM);
        FittingRange range2 = new FittingRange(4.0, 5.0, XUnit.ANGSTROM);
        FittingRange range3 = new FittingRange(.5, .6, XUnit.NM);
        FitConfiguration fitConfig = sedManager.getSelected().getFit();
        fitConfig.addFittingRange(range1);
        fitConfig.addFittingRange(range2);
        fitConfig.addFittingRange(range3);
        
        // open FittingRangesManager
        fittingTool.getButton("Add Ranges...").click();
        
        // assert fitting ranges manager appears
        desktop.containsWindow("Fitting Ranges Manager").check();
        
        Window rangesWindow = desktop.getWindow("Fitting Ranges Manager");
        
        // assert the table already has a range in it
        assertEquals(3, rangesWindow.getTable().getRowCount());
        
        // check values
        assertEquals("1.0", rangesWindow.getTable().getContentAt(0, 0));
        assertEquals("3.0", rangesWindow.getTable().getContentAt(0, 1));
        assertEquals("Angstrom", rangesWindow.getTable().getContentAt(0, 2));
        assertEquals("5.0", rangesWindow.getTable().getContentAt(2, 0));
        assertEquals("6.0", rangesWindow.getTable().getContentAt(2, 1));
        assertEquals("Angstrom", rangesWindow.getTable().getContentAt(0, 2));
        
        // add a specific range
        rangesWindow.getTextBox("x1TextBox").setText("10.0");
        rangesWindow.getTextBox("x2TextBox").setText("1.0");
        rangesWindow.getComboBox("xUnitComboBox").select(XUnit.NM.getString());
        rangesWindow.getPanel("addRangePanel").getButton("Add").click();
        
        // assert it's added to the table AND fit config
        assertEquals(4, rangesWindow.getTable().getRowCount());
        assertEquals(4, fitConfig.getFittingRanges().size());
        
        // assert start/end position are ordered correctly
        assertEquals("10.0", rangesWindow.getTable().getContentAt(3, 0));
        assertEquals("100.0", rangesWindow.getTable().getContentAt(3, 1));
        
        // remove selected rows
        rangesWindow.getTable().selectRows(new int[]{0, 2});
        rangesWindow.getTable().rowsAreSelected(new int[]{0, 2}).check();
        rangesWindow.getButton("Remove").click();
        assertEquals(2, rangesWindow.getTable().getRowCount());
        assertEquals("4.0", rangesWindow.getTable().getContentAt(0, 0));
        assertEquals("5.0", rangesWindow.getTable().getContentAt(0, 1));
        assertEquals("10.0", rangesWindow.getTable().getContentAt(1, 0));
        assertEquals("100.0", rangesWindow.getTable().getContentAt(1, 1));
        
        // clear all ranges
        rangesWindow.getButton("Clear all").click();
        assertEquals(0, rangesWindow.getTable().getRowCount());
        
        // add some bad fit ranges
        
        // non-numeric
        rangesWindow.getTextBox("x1TextBox").setText("1.0");
        rangesWindow.getTextBox("x2TextBox").setText("asdt3");
        rangesWindow.getPanel("addRangePanel").getButton("Add").click();
        
        WindowInterceptor
                .init(rangesWindow.getPanel("addRangePanel").getButton("Add").triggerClick())
                .processWithButtonClick("ERROR","OK")
                .run();
        
        // empty box
        rangesWindow.getTextBox("x1TextBox").clear();
        rangesWindow.getTextBox("x2TextBox").setText("1.0");
        rangesWindow.getPanel("addRangePanel").getButton("Add").click();
        
        WindowInterceptor
                .init(rangesWindow.getPanel("addRangePanel").getButton("Add").triggerClick())
                .processWithButtonClick("ERROR","OK")
                .run();
        
        // make sure the table is still empty
        assertEquals(0, rangesWindow.getTable().getRowCount());
        
        // scientific notation allowed
        rangesWindow.getTextBox("x1TextBox").setText("1e-4");
        rangesWindow.getTextBox("x2TextBox").setText("1E7");
        rangesWindow.getComboBox("xUnitComboBox").select(XUnit.ANGSTROM.getString());
        rangesWindow.getPanel("addRangePanel").getButton("Add").click();
        
        assertEquals(1, rangesWindow.getTable().getRowCount());
        assertEquals("1.0E-4", rangesWindow.getTable().getContentAt(0, 0));
        assertEquals("1.0E7", rangesWindow.getTable().getContentAt(0, 1));
        
        // negatives allowed
        rangesWindow.getTextBox("x1TextBox").setText("1");
        rangesWindow.getTextBox("x2TextBox").setText("-1");
        rangesWindow.getComboBox("xUnitComboBox").select(XUnit.ANGSTROM.getString());
        rangesWindow.getPanel("addRangePanel").getButton("Add").click();
        
        assertEquals(2, rangesWindow.getTable().getRowCount());
        assertEquals("-1.0", rangesWindow.getTable().getContentAt(1, 0));
        assertEquals("1.0", rangesWindow.getTable().getContentAt(1, 1));
        
    }

    @Override
    protected IrisComponent getComponent() {
        return this.comp;
    }
    
}
