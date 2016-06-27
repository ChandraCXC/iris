/**
 * Copyright 2016 Chandra X-Ray Observatory.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package cfa.vo.iris.sed.stil;

import java.io.IOException;
import java.util.BitSet;

import uk.ac.starlink.table.ColumnData;
import uk.ac.starlink.table.ColumnInfo;

/**
 * Primary class for column information in a SegmentStarTable.
 * 
 * Note that Comparable and compare are all over the Column enum. This is so
 * that the set of columns in the star table will remain in line and in order
 * with what data is available to consumers of SegmentStarTable class.
 * 
 */
public abstract class SegmentColumn extends ColumnData
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
     * Column for storing whether or not a row should be masked.
     *
     */
    public static class FilterColumn extends SegmentColumn {
        
        private BitSet masked;
        
        public FilterColumn() {
            super(Column.Masked);
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
    
    /**
     * Column info, descriptions, name, and identifiers for flux and spectral
     * values in an SED.
     *
     */
    public enum Column {
        // Columns will always appear in this order!
        Masked("Plotter filtered state", "iris.segment.filtered", Boolean.class),
        Segment_Id("Segment ID", "iris.segment.id", String.class),
        Spectral_Value("X axis values", "iris.spec.value", Double.class),
        Flux_Value("Y axis values", "iris.flux.value", Double.class),
        Original_Flux_Value("Original flux values", "iris.flux.value.original", Double.class),
        Spectral_Error("X axis error values", "iris.spec.value.error", Double.class),
        Spectral_Error_High("X axis error values", "iris.spec.value.error.high", Double.class),
        Spectral_Error_Low("X axis low error values", "iris.spec.value.error.low", Double.class),
        Flux_Error("Y axis error values", "iris.flux.value.error", Double.class),
        Flux_Error_High("Y axis error values", "iris.flux.value.error.high", Double.class),
        Flux_Error_Low("Y axis low error values", "iris.flux.value.error.low", Double.class),
        Original_Flux_Error("Original Y axis error values", "iris.flux.value.original.error", Double.class),
        Original_Flux_Error_Hi("Original Y axis upper error values", "iris.flux.value.original.error.high", Double.class),
        Original_Flux_Error_Low("Original Y axis low error values", "iris.flux.value.original.error.low", Double.class),
        Model_Values("Evaluated values from fit model", "iris.fit.values", Double.class),
        Residuals("Observed - Expected", "iris.fit.residuals", Double.class),
        Ratios("abs(Observerd - Expected)/Expected", "iris.fit.ratios", Double.class);
        
        public String description;
        public String utype;
        private ColumnInfo columnInfo;
        
        private Column(String description, String utype, Class<?> clazz) {
            this.description = description;
            this.utype = utype;
            this.columnInfo = new ColumnInfo(name(), clazz, description);
            columnInfo.setUtype(utype);
        }
        
        public ColumnInfo getColumnInfo() {
            return new ColumnInfo(columnInfo);
        }
        
        public static Column getColumn(String columnName) {
            for (Column c : Column.values()) {
                if (c.name().equals(columnName)) {
                    return c;
                }
            }
            throw new IllegalArgumentException("No such columnName: " + columnName);
        }
    }
}
