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
import cfa.vo.iris.fitting.custom.DefaultCustomModel;
import cfa.vo.iris.sed.ExtSed;
import cfa.vo.iris.sed.quantities.SPVYUnit;
import cfa.vo.iris.sed.quantities.XUnit;
import cfa.vo.iris.sed.stil.SegmentStarTable;
import cfa.vo.iris.test.unit.TestUtils;
import cfa.vo.iris.units.spv.XUnits;
import cfa.vo.iris.visualizer.preferences.SedModel;
import cfa.vo.iris.visualizer.stil.tables.IrisStarTable;
import cfa.vo.iris.visualizer.stil.tables.IrisStarTableAdapter;
import cfa.vo.sedlib.common.Utypes;
import cfa.vo.sherpa.ConfidenceResults;
import cfa.vo.sherpa.Data;
import cfa.vo.sherpa.FitResults;
import cfa.vo.sherpa.SherpaClient;
import cfa.vo.sherpa.models.*;
import cfa.vo.sherpa.optimization.OptimizationMethod;
import cfa.vo.sherpa.stats.Statistic;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import net.javacrumbs.jsonunit.JsonAssert;

import static cfa.vo.iris.test.unit.TestUtils.createSampleSegment;
import static org.junit.Assert.*;

public class FitControllerTest {
    private FitController controller;
    private FitConfiguration configuration;
    private ByteArrayOutputStream os = new ByteArrayOutputStream();
    private SherpaClient mockClient;
    private double[] x = {1.0, 1.1, 1.2};
    private double[] y = {2.0, 2.1, 2.2};
    private double[] err = {1.0, 1.0, 1.0};

    @Before
    public void setUp() throws Exception {
        ExtSed sed = Mockito.mock(ExtSed.class);
        configuration = createFit();
        Mockito.stub(sed.getFit()).toReturn(configuration);
        Mockito.stub(sed.toString()).toReturn("MySed (Segments: 3)");
        CustomModelsManager modelsManager = Mockito.mock(CustomModelsManager.class);
        mockClient = Mockito.mock(SherpaClient.class);
        Data data = SAMPFactory.get(Data.class);
        data.setX(x);
        data.setY(y);
        data.setStaterror(err);
        Mockito.stub(mockClient.evaluate(Mockito.any(double[].class), Mockito.any(FitConfiguration.class))).toReturn(y);
        SedModel sedModel = new SedModel(sed, new IrisStarTableAdapter(null));
        controller = new FitController(sedModel, modelsManager, mockClient);
    }

    @Test
    public void testSave() throws Exception {
        controller.save(os);
        assertEquals(TestUtils.readFile(getClass(), "fit.output"), os.toString("UTF-8"));
    }

    @Test
    public void testSaveAsJson() throws Exception {
        controller.saveJson(os);
        JsonAssert.assertJsonEquals(TestUtils.readFile(getClass(), "fit.json"), os.toString("UTF-8"));
    }

    @Test
    public void testLoadJson() throws Exception {
        FitConfiguration actual = controller.loadJson(getClass().getResource("fit.json").openStream());
        assertEquals(actual, configuration);
        assertEquals(actual, controller.getFit());
    }

    @Test
    public void testRoundTrip() throws Exception {
        controller.saveJson(os);
        InputStream is = new ByteArrayInputStream(os.toString("UTF-8").getBytes("UTF-8"));
        FitConfiguration actual = controller.loadJson(is);
        assertEquals(actual, controller.getFit());
    }

    @Test
    public void testEvaluate() throws Exception {
        ExtSed sed = ExtSed.makeSed("test", false, x, y, SherpaClient.X_UNIT, SherpaClient.Y_UNIT);
        SedModel model = new SedModel(sed, new IrisStarTableAdapter(null));
        controller.evaluateModel(model);
        SegmentStarTable data = model.getDataTables().get(0).getPlotterDataTable();
        assertArrayEquals(x, data.getSpecValues(), 0.001);
        assertArrayEquals(y, data.getModelValues(), 0.001);

        double[] zeros = new double[]{0.0, 0.0, 0.0};
        assertArrayEquals(zeros, data.getResidualValues(), 0.001);
        assertArrayEquals(zeros, data.getRatioValues(), 0.001);
        assertEquals(SherpaClient.X_UNIT, data.getSpecUnits().toString());
        assertEquals(SherpaClient.Y_UNIT, data.getFluxUnits().toString());
    }
    
    @Test
    public void testFitMaskedSeds() throws Exception {
        
        // Expectations
        FitResults mockResults = Mockito.mock(FitResults.class);
        Mockito.stub(mockResults.getParvals()).toReturn(new double[] {});
        Mockito.stub(mockResults.getParvals()).toReturn(new double[] {});
        Mockito.stub(mockClient.fit(Mockito.any(Data.class), Mockito.any(FitConfiguration.class))).toReturn(mockResults);
        
        final ExtSed sed = new ExtSed("sed", false);
        sed.addSegment(createSampleSegment(new double[] {1,2,3}, new double[] {100,200,300},
                "nm", "Jy"));
        sed.setFit(configuration);
        
        SedModel model = new SedModel(sed, new IrisStarTableAdapter(null));
        controller.setSedModel(model);
        
        // mask first row in data table
        model.getDataTables().get(0).applyMasks(new int[] {0});
        
        // These are here to make sure array lengths are kosher throughout the wiring in the 
        // controller.
        // Fit the model
        controller.fit();
        
        // Evaluate model
        controller.evaluateModel(model);
        
        // SedModel should be evaluated at ALL points
        controller.evaluateModel(model);
        
        IrisStarTable segment = model.getDataTables().get(0);
        double[] modelVal = segment.getPlotterDataTable().getModelValues();
        double[] ratVal = segment.getPlotterDataTable().getRatioValues();
        double[] resVal = segment.getPlotterDataTable().getResidualValues();
        
        assertEquals(3, modelVal.length);
        assertEquals(3, ratVal.length);
        assertEquals(3, resVal.length);
    }
    
    @Test
    public void testConstructSherpaClientDataCall() throws Exception {
        
        final ExtSed sed = new ExtSed("sed", false);
        sed.addSegment(createSampleSegment(new double[] {1,2,3}, new double[] {10,20,30},
                XUnit.EV.getString(), SPVYUnit.ABMAG.getString()));
        sed.addSegment(createSampleSegment(new double[] {30}, new double[] {3},
                XUnit.EV.getString(), SPVYUnit.ABMAG.getString()));
        
        // Add errors to first segment
        sed.getSegment(0).getData().setDataValues(new double[] {.001,.002,.003}, Utypes.SEG_DATA_FLUXAXIS_ACC_STATERR);
        sed.setFit(configuration);
        
        // We used ExtSed.flatten to get expected values
        ExtSed flatSed = ExtSed.flatten(sed, SherpaClient.X_UNIT, SherpaClient.Y_UNIT);
        double[] expectedX = flatSed.getSegment(0).getSpectralAxisValues();
        double[] expectedY = flatSed.getSegment(0).getFluxAxisValues();
        double[] expectedErrs = (double[]) flatSed.getSegment(0).getData().getDataValues(Utypes.SEG_DATA_FLUXAXIS_ACC_STATERR);
        
        SedModel model = new SedModel(sed, new IrisStarTableAdapter(null));
        controller.setSedModel(model);
        
        // Get data
        Data allData = controller.constructSherpaCall(model);
        
        // Should have converted units
        assertArrayEquals(expectedX, allData.getX(), 1E-15);
        assertArrayEquals(expectedY, allData.getY(), 1E-15);
        assertArrayEquals(expectedErrs, allData.getStaterror(), 1E-15);
        
        // mask first row in data table
        model.getDataTables().get(0).applyMasks(new int[] {0});
        Data maskedData = controller.constructSherpaCall(model);
        
        // Should have converted units AND masked first row
        assertArrayEquals(Arrays.copyOfRange(expectedX, 1, 4), maskedData.getX(), 1E-15);
        assertArrayEquals(Arrays.copyOfRange(expectedY, 1, 4), maskedData.getY(), 1E-15);
        assertArrayEquals(Arrays.copyOfRange(expectedErrs, 1, 4), maskedData.getStaterror(), 1E-15);
    }

    @Test
    public void testSetFittingRanges() throws Exception {
        ExtSed sed = ExtSed.makeSed("test", false, x, y, "nm", SherpaClient.Y_UNIT);
        SedModel model = new SedModel(sed, new IrisStarTableAdapter(null));
        FitConfiguration config = model.getFit();
        
        // set fit ranges
        FittingRange range = new FittingRange(1.05, 1.3, XUnit.NM);
        config.addFittingRange(range);
        controller.evaluateModel(model);
        
        // first check ranges are correct. Original XUnits were nm; they should 
        // be in Angstroms now
        List<FittingRange> ranges = model.getFit().getFittingRanges();
        assertEquals(10.5, ranges.get(0).getStartPoint(), 0.00001);
        assertEquals(13.0, ranges.get(0).getEndPoint(), 0.00001);
        
        // add new range, with end point and start point switched.
        // check that the ranges get sorted correctly.
        range = new FittingRange(1.3, 1.05, XUnit.NM);
        config.addFittingRange(range);
        
        // there should be two fitting ranges now
        assertEquals(2, ranges.size());
        
        ranges = model.getFit().getFittingRanges();
        assertEquals(10.5, ranges.get(1).getStartPoint(), 0.00001);
        assertEquals(13.0, ranges.get(1).getEndPoint(), 0.00001);
        
        // add another fitting range, in energy units
        range = new FittingRange(1.5, 2.0, XUnit.KEV);
        config.addFittingRange(range);
        
        ranges = model.getFit().getFittingRanges();
        assertEquals(6.1992, ranges.get(2).getStartPoint(), 0.001);
        assertEquals(8.2656, ranges.get(2).getEndPoint(), 0.001);
        
        // make sure fitting ranges aren't overwriting each other
        assertEquals(10.5, ranges.get(0).getStartPoint(), 0.00001);
        assertEquals(13.0, ranges.get(0).getEndPoint(), 0.00001);
        
        // check that the number of data points used in the fit is 2
        controller.setSedModel(model);
        Data allData = controller.constructSherpaCall(model);
        assertEquals(2, allData.getX().length);
        
        // remove one of the ranges
        config.removeFittingRange(range); // last one added
        assertEquals(2, ranges.size());
        assertEquals(10.5, ranges.get(0).getStartPoint(), 0.00001);
        assertEquals(13.0, ranges.get(0).getEndPoint(), 0.00001);
        assertEquals(10.5, ranges.get(1).getStartPoint(), 0.00001);
        assertEquals(13.0, ranges.get(1).getEndPoint(), 0.00001);
        
        // remove another range
        config.removeFittingRange(0);
        assertEquals(1, ranges.size());
        assertEquals(10.5, ranges.get(0).getStartPoint(), 0.00001);
        assertEquals(13.0, ranges.get(0).getEndPoint(), 0.00001);
        
        // add dummy range to check that clearRanges works
        config.addFittingRange(range);
        config.clearFittingRanges();
        assertEquals(0, config.getFittingRanges().size());
        
        // assert that all the points are included in the fit calculation
        controller.setSedModel(model);
        allData = controller.constructSherpaCall(model);
        assertEquals(3, allData.getX().length);
    }

    private FitConfiguration createFit() throws Exception {
        FitConfiguration fit = new FitConfiguration();

        ModelFactory factory = new ModelFactory();
        Model m = factory.getModel("polynomial", "m1");
        Parameter c0 = m.findParameter("c0");
        c0.setFrozen(0);
        c0.setVal(0.1);

        Parameter c1 = m.findParameter("c1");
        c1.setFrozen(0);
        c1.setVal(0.2);

        Model m2 = factory.getModel("powlaw1d", "m2");
        m2.findParameter("gamma").setVal(0.01);
        m2.findParameter("ampl").setVal(0.02);

        DefaultCustomModel userModel = Mockito.mock(DefaultCustomModel.class);
        Model model = new ModelStub();
        UserModel um = new UserModelStub();
        Mockito.stub(userModel.makeModel(Mockito.anyString())).toReturn(model);
        Mockito.stub(userModel.makeUserModel(Mockito.anyString())).toReturn(um);
        fit.addUserModel(userModel, "m3");

        CompositeModel cm = fit.getModel();
        cm.setName("m1+m2+m3");
        cm.addPart(m);
        cm.addPart(m2);

        fit.setModel(cm);

        fit.setMethod(OptimizationMethod.LevenbergMarquardt);
        fit.setStat(Statistic.LeastSquares);

        fit.setDof(30);
        fit.setStatVal(0.1234);
        fit.setrStat(0.4321);
        fit.setqVal(0.1357);
        fit.setDof(30);
        fit.setNumPoints(430);
        fit.setnFev(731);

        ConfidenceResults confidenceResults = SAMPFactory.get(ConfidenceResults.class);
        confidenceResults.setParnames(Arrays.asList("m1.c0", "m1.c1", "m2.c2"));
        confidenceResults.setParmins(new double[]{-0.1, -0.2, -0.3});
        confidenceResults.setParmaxes(new double[]{0.1, 0.2, 0.3});
        confidenceResults.setParvals(new double[]{1.0, 2.0, 3.0});
        confidenceResults.setSigma(1.6);
        confidenceResults.setPercent(96.3);
        fit.setConfidenceResults(confidenceResults);

        return fit;
    }


    private class ModelStub implements Model {
        private List<Parameter> pars;

        public ModelStub() {
            pars = new ArrayList<>();
            pars.add(new ParameterStub("m3.p1", 0.1, 1));
            pars.add(new ParameterStub("m3.p2", 0.2, 0));
            pars.add(new ParameterStub("m3.p3", 0.3, 0));
        }

        @Override
        public String getId() {
            return "m3";
        }

        @Override
        public void setId(String id) {

        }

        @Override
        public String getName() {
            return "tablemodel.m3";
        }

        @Override
        public void setName(String name) {

        }

        @Override
        public String getDescription() {
            return null;
        }

        @Override
        public void setDescription(String description) {

        }

        @Override
        public List<Parameter> getPars() {
            return pars;
        }

        @Override
        public void addPar(Parameter par) {

        }

        @Override
        public Parameter findParameter(String paramName) {
            return null;
        }

        @Override
        public String toString() {
            return getName();
        }
    }

    private class ParameterStub implements Parameter {
        private String name;
        private Double val;
        private Integer frozen;

        public ParameterStub(String name, Double val, Integer frozen) {
            this.name = name;
            this.val = val;
            this.frozen = frozen;
        }

        @Override
        public String getName() {
            return name;
        }

        @Override
        public void setName(String name) {

        }

        @Override
        public Double getVal() {
            return val;
        }

        @Override
        public void setVal(Double value) {

        }

        @Override
        public Double getMin() {
            return null;
        }

        @Override
        public void setMin(Double min) {

        }

        @Override
        public Double getMax() {
            return null;
        }

        @Override
        public void setMax(Double max) {

        }

        @Override
        public Integer getFrozen() {
            return frozen;
        }

        @Override
        public void setFrozen(Integer frozen) {

        }

        @Override
        public Integer getHidden() {
            return null;
        }

        @Override
        public void setHidden(Integer hidden) {

        }

        @Override
        public Integer getAlwaysfrozen() {
            return null;
        }

        @Override
        public void setAlwaysfrozen(Integer alwaysfrozen) {

        }

        @Override
        public String getUnits() {
            return null;
        }

        @Override
        public void setUnits(String units) {

        }

        @Override
        public String getLink() {
            return null;
        }

        @Override
        public void setLink(String link) {

        }
    }

    private class UserModelStub implements UserModel {

        @Override
        public String getName() {
            return "function.m3";
        }

        @Override
        public void setName(String name) {

        }

        @Override
        public String getFile() {
            return "file://somewhere/on/disk";
        }

        @Override
        public void setFile(String path) {

        }

        @Override
        public String getFunction() {
            return "somefunc";
        }

        @Override
        public void setFunction(String function) {

        }
    }
}