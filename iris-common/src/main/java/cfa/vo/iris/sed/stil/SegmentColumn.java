package cfa.vo.iris.sed.stil;

import java.io.IOException;
import java.util.BitSet;

import cfa.vo.iris.sed.stil.SegmentStarTable.Column;
import uk.ac.starlink.table.ColumnData;

/**
 * Primary class for column information in a SegmentStarTable.
 * 
 * Note that Comparable and compare are all over the Column enum. This is so
 * that the set of columns in the star table will remain in line and in order
 * with what data is available to consumers of SegmentStarTable class.
 * 
 */
abstract class SegmentColumn extends ColumnData
        implements Comparable<SegmentColumn> {

    public Column column;

    public SegmentColumn(Column column) {
        this.column = column;
        this.setColumnInfo(column.getColumnInfo());
    }

    @Override
    public int compareTo(SegmentColumn arg0) {
        SegmentColumn o = (SegmentColumn) arg0;
        return column.compareTo(o.column);
    }

    @Override
    public boolean equals(Object arg0) {
        SegmentColumn o = (SegmentColumn) arg0;
        return column.equals(o.column);
    }

    @Override
    public int hashCode() {
        return column.hashCode();
    }
    
    
    /**
     * ColumnData class which stores relevant double array data for spectral/flux values.
     *
     */
    public static class SegmentDataColumn extends SegmentColumn {
        
        public double[] data;
        
        public SegmentDataColumn(Column column, double[] data) {
            super(column);
            this.data = data;
        }

        @Override
        public Object readValue(long i) throws IOException {
            if (i > Integer.MAX_VALUE) {
                throw new IOException("Segment data columns backed by integer indexed arrays");
            }
            return data[(int) i];
        }
        
        @Override
        public void storeValue( long i, Object val ) throws IOException {
            if (!(val instanceof Double)) {
                throw new IOException("Segment data columns only store doubles");
            }
            if (i > Integer.MAX_VALUE) {
                throw new IOException("Segment data columns backed by integer indexed arrays");
            }
            data[(int) i] = (Double) val;
        }
    }
    
    /**
     * Similar to a ConstantColumn, but with an adjustable name value.
     *
     */
    public static class NameColumn extends SegmentColumn {
        
        private String name;
        
        public NameColumn(String name) {
            super(Column.Segment_Id);
            this.name = name;
        }
        
        public void setName(String name) {
            this.name = name;
        }

        @Override
        public Object readValue(long arg0) throws IOException {
            return name;
        }
    }
    
    /**
     * Similar to a ConstantColumn, but with an adjustable name value.
     *
     */
    public static class FilterColumn extends SegmentColumn {
        
        private BitSet masked;
        
        public FilterColumn() {
            super(Column.Filtered);
            this.masked = new BitSet();
        }
        
        public void setMasked(BitSet masked) {
            if (masked == null) return;
            this.masked = masked;
        }
        
        public boolean isEmpty() {
            return masked.cardinality() == 0;
        }

        @Override
        public Object readValue(long index) throws IOException {
            return masked.get((int) index);
        }
    }
}
