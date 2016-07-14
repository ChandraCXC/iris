package cfa.vo.sherpa;

import cfa.vo.interop.SAMPFactory;
import cfa.vo.interop.SampService;
import cfa.vo.iris.fitting.FitConfiguration;
import cfa.vo.iris.sed.ExtSed;
import cfa.vo.sedlib.Segment;
import cfa.vo.sherpa.models.CompositeModel;
import cfa.vo.sherpa.models.Model;
import cfa.vo.sherpa.models.ModelFactory;
import cfa.vo.sherpa.models.Parameter;
import cfa.vo.sherpa.optimization.Method;
import cfa.vo.sherpa.optimization.OptimizationMethod;
import cfa.vo.sherpa.stats.Stat;
import cfa.vo.sherpa.stats.Statistic;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import static org.junit.Assert.*;

public class SherpaClientTest {
    ExtSed sed;
    SherpaClient client;
    private CompositeModel cm;
    double[] x = {1.0, 2.0, 3.0};
    double[] y = {1.0, 4.0, 9.0};

    @Before
    public void setUp() throws Exception {
        client = new SherpaClient(Mockito.mock(SampService.class));
        sed = new ExtSed("testsed", false);
        Segment segment = new Segment();
        segment.setSpectralAxisValues(x);
        segment.setFluxAxisValues(y);
        segment.setSpectralAxisUnits("Angstrom");
        segment.setFluxAxisUnits("photon/s/cm2/Angstrom");
        sed.addSegment(segment);

        ModelFactory factory = new ModelFactory();
        Model m = factory.getModel("polynomial", "m");
        Parameter c0 = m.findParameter("c0");
        c0.setFrozen(0);

        Parameter c1 = m.findParameter("c1");
        c1.setFrozen(0);

        cm = SAMPFactory.get(CompositeModel.class);
        cm.setName("m");
        cm.addPart(m);

        Stat s = Statistic.LeastSquares;

        Method method = OptimizationMethod.LevenbergMarquardt;

        FitConfiguration conf = new FitConfiguration();
        conf.setMethod(method);
        conf.setModel(cm);
        conf.setStat(s);

        sed.setFit(conf);

    }

    @Test
    public void testMake() throws Exception {
        SherpaFitConfiguration fitConf = client.make(sed);
        assertEquals(1, fitConf.getDatasets().size());
        Data data = fitConf.getDatasets().get(0);
        for (int i=0; i<3; i++) {
            assertEquals(x[i], data.getX()[i], 1e-10);
            assertEquals(y[i], data.getY()[i], 1e-10);
        }
        assertEquals(Statistic.LeastSquares, fitConf.getStat());
        assertEquals(OptimizationMethod.LevenbergMarquardt, fitConf.getMethod());

        assertEquals(1, fitConf.getModels().size());
        assertEquals(cm.getName(), fitConf.getModels().get(0).getName());
        assertEquals(1, cm.getParts().size());
        assertEquals(cm.getParts().get(0).getName(), cm.getParts().get(0).getName());
    }
}