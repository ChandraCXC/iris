package cfa.vo.iris.gui.widgets;

import cfa.vo.interop.SAMPFactory;
import cfa.vo.sherpa.models.Model;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

public class ModelExpressionVerifierTest {
    private ModelExpressionVerifier verifier;
    private List<Model> models;

    @Before
    public void setUp() {
        verifier = new ModelExpressionVerifier();
        models = new ArrayList<>();
        Model m = SAMPFactory.get(Model.class);
        m.setName("amodel.m1");
        models.add(m);
        m = SAMPFactory.get(Model.class);
        m.setName("anothermodel.m2");
        models.add(m);
    }

    @Test
    public void testVerifySingle() throws Exception {
        assertTrue(verifier.verify("m1", models));
    }

    @Test
    public void testVerifyNumber() throws Exception {
        assertTrue(verifier.verify("3*m1+m2", models));
    }

    @Test
    public void testVerifyEmpty() throws Exception {
        assertFalse(verifier.verify("", models));
    }

    @Test
    public void testVerifyNull() throws Exception {
        assertFalse(verifier.verify(null, models));
    }

    @Test
    public void testVerifyNoModel() throws Exception {
        assertFalse(verifier.verify("m1+m3", models));
    }
}