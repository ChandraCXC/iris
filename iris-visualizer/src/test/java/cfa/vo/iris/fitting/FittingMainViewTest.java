/**
 * Copyright (C) 2016 Smithsonian Astrophysical Observatory
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
package cfa.vo.iris.fitting;

import cfa.vo.iris.fitting.custom.CustomModelsManager;
import cfa.vo.iris.sed.ExtSed;
import cfa.vo.iris.sed.SedlibSedManager;
import cfa.vo.iris.sed.quantities.XUnit;
import cfa.vo.iris.test.unit.ApplicationStub;
import cfa.vo.iris.test.unit.TestUtils.SingleThreadExecutor;
import cfa.vo.iris.units.spv.XUnits;
import cfa.vo.iris.visualizer.preferences.SedModel;
import cfa.vo.iris.visualizer.preferences.VisualizerComponentPreferences;
import cfa.vo.iris.visualizer.stil.tables.IrisStarTableAdapter;
import cfa.vo.sherpa.SherpaClient;
import cfa.vo.sherpa.optimization.OptimizationMethod;
import cfa.vo.sherpa.stats.Statistic;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.uispec4j.ComboBox;
import org.uispec4j.TextBox;
import org.uispec4j.Window;
import org.uispec4j.assertion.UISpecAssert;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import static org.junit.Assert.assertEquals;

public class FittingMainViewTest {
    private SedlibSedManager manager;
    private Window fittingView;
    private TextBox sedId;
    private ComboBox optimizationCombo;
    private ComboBox statCombo;
    private FitController controller;

    @Before
    public void setUp() throws Exception {
        manager = new SedlibSedManager();
        ExtSed sed = manager.newSed("Sed0");
        CustomModelsManager modelsManager = Mockito.mock(CustomModelsManager.class);
        Mockito.when(modelsManager.getCustomModels()).thenReturn(new DefaultMutableTreeNode("Custom Models"));
        SherpaClient client = Mockito.mock(SherpaClient.class);
        JFileChooser chooser = Mockito.mock(JFileChooser.class);
        SedModel sedModel = new SedModel(sed, new IrisStarTableAdapter(null));
        controller = new FitController(sedModel, modelsManager, client);
        ApplicationStub app = new ApplicationStub();
        VisualizerComponentPreferences preferences = new VisualizerComponentPreferences(app.getWorkspace(), new SingleThreadExecutor());
        FittingMainView view = new FittingMainView(preferences, chooser, controller);

        fittingView = new Window(view);
        sedId = fittingView.getInputTextBox("currentSedField");
        optimizationCombo = fittingView.getComboBox("optimizationCombo");
        statCombo = fittingView.getComboBox("statisticCombo");

        optimizationCombo.selectionEquals("LevenbergMarquardt").check();
        statCombo.selectionEquals("Chi2").check();
    }

    @Test
    public void testSedId() throws Exception {
        sedId.textEquals("Sed0").check();
    }

    @Test
    public void testSwitchSed() throws Exception {
        manager.newSed("NewSed");
        UISpecAssert.waitUntil(sedId.textEquals("NewSed"), 1000);
    }

    @Test
    public void testMethodAndStatRead() throws Exception {
        ExtSed sed = manager.getSelected();
        sed.getFit().setMethod(OptimizationMethod.MonteCarlo);
        sed.getFit().setStat(Statistic.LeastSquares);
        optimizationCombo.selectionEquals("MonteCarlo").check();
        statCombo.selectionEquals("LeastSquares").check();
    }

    @Test
    public void testMethodAndStatWrite() throws Exception {
        optimizationCombo.select("NelderMeadSimplex");
        statCombo.select("Cash");
        ExtSed sed = manager.getSelected();
        Assert.assertEquals(sed.getFit().getMethod(), OptimizationMethod.NelderMeadSimplex);
        Assert.assertEquals(sed.getFit().getStat(), Statistic.Cash);
    }
}
