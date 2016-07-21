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

import cfa.vo.interop.SAMPFactory;
import cfa.vo.iris.fitting.custom.CustomModelsManager;
import cfa.vo.iris.sed.ExtSed;
import cfa.vo.iris.sed.stil.SegmentStarTable;
import cfa.vo.iris.test.unit.SherpaResource;
import cfa.vo.iris.test.unit.TestUtils;
import cfa.vo.iris.units.UnitsManager;
import cfa.vo.iris.visualizer.preferences.SedModel;
import cfa.vo.iris.visualizer.stil.tables.IrisStarTableAdapter;
import cfa.vo.sedlib.Segment;
import cfa.vo.sherpa.ConfidenceResults;
import cfa.vo.sherpa.Data;
import cfa.vo.sherpa.FitResults;
import cfa.vo.sherpa.SherpaClient;
import cfa.vo.sherpa.models.*;
import cfa.vo.sherpa.optimization.OptimizationMethod;
import cfa.vo.sherpa.stats.Statistic;
import cfa.vo.utils.Default;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mockito;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

public class FitControllerIT {
    
    private FitController controller;
    private SedModel sedModel;
    private double[] x = {1.0, 2.0, 3.0, 4.0, 5.0, 6.0};
    private double[] y = {1.0, 2.0, 3.0, 4.0, 5.0, 6.0};
    private double[] err = {1.0, 1.0, 1.0, 1.0, 1.0, 1.0};

    @Rule
    public SherpaResource sherpa = new SherpaResource();

    @Before
    public void setUp() throws Exception {
        ExtSed sed = new ExtSed("Test", false);
        Segment segment = TestUtils.createSampleSegment(x, y, SherpaClient.X_UNIT, SherpaClient.Y_UNIT);
        sed.addSegment(segment);
        FitConfiguration configuration = createFit();
        sed.setFit(configuration);
        CustomModelsManager modelsManager = Mockito.mock(CustomModelsManager.class);
        Data data = SAMPFactory.get(Data.class);
        data.setX(x);
        data.setY(y);
        data.setStaterror(err);
        SherpaClient client = sherpa.getClient();
        sedModel = new SedModel(sed, new IrisStarTableAdapter(null));
        controller = new FitController(sedModel, modelsManager, client);
    }

    @Test
    public void testFit() throws Exception {
        FitResults results = controller.fit();
        double[] expected = {0.0026159648660802892, 1.0011971292318782};
        assertArrayEquals(expected, results.getParvals(), 1E-15);
        
        ConfidenceResults conf = controller.computeConfidence();
        assertArrayEquals(new double[] {2.5042588276725897, 0.4012959241913576} , conf.getParmaxes(), 1E-15);
        assertArrayEquals(new double[] {-1.3568985622029128, -0.6881227423832292} , conf.getParmins(), 1E-15);
        assertArrayEquals(new double[] {0.0026159648660802892, 1.0011971292318782} , conf.getParvals(), 1E-15);
    }

    @Test
    public void testFitMasked() throws Exception {
        
        FitResults original = controller.fit();
        
        // Mask first and last rows
        sedModel.getDataTables().get(0).applyMasks(new int[] {0});
        FitResults masked = controller.fit();
        
        // One less point
        assertEquals((int) original.getNumpoints() - 1, (int) masked.getNumpoints());
    }

    @Test
    public void testEvaluate() throws Exception {
        Model model = controller.getFit().getModel().getParts().get(0);
        model.findParameter("c0").setVal(0.0);
        model.findParameter("c1").setVal(1.0);
        model.findParameter("c1").setFrozen(0);
        controller.evaluateModel(sedModel);
        SegmentStarTable data = sedModel.getDataTables().get(0).getPlotterDataTable();
        assertArrayEquals(x, data.getSpecValues(), 0.001);
        assertArrayEquals(y, data.getModelValues(), 0.001);

        double[] zeros = {0.0, 0.0, 0.0, 0.0, 0.0, 0.0};
        assertArrayEquals(zeros, data.getResidualValues(), 0.001);
        assertArrayEquals(zeros, data.getRatioValues(), 0.001);

        assertEquals(SherpaClient.X_UNIT, data.getSpecUnits().toString());
        assertEquals(SherpaClient.Y_UNIT, data.getFluxUnits().toString());
    }

    @Test
    public void testEvaluatePoorModel() throws Exception {
        Model model = controller.getFit().getModel().getParts().get(0);
        model.findParameter("c0").setVal(0.0);
        model.findParameter("c1").setVal(2.0); // force to have wrong value
        model.findParameter("c1").setFrozen(1);
        controller.evaluateModel(sedModel);
        SegmentStarTable data = sedModel.getDataTables().get(0).getPlotterDataTable();
        assertArrayEquals(x, data.getSpecValues(), 0.001);

        double[] y = {2.0, 4.0, 6.0, 8.0, 10.0, 12.0};
        assertArrayEquals(y, data.getModelValues(), 0.001);

        double[] expectedResiduals = {1.0, 2.0, 3.0, 4.0, 5.0, 6.0};
        double[] expectedRatios = {1.0, 1.0, 1.0, 1.0, 1.0, 1.0};
        assertArrayEquals(expectedResiduals, data.getResidualValues(), 0.001);
        assertArrayEquals(expectedRatios, data.getRatioValues(), 0.001);

        assertEquals(SherpaClient.X_UNIT, data.getSpecUnits().toString());
        assertEquals(SherpaClient.Y_UNIT, data.getFluxUnits().toString());
    }

    @Test
    public void testEvaluateUnitsConversionY() throws Exception {
        String localUnit = "Jy";
        sedModel.setUnits(SherpaClient.X_UNIT, localUnit);
        Model model = controller.getFit().getModel().getParts().get(0);
        model.findParameter("c0").setVal(0.0);
        model.findParameter("c1").setVal(1.0);
        model.findParameter("c1").setFrozen(0);
        controller.evaluateModel(sedModel);
        SegmentStarTable data = sedModel.getDataTables().get(0).getPlotterDataTable();
        assertArrayEquals(x, data.getSpecValues(), 0.001);

        UnitsManager uManager = Default.getInstance().getUnitsManager();
        double[] yConverted = uManager.convertY(y, x, SherpaClient.Y_UNIT, SherpaClient.X_UNIT, localUnit);
        assertArrayEquals(yConverted, data.getModelValues(), 0.001);

        double[] zeros = {0.0, 0.0, 0.0, 0.0, 0.0, 0.0};
        assertArrayEquals(zeros, data.getResidualValues(), 0.001);
        assertArrayEquals(zeros, data.getRatioValues(), 0.001);

        assertEquals(SherpaClient.X_UNIT, data.getSpecUnits().toString());
        assertEquals(localUnit, data.getFluxUnits().toString());
    }

    @Test
    public void testEvaluateUnitsConversionXY() throws Exception {
        String xUnit = "Hz";
        String yUnit = "Jy";
        sedModel.setUnits(xUnit, yUnit);
        Model model = controller.getFit().getModel().getParts().get(0);
        model.findParameter("c0").setVal(0.0);
        model.findParameter("c1").setVal(1.0);
        model.findParameter("c1").setFrozen(0);
        controller.evaluateModel(sedModel);
        SegmentStarTable data = sedModel.getDataTables().get(0).getPlotterDataTable();

        UnitsManager uManager = Default.getInstance().getUnitsManager();
        double[] xConverted = uManager.convertX(x, SherpaClient.X_UNIT, xUnit);
        double[] yConverted = uManager.convertY(y, x, SherpaClient.Y_UNIT, SherpaClient.X_UNIT, yUnit);
        assertArrayEquals(xConverted, data.getSpecValues(), 0.001);
        assertArrayEquals(yConverted, data.getModelValues(), 0.001);

        double[] zeros = new double[]{0.0, 0.0, 0.0, 0.0, 0.0, 0.0};
        assertArrayEquals(zeros, data.getResidualValues(), 0.001);
        assertArrayEquals(zeros, data.getRatioValues(), 0.001);

        assertEquals(yUnit, data.getFluxUnits().toString());
        assertEquals(xUnit, data.getSpecUnits().toString());
    }

    private FitConfiguration createFit() throws Exception {
        FitConfiguration fit = new FitConfiguration();

        ModelFactory factory = new ModelFactory();
        Model m = factory.getModel("polynomial", "m1");
        Parameter c0 = m.findParameter("c0");
        c0.setFrozen(0);

        Parameter c1 = m.findParameter("c1");
        c1.setFrozen(0);

        CompositeModel cm = fit.getModel();
        cm.addPart(m);
        cm.setName("m1");

        fit.setModel(cm);

        fit.setMethod(OptimizationMethod.LevenbergMarquardt);
        fit.setStat(Statistic.CStat);

        return fit;
    }
}