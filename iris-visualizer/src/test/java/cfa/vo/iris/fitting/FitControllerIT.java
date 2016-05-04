package cfa.vo.iris.fitting;

import cfa.vo.interop.SAMPFactory;
import cfa.vo.iris.fitting.custom.CustomModelsManager;
import cfa.vo.iris.fitting.custom.DefaultCustomModel;
import cfa.vo.iris.sed.ExtSed;
import cfa.vo.iris.test.unit.SherpaResource;
import cfa.vo.iris.test.unit.TestUtils;
import cfa.vo.sedlib.Segment;
import cfa.vo.sherpa.ConfidenceResults;
import cfa.vo.sherpa.Data;
import cfa.vo.sherpa.FitResults;
import cfa.vo.sherpa.SherpaClient;
import cfa.vo.sherpa.models.*;
import cfa.vo.sherpa.optimization.OptimizationMethod;
import cfa.vo.sherpa.stats.Statistic;
import net.javacrumbs.jsonunit.JsonAssert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mockito;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

public class FitControllerIT {
    private FitController controller;
    private FitConfiguration configuration;
    private CustomModelsManager modelsManager;
    private SherpaClient client;
    private Data data;
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
        configuration = createFit();
        sed.setFit(configuration);
        modelsManager = Mockito.mock(CustomModelsManager.class);
        data = SAMPFactory.get(Data.class);
        data.setX(x);
        data.setY(y);
        data.setStaterror(err);
        client = sherpa.getClient();
        controller = new FitController(sed, modelsManager, client);
    }

    @Test
    public void testFit() throws Exception {
        FitResults results = controller.fit();
        double[] expected = {0, 1};
        assertArrayEquals(expected, results.getParvals(), 0.01);
    }

    @Test
    public void testEvaluate() throws Exception {
        Model model = controller.getFit().getModel().getParts().get(0);
        model.findParameter("c0").setVal(0.0);
        model.findParameter("c1").setVal(1.0);
        model.findParameter("c1").setFrozen(0);
        Data data = controller.evaluateModel();
        assertArrayEquals(x, data.getX(), 0.001);
        assertArrayEquals(y, data.getY(), 0.001);
        assertArrayEquals(err, data.getStaterror(), 0.001);
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
        fit.setStat(Statistic.LeastSquares);

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