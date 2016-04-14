package cfa.vo.sherpa;

import cfa.vo.interop.SAMPMessage;
import cfa.vo.interop.SampService;
import cfa.vo.sherpa.models.CompositeModel;
import cfa.vo.sherpa.models.Model;
import cfa.vo.sherpa.models.Parameter;
import cfa.vo.sherpa.optimization.Method;
import cfa.vo.sherpa.optimization.OptimizationMethod;
import cfa.vo.sherpa.stats.Stat;
import cfa.vo.sherpa.stats.Stats;
import com.google.common.collect.ImmutableMap;
import org.astrogrid.samp.Response;
import org.junit.Before;
import org.junit.Test;

import java.util.Map;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class SherpaClientTest {
    private SherpaClient client;
    private double[] x = {1.0, 2.0, 3.0};
    private double[] y = {1.0, 2.0, 3.0};

    @Before
    public void setUp() throws Exception {

        // Stub SampService
        SampService serviceStub = mock(SampService.class);

        // Stub Response from service and its result
        Response responseStub = mock(Response.class);
        Map<String, String> result = ImmutableMap.of(
                "succeeded", "1"
        );
        when(responseStub.getResult()).thenReturn(result);

        // Stub serviceStub method called when fitting.
        when(serviceStub.callSherpaAndRetry(any(SAMPMessage.class))).thenReturn(responseStub);
        client = new SherpaClient(serviceStub);
    }

    @Test
    public void testSimpleFit() throws Exception {
        Data data = client.createData("test");
        data.setX(x);
        data.setY(y);

        Model m = client.createModel("polynomial");
        Parameter c0 = m.findParameter("c0");
        c0.setFrozen(0);

        Parameter c1 = m.findParameter("c1");
        c1.setFrozen(0);

        CompositeModel cm = client.createCompositeModel("m", m);

        Stat s = Stats.LeastSquares;

        Method method = OptimizationMethod.LevenbergMarquardt;

        FitResults fr = client.fit(data, cm, s, method);
        assertTrue(fr.getSucceeded());
    }
}