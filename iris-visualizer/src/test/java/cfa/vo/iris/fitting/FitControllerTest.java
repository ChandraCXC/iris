package cfa.vo.iris.fitting;

import cfa.vo.interop.SAMPFactory;
import cfa.vo.iris.fitting.custom.CustomModelsManager;
import cfa.vo.iris.fitting.custom.DefaultCustomModel;
import cfa.vo.iris.sed.ExtSed;
import cfa.vo.iris.test.unit.TestUtils;
import cfa.vo.sherpa.ConfidenceResults;
import cfa.vo.sherpa.SherpaClient;
import cfa.vo.sherpa.models.*;
import cfa.vo.sherpa.optimization.OptimizationMethod;
import cfa.vo.sherpa.stats.Statistic;
import com.sun.xml.internal.messaging.saaj.util.ByteInputStream;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import net.javacrumbs.jsonunit.JsonAssert;
import static org.junit.Assert.*;

public class FitControllerTest {
    private FitController controller;
    private FitConfiguration configuration;
    private ByteArrayOutputStream os = new ByteArrayOutputStream();

    @Before
    public void setUp() throws Exception {
        ExtSed sed = Mockito.mock(ExtSed.class);
        configuration = createFit();
        Mockito.stub(sed.getFit()).toReturn(configuration);
        Mockito.stub(sed.toString()).toReturn("MySed (Segments: 3)");
        CustomModelsManager modelsManager = Mockito.mock(CustomModelsManager.class);
        SherpaClient client = Mockito.mock(SherpaClient.class);
        controller = new FitController(sed, modelsManager, client);
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
            return null;
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