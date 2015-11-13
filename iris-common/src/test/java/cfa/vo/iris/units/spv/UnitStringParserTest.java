package cfa.vo.iris.units.spv;

import org.junit.Test;

import static org.junit.Assert.*;

public class UnitStringParserTest {
    @Test
    public void expressions() throws Exception {
        UnitStringParser.FactoredUnit u = UnitStringParser.parse("1e2 Jy");
        assertEquals(1e2, u.getFactor(), 1e-20);
        assertEquals("Jy", u.getUnit());

        u = UnitStringParser.parse("10*Jy");
        assertEquals(10.0, u.getFactor(), 1e-20);
        assertEquals("Jy", u.getUnit());

        u = UnitStringParser.parse("10 Jy");
        assertEquals(10.0, u.getFactor(), 1e-20);
        assertEquals("Jy", u.getUnit());

        u = UnitStringParser.parse("1e12 Jy");
        assertEquals(1e12, u.getFactor(), 1e-20);
        assertEquals("Jy", u.getUnit());

        u = UnitStringParser.parse("1e2Jy");
        assertEquals(1e2, u.getFactor(), 1e-20);
        assertEquals("Jy", u.getUnit());

        u = UnitStringParser.parse("1.0e3*erg/s/cm2");
        assertEquals(1e3, u.getFactor(), 1e-20);
        assertEquals("erg/s/cm2", u.getUnit());

        u = UnitStringParser.parse("1.0e3*erg s**-1 cm**-2");
        assertEquals(1e3, u.getFactor(), 1e-20);
        assertEquals("erg s**-1 cm**-2", u.getUnit());

        u = UnitStringParser.parse("+3e-15*Jy");
        assertEquals(3e-15, u.getFactor(), 1e-20);
        assertEquals("Jy", u.getUnit());

        u = UnitStringParser.parse(".1*Jy");
        assertEquals(.1, u.getFactor(), 1e-20);
        assertEquals("Jy", u.getUnit());

        u = UnitStringParser.parse("1.*Jy");
        assertEquals(1, u.getFactor(), 1e-20);
        assertEquals("Jy", u.getUnit());

        u = UnitStringParser.parse("Jy");
        assertEquals(1, u.getFactor(), 1e-20);
        assertEquals("Jy", u.getUnit());

        u = UnitStringParser.parse("");
        assertEquals(1, u.getFactor(), 1e-20);
        assertEquals("", u.getUnit());

        u = UnitStringParser.parse("1e2");
        assertEquals(1e2, u.getFactor(), 1e-20);
        assertEquals("", u.getUnit());
    }

}