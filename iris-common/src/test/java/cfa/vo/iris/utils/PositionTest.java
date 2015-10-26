package cfa.vo.iris.utils;

import junit.framework.Assert;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by olaurino on 10/26/15.
 */
public class PositionTest {

    @Test
    public void testGetDec() throws Exception {
        Position pos = new Position(111.1, 222.2);
        Assert.assertEquals(222.2, pos.getDec());
    }

    @Test
    public void testGetRa() throws Exception {
        Position pos = new Position(3.0, 4.0);
        Assert.assertEquals(3.0, pos.getRa());
    }
}