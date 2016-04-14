package cfa.vo.iris.fitting.custom;

import cfa.vo.sherpa.models.Model;
import cfa.vo.sherpa.models.Parameter;
import cfa.vo.sherpa.models.UserModel;
import org.junit.Test;

import java.net.URL;

import static org.junit.Assert.*;

public class DefaultCustomModelTest {

    @Test
    public void testMakeArrayInt() throws Exception {
        Integer[] observed = DefaultCustomModel.makeArray("1,2,3", Integer.class);
        assertArrayEquals(new Integer[]{1,2,3}, observed);
    }

    @Test
    public void testMakeArrayDouble() throws Exception {
        Double[] observed = DefaultCustomModel.makeArray("1.0,2.0,3.0", Double.class);
        assertArrayEquals(new Double[]{1.0,2.0,3.0}, observed);
    }

    @Test
    public void testMakeArrayBoolean() throws Exception {
        Boolean[] observed = DefaultCustomModel.makeArray("True,False,True", Boolean.class);
        assertArrayEquals(new Boolean[]{Boolean.TRUE,Boolean.FALSE,Boolean.TRUE}, observed);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testMakeArrayClassError() throws Exception {
        DefaultCustomModel.makeArray("True,False", Double.class);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testMakeArrayConstructorError() throws Exception {
        DefaultCustomModel.makeArray("True,False", DefaultCustomModel.class);
    }

    @Test
    public void testMakeModel() throws Exception {
        DefaultCustomModel m = new DefaultCustomModel();
        m.setName("test");
        m.setType(CustomModelType.TEMPLATE);
        m.setParnames("parA,parB,parC");
        m.setParvals("1.0,2.0,3.0");
        m.setParmins("0.0, 0.1, 0.2");
        m.setParmaxs("10.0, 10.1, 10.2");
        m.setParfrozen("False,False, True");
        Model model = m.makeModel("my");

        Parameter p = model.getPars().get(0);
        assertEquals("my.parA", p.getName());
        assertEquals(new Double(1.0), p.getVal());
        assertEquals(new Double(0.0), p.getMin());
        assertEquals(new Double(10.0), p.getMax());
        assertEquals(new Integer(0), p.getFrozen());

        p = model.getPars().get(1);
        assertEquals("my.parB", p.getName());
        assertEquals(new Double(2.0), p.getVal());
        assertEquals(new Double(0.1), p.getMin());
        assertEquals(new Double(10.1), p.getMax());
        assertEquals(new Integer(0), p.getFrozen());

        p = model.getPars().get(2);
        assertEquals("my.parC", p.getName());
        assertEquals(new Double(3.0), p.getVal());
        assertEquals(new Double(0.2), p.getMin());
        assertEquals(new Double(10.2), p.getMax());
        assertEquals(new Integer(1), p.getFrozen());

        assertEquals(3, model.getPars().size());
        assertEquals("template.my", model.getName());
    }

    @Test
    public void testMakeUserModel() throws Exception {
        DefaultCustomModel m = new DefaultCustomModel();
        m.setName("test");
        m.setType(CustomModelType.TEMPLATE);
        m.setUrl(new URL("file://some/url"));
        UserModel model = m.makeUserModel("my");
        assertEquals("template.my", model.getName());

        m.setType(CustomModelType.USERMODEL);
        model = m.makeUserModel("my");
        assertEquals("usermodel.my", model.getName());

        m.setType(CustomModelType.TABLEMODEL);
        model = m.makeUserModel("my");
        assertEquals("tablemodel.my", model.getName());
    }

}