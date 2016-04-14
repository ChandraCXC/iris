package cfa.vo.iris.gui.widgets;

import cfa.vo.interop.SAMPFactory;
import cfa.vo.iris.fitting.FitConfiguration;
import cfa.vo.sherpa.models.Model;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class ModelExpressionVerifierTest {
    private ModelExpressionVerifier verifier;
    private FitConfiguration fit;

    @Before
    public void setUp() {
        verifier = new ModelExpressionVerifier();
        fit = new FitConfiguration();
        Model m = SAMPFactory.get(Model.class);
        m.setName("amodel.m1");
        fit.addModel(m);
        m = SAMPFactory.get(Model.class);
        m.setName("anothermodel.m2");
        fit.addModel(m);
    }

    @Test
    public void testVerifySingle() throws Exception {
        fit.setExpression("m1");
        assertTrue(verifier.verify(fit));
    }

    @Test
    public void testVerifyNumber() throws Exception {
        fit.setExpression("3*m1+m2");
        assertTrue(verifier.verify(fit));
    }

    @Test
    public void testVerifyEmpty() throws Exception {
        fit.setExpression("");
        assertFalse(verifier.verify(fit));
    }

    @Test
    public void testVerifyNull() throws Exception {
        fit.setExpression(null);
        assertFalse(verifier.verify(fit));
    }

    @Test
    public void testVerifyNoModel() throws Exception {
        fit.setExpression("m1+m3");
        assertFalse(verifier.verify(fit));
    }
}