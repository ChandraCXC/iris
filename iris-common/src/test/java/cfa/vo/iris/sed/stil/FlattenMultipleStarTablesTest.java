package cfa.vo.iris.sed.stil;

import cfa.vo.iris.utils.UTYPE;
import org.junit.Before;
import org.junit.Test;
import uk.ac.starlink.table.*;
import uk.ac.starlink.ttools.filter.AssertException;
import java.util.Collection;
import java.util.NoSuchElementException;

import static org.junit.Assert.*;

public class FlattenMultipleStarTablesTest {
    private ColumnStarTable table1;
    private ColumnStarTable table2;
    private final double[] SPECTRAL = {1.0, 1.1, 1.2};
    private final double[] FLUX = {2.0, 2.1, 2.2};
    private final String[] STRING = {"One", "Two", "Three"};
    private final String SPECTRAL_NAME = "spectral";
    private final String FLUX_NAME = "flux";
    private final String STRING_NAME = "stringField";
    private final String SPECTRAL_UTYPE = "test:spectral";
    private final String FLUX_UTYPE = "test:flux";
    private final String STRING_UTYPE = "test:string";
    private final StilColumnManager manager = new StilColumnManager();

    @Before
    public void setUp() {
        ColumnInfo info11 = new ColumnInfo(SPECTRAL_NAME, Double.class, "");
        // take only the main part of the utype
        info11.setUtype(SPECTRAL_UTYPE.split(":")[1]);
        ColumnData columnSpectral1 = PrimitiveArrayColumn.makePrimitiveColumn(info11, SPECTRAL);
        ColumnInfo info12 = new ColumnInfo(FLUX_NAME, Double.class, "");
        info12.setUtype(FLUX_UTYPE);
        ColumnData columnFlux1 = PrimitiveArrayColumn.makePrimitiveColumn(info12, FLUX);

        ColumnInfo info21 = new ColumnInfo(SPECTRAL_NAME, Double.class, "");
        info21.setUtype(SPECTRAL_UTYPE.toUpperCase());
        ColumnData columnSpectral2 = PrimitiveArrayColumn.makePrimitiveColumn(info21, SPECTRAL);
        ColumnInfo info22 = new ColumnInfo(FLUX_NAME, Double.class, "");
        info22.setUtype(FLUX_UTYPE.toUpperCase());
        ColumnData columnFlux2 = PrimitiveArrayColumn.makePrimitiveColumn(info22, FLUX);
        ColumnInfo info23 = new ColumnInfo(STRING_NAME, String.class, "");
        info23.setUtype(STRING_UTYPE.toUpperCase());
        ColumnData columnString2 = new ObjectArrayColumn(info23, STRING);

        table1 = new ColumnStarTable() {
            @Override
            public long getRowCount() {
                return 3;
            }
        };
        table1.addColumn(columnSpectral1);
        table1.addColumn(columnFlux1);

        table2 = new ColumnStarTable() {
            @Override
            public long getRowCount() {
                return 3;
            }
        };
        table2.addColumn(columnFlux2);
        table2.addColumn(columnString2);
        table2.addColumn(columnSpectral2);
    }

    @Test
    public void testExtractColumns() throws Exception {
        // Tables have two columns with the same utype, plus one column with a third utype.
        // Columns extracted from the two tables should be a set of three columnInfo with three different utypes
        ColumnInfo[] columnInfos = manager.extractColumns(table1, table2);
        assertEquals(3, columnInfos.length);

        // We are assuming a TreeMap implementation in index, so keys are sorted alphabetically.
        assertEquals(new UTYPE(FLUX_UTYPE), new UTYPE(columnInfos[0].getUtype()));
        assertEquals(new UTYPE(SPECTRAL_UTYPE), new UTYPE(columnInfos[1].getUtype()));
        assertEquals(new UTYPE(STRING_UTYPE), new UTYPE(columnInfos[2].getUtype()));
    }

    @Test
    public void testStarTableFlatten() throws Exception {
        ColumnStarTable table = manager.flatten(table1, table2);

        assertEquals(3, table.getColumnCount());
        assertEquals(6, table.getRowCount());

        // assuming specific implementation is ArrayColumn
        int spectralIndex = StilColumnManager.getColumnIndex(table, SPECTRAL_UTYPE);
        Object[] spectral = (Object[])((ArrayColumn)table.getColumnData(spectralIndex)).getArray();
        assertArrayEquals(new Double[]{1.0, 1.1, 1.2, 1.0, 1.1, 1.2}, spectral);

        int fluxIndex = StilColumnManager.getColumnIndex(table, FLUX_UTYPE);
        Object[] flux = (Object[]) ((ArrayColumn)table.getColumnData(fluxIndex)).getArray();
        assertArrayEquals(new Double[]{2.0, 2.1, 2.2, 2.0, 2.1, 2.2}, flux);

        int stringIndex = StilColumnManager.getColumnIndex(table, STRING_UTYPE);
        Object[] string = (Object[])((ArrayColumn)table.getColumnData(stringIndex)).getArray();
        assertArrayEquals(new String[]{null, null, null, "One", "Two", "Three"}, string);
    }

    @Test
    public void testInfoIndex() throws Exception {
        StilColumnManager.ColumnInfoIndex index = new StilColumnManager.ColumnInfoIndex();
        ColumnInfo info = new ColumnInfo(SPECTRAL_NAME);
        ColumnInfo info2 = new ColumnInfo(STRING_NAME);
        info2.setUtype(STRING_UTYPE);

        assertTrue(index.put(info));

        assertTrue(index.hasInfo(info));

        // Should return false because info2 does not have either name or utype matching those of info
        assertFalse(index.hasInfo(info2));

        assertTrue(index.put(info2));

        assertTrue(index.hasInfo(info2));

        ColumnInfo info3 = new ColumnInfo(FLUX_NAME);
        info3.setUtype(STRING_UTYPE);

        // Should return true because utype is the same, even if we did not add info3 to the index;
        assertTrue(index.hasInfo(info3));

        // Should return false because a slot with this ID is already taken
        assertFalse(index.put(info3));

        assertSame(info, index.get(SPECTRAL_NAME));
        assertSame(info2, index.get(STRING_UTYPE));

        try {
            index.get(STRING_NAME);
            throw new AssertException("should have failed");
        } catch (NoSuchElementException e) {
            // should throw exception because STRING_NAME is not an ID for any of the infos in the index
        }

        ColumnInfo info4 = new ColumnInfo(STRING_NAME);
        info4.setUtype(FLUX_UTYPE);
        // should return false because even though it has the same name as info2, they do not have the same utype
        assertFalse(index.hasInfo(info4));

        ColumnInfo info5 = new ColumnInfo("");
        // ColumnInfos with empty names and no utypes are not acceptable. There is no means to compare their semantic content.
        assertFalse(index.put(info5));
        // should return false because even though it has the same name as info2, they do not have the same utype
        assertFalse(index.hasInfo(info5));

        Collection<ColumnInfo> infos = index.getValues();
        assertEquals(2, infos.size());
        assertTrue(infos.contains(info));
        assertTrue(infos.contains(info2));

        assertTrue(StilColumnManager.ColumnInfoIndex.sameId(info2, info3));
        assertFalse(StilColumnManager.ColumnInfoIndex.sameId(info, info2));
    }

    @Test
    public void testGetColumnIndex() throws Exception {
        assertEquals(0, StilColumnManager.getColumnIndex(table1, SPECTRAL_UTYPE));
        assertEquals(1, StilColumnManager.getColumnIndex(table1, FLUX_UTYPE));
        assertEquals(-1, StilColumnManager.getColumnIndex(table1, STRING_UTYPE));
        assertEquals(0, StilColumnManager.getColumnIndex(table2, FLUX_UTYPE));
        assertEquals(2, StilColumnManager.getColumnIndex(table2, SPECTRAL_UTYPE));
        assertEquals(1, StilColumnManager.getColumnIndex(table2, STRING_UTYPE));
    }
}
