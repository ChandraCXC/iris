package cfa.vo.iris.utils;

import org.junit.Test;

import static org.junit.Assert.*;

public class UTYPETest {
    @Test
    public void testFull() throws Exception {
        UTYPE u = new UTYPE("test:something");

        assertEquals("test", u.prefix);
        assertEquals("something", u.main);
    }

    @Test
    public void testMainOnly() throws Exception {
        UTYPE u = new UTYPE("something");

        assertNull(u.prefix);
        assertEquals("something", u.main);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testErrorEmpty() throws Exception {
        UTYPE u = new UTYPE("");
    }

    @Test(expected=IllegalArgumentException.class)
    public void testErrorNull() throws Exception {
        UTYPE u = new UTYPE(null);
    }

    @Test
    public void testEqualityFull() throws Exception {
        UTYPE u = new UTYPE("test:something");
        UTYPE u2 = new UTYPE("TEST:someTHing");
        assertTrue(u2.equals(u) && u.equals(u2));
        assertTrue(u2.hashCode() == u.hashCode());
    }

    @Test
    public void testEqualityMainOnly() throws Exception {
        UTYPE u = new UTYPE("test:something");
        UTYPE u2 = new UTYPE("someTHing");
        assertTrue(u2.equals(u) && u.equals(u2));
        assertTrue(u2.hashCode() == u.hashCode());
    }

    @Test
    public void testEqualitySpectrum() throws Exception {
        UTYPE u = new UTYPE("test:SpEctruM.something");
        UTYPE u2 = new UTYPE("someTHing");
        assertTrue(u2.equals(u) && u.equals(u2));
        assertTrue(u2.hashCode() == u.hashCode());
    }

    @Test
    public void testCanonicalString() throws Exception {
        UTYPE u = new UTYPE("test:something");
        UTYPE u2 = new UTYPE("TEST:someTHing");
        assertTrue(u2.getCanonicalString().equals(u.getCanonicalString()));
        assertEquals("test:something", u.getCanonicalString());

        u = new UTYPE("somethiNG");
        assertEquals("something", u.getCanonicalString());
    }

    @Test
    public void testMain() throws Exception {
        UTYPE u = new UTYPE("test:something");
        UTYPE u2 = new UTYPE("someTHing");
        assertTrue(u2.getMain().equals(u.getMain()));
        assertEquals("something", u.getMain());
    }

    @Test
    public void testToString() throws Exception {
        UTYPE u = new UTYPE("test:something");
        UTYPE u2 = new UTYPE("TEST:someTHing");
        assertTrue(u2.toString().equals(u.toString()));
        assertEquals("UTYPE{prefix='test', main='something'}", u.toString());
    }


}