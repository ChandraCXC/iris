package cfa.vo.iris.utils;

import cfa.vo.utils.Time;
import org.junit.Before;
import org.junit.Test;

import java.util.concurrent.TimeUnit;

import static org.junit.Assert.*;

public class TimeTest {
    private Time timeout;

    @Before
    public void setUp() throws Exception {
        timeout = new Time(3, TimeUnit.SECONDS);
    }

    @Test
    public void testGetUnit() throws Exception {
        assertEquals(TimeUnit.SECONDS, timeout.getUnit());
    }

    @Test
    public void testGetAmount() throws Exception {
        assertEquals(3, timeout.getAmount());
    }

    @Test
    public void testConvert() throws Exception {
        Time newTime = timeout.convertTo(TimeUnit.MILLISECONDS);
        assertEquals(3000, newTime.getAmount());
        assertEquals(TimeUnit.MILLISECONDS, newTime.getUnit());

        newTime = newTime.convertTo(timeout.getUnit());
        assertEquals(3, newTime.getAmount());
        assertEquals(TimeUnit.SECONDS, newTime.getUnit());
    }
}