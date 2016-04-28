package cfa.vo.iris.fitting;

import cfa.vo.interop.SAMPFactory;
import cfa.vo.iris.fitting.custom.CustomModelsManager;
import cfa.vo.iris.sed.ExtSed;
import cfa.vo.sherpa.SherpaClient;
import cfa.vo.sherpa.models.CompositeModel;
import cfa.vo.sherpa.models.Model;
import cfa.vo.sherpa.models.ModelFactory;
import cfa.vo.sherpa.models.Parameter;
import cfa.vo.sherpa.optimization.OptimizationMethod;
import cfa.vo.sherpa.stats.Statistic;
import org.apache.commons.io.FileUtils;
import org.junit.Test;
import org.mockito.Mockito;

import java.io.ByteArrayOutputStream;
import java.io.File;

import static org.junit.Assert.*;

public class FitControllerTest {

    @Test
    public void testSave() throws Exception {
        ExtSed sed = Mockito.mock(ExtSed.class);
        FitConfiguration configuration = createFit();
        Mockito.when(sed.getFit()).thenReturn(configuration);
        Mockito.when(sed.getId()).thenReturn("MySed");
        CustomModelsManager modelsManager = Mockito.mock(CustomModelsManager.class);
        SherpaClient client = Mockito.mock(SherpaClient.class);

        FitController controller = new FitController(sed, modelsManager, client);
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        controller.save(os);

        assertEquals(getOutputString(), os.toString("UTF-8"));
    }

    private FitConfiguration createFit() {
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

        CompositeModel cm = SAMPFactory.get(CompositeModel.class);
        cm.setName("m1+m2");
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

        return fit;
    }

    private String getOutputString() throws Exception {
        String path = getClass().getResource("fit.output").getFile();
        return FileUtils.readFileToString(new File(path));
    }
}