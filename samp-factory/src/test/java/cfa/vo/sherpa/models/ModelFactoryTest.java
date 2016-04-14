package cfa.vo.sherpa.models;

import org.junit.Before;
import org.junit.Test;
import java.util.NoSuchElementException;

import static org.junit.Assert.*;

public class ModelFactoryTest {
    private ModelFactory factory;

    @Before
    public void setUp() {
        factory = new ModelFactory();
    }

    @Test
    public void testSize() throws Exception {
        assertEquals(49, factory.getSize());
    }

    @Test
    public void testPowerlaw() throws Exception {
        Model m = factory.getModel("powerlaw", "p1d");
        assertEquals("powerlaw.p1d", m.getName());
        assertEquals("Power law function", m.getDescription());

        // refer
        Parameter par = m.findParameter("refer");
        assertEquals((Double) 5000d, par.getVal());
        assertEquals((Integer) 0, par.getAlwaysfrozen());
        assertEquals((Integer) 1, par.getFrozen());
//        assertEquals((Integer) 0, par.getHidden());
        assertEquals((Double) 1.1754943508222875e-38, par.getMin());
        assertEquals((Double) 3.4028234663852886e+38, par.getMax());
//        assertEquals("angstroms", par.getUnits());
//        assertEquals("INDEF", par.getLink());

        // ampl
        par = m.findParameter("ampl");
        assertEquals((Double) 1.0, par.getVal());
        assertEquals((Integer) 0, par.getAlwaysfrozen());
        assertEquals((Integer) 0, par.getFrozen());
//        assertEquals((Integer) 0, par.getHidden());
        assertEquals((Double) 1.1754943508222875e-38, par.getMin());
        assertEquals((Double) 3.4028234663852886e+38, par.getMax());
//        assertEquals("", par.getUnits());
//        assertEquals("INDEF", par.getLink());

        // index
        par = m.findParameter("index");
        assertEquals((Double) (-0.5), par.getVal());
        assertEquals((Integer) 0, par.getAlwaysfrozen());
        assertEquals((Integer) 0, par.getFrozen());
//        assertEquals((Integer) 0, par.getHidden());
        assertEquals((Double) (-10.0), par.getMin());
        assertEquals((Double) 10.0, par.getMax());
//        assertEquals("", par.getUnits());
//        assertEquals("INDEF", par.getLink());
    }

    @Test
    public void testParameterException() throws Exception {
        Model m = factory.getModel("powerlaw", "p1d");
        try {
            m.findParameter("foo");
        } catch (NoSuchElementException ex) {
            return;
        }
        fail("Should have thrown exception");
    }

    @Test(expected=NoSuchElementException.class)
    public void testModelException() throws Exception {
        factory.getModel("powerlaw1d", "p1d");
    }
}