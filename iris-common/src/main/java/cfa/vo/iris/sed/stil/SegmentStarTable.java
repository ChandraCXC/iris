/**
 * Copyright (C) 2016 Smithsonian Astrophysical Observatory
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package cfa.vo.iris.sed.stil;

import java.io.IOException;
import java.util.Iterator;
import java.util.TreeSet;
import java.util.UUID;
import java.util.logging.Logger;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;

import uk.ac.starlink.table.ColumnData;
import uk.ac.starlink.table.ColumnInfo;
import uk.ac.starlink.table.RandomStarTable;
import cfa.vo.iris.units.UnitsException;
import cfa.vo.iris.units.UnitsManager;
import cfa.vo.iris.units.XUnit;
import cfa.vo.iris.units.YUnit;
import cfa.vo.sedlib.Segment;
import cfa.vo.sedlib.common.SedInconsistentException;
import cfa.vo.sedlib.common.SedNoDataException;
import cfa.vo.sedlib.common.Utypes;
import cfa.vo.utils.Default;

/**
 * StarTable implementation based on the data contained in an SED segment - designed 
 * specifically for use in plotting applications. The Star table will have at least 2 and 
 * no more than 8 columns representing spectral and flux values and error ranges.
 *
 */
public class SegmentStarTable extends RandomStarTable {
    
    private static final Logger logger = Logger.getLogger(SegmentStarTable.class.getName());
    private static final UnitsManager units = Default.getInstance().getUnitsManager();
    
    private Segment segment;
    private XUnit specUnits;
    private YUnit fluxUnits;
    private final long rows;

    // nameColumn will always be the first column in this star table
    private NameColumn nameColumn;
    
    // Maintain a sorted set of data columns
    private TreeSet<SegmentDataColumn> columns;
    
    // Data holders
    private double[] specValues;
    private double[] fluxValues;
    private double[] originalFluxValues;
    private double[] specErrValues;
    private double[] specErrValuesLo;
    private double[] specErrValuesHi;
    private double[] fluxErrValues;
    private double[] fluxErrValuesLo;
    private double[] fluxErrValuesHi;
    
    public SegmentStarTable(Segment segment) 
            throws SedNoDataException, UnitsException, SedInconsistentException {
        this(segment, null);
    }
    
    public SegmentStarTable(Segment segment, String id) 
            throws UnitsException, SedNoDataException, SedInconsistentException 
    {
        this.segment = segment;
        this.specUnits = units.newXUnits(segment.getSpectralAxisUnits());
        this.fluxUnits = units.newYUnits(segment.getFluxAxisUnits());
        this.columns = new TreeSet<SegmentDataColumn>();
        
        this.nameColumn = new NameColumn(null);
        
        // Look for a name in the segment if we are not given one
        if (StringUtils.isBlank(id)) {
            if (segment.isSetTarget() && segment.getTarget().isSetName()) {
                id = segment.getTarget().getName().getValue();
            } else {
                id = UUID.randomUUID().toString();
            }
        }
        
        // Set star tabel name
        setName(id);
        
        // Set number of rows for this table
        rows = segment.getLength();
        
        // Add spectral, flux, and original flux values
        setSpecValues(segment.getSpectralAxisValues());
        setFluxValues(segment.getFluxAxisValues());
        setOriginalFluxValues(segment.getFluxAxisValues()); // Copies data so these aren't the same
        
        // Try to add flux error values
        setFluxErrValues((double[]) getDataFromSegment(Utypes.SEG_DATA_FLUXAXIS_ACC_STATERR));
        setFluxErrValuesHi((double[]) getDataFromSegment(Utypes.SEG_DATA_FLUXAXIS_ACC_STATERRHIGH));
        setFluxErrValuesLo((double[]) getDataFromSegment(Utypes.SEG_DATA_FLUXAXIS_ACC_STATERRLOW));
        
        // Try to add spectral error columns
        setSpecErrValues((double[]) getDataFromSegment(Utypes.SEG_DATA_SPECTRALAXIS_ACC_STATERR));
        setSpecErrValuesHi((double[]) getDataFromSegment(Utypes.SEG_DATA_SPECTRALAXIS_ACC_STATERRHIGH));
        setSpecErrValuesLo((double[]) getDataFromSegment(Utypes.SEG_DATA_SPECTRALAXIS_ACC_STATERRLOW));
    }

    @Override
    public int getColumnCount() {
        return 1 + columns.size();
    }
    
    public ColumnData getColumnData(int index) {
        if (index == 0) {
            return nameColumn;
        }
        
        Iterator<SegmentDataColumn> it = columns.iterator();
        for (int i=0; i<index-1; i++) it.next();
        return it.next();
    }
    

    @Override
    public ColumnInfo getColumnInfo(int index) {
        return getColumnData(index).getColumnInfo();
    }

    @Override
    public long getRowCount() {
        return segment.getData().getLength();
    }
    
    @Override
    public Object getCell( long lrow, int icol ) throws IOException {
        return getColumnData(icol).readValue( lrow );
    }
    
    @Override
    public void setName(String name) {
        super.setName(name);
        nameColumn.setName(name);
    }
    
    /**
     * Sets the spectral axis units for this star table, which updates all spectral
     * valued columns in the table. As this may also alter the flux axis units,
     * this method calls to update flux values as well.
     * 
     * @param newUnit
     * @throws UnitsException
     */
    public void setSpecUnits(XUnit newUnit) throws UnitsException {
        if (newUnit.equals(this.specUnits)) return;
        
        setSpecValues(units.convertX(specValues, specUnits, newUnit));
        setSpecErrValues(units.convertX(specErrValues, specUnits, newUnit));
        setSpecErrValuesLo(units.convertX(specErrValuesLo, specUnits, newUnit));
        setSpecErrValuesHi(units.convertX(specErrValuesHi, specUnits, newUnit));
        specUnits = newUnit;
        
        // This may change Y values, so update them accordingly.
        setFluxUnits(fluxUnits);
        
        // Update column unit values
        for (int i=0; i<this.getColumnCount(); i++) {
            if (StringUtils.contains(getColumnInfo(i).getUtype(), "spec"))
                getColumnInfo(i).setUnitString(newUnit.toString());
        }
    }
    
    /**
     * Sets the flux axis units for this star table, which updates all relevat flux
     * valued columns in the table.
     * 
     * @param newUnit
     * @throws UnitsException
     */
    public void setFluxUnits(YUnit newUnit) throws UnitsException {
        if (specValues == null) return;
        
        // Convert units
        setFluxValues(units.convertY(fluxValues, specValues, fluxUnits, specUnits, newUnit));
        setOriginalFluxValues(units.convertY(originalFluxValues, specValues, fluxUnits, specUnits, newUnit));
        setFluxErrValues(units.convertY(fluxErrValues, specValues, fluxUnits, specUnits, newUnit));
        setFluxErrValuesLo(units.convertY(fluxErrValuesLo, specValues, fluxUnits, specUnits, newUnit));
        setFluxErrValuesHi(units.convertY(fluxErrValuesLo, specValues, fluxUnits, specUnits, newUnit));
        
        // Update values
        fluxUnits = newUnit;
        for (int i=0; i<this.getColumnCount(); i++) {
            if (StringUtils.contains(getColumnInfo(i).getUtype(), "flux"))
                getColumnInfo(i).setUnitString(newUnit.toString());
        }
    }

    /**
     * @return XUnit for this star table
     */
    public XUnit getSpecUnits() {
        return specUnits;
    }

    /**
     * @return YUnit for this star table
     */
    public YUnit getFluxUnits() {
        return fluxUnits;
    }

    /*
     * 
     * Getters and setters for data columns.
     * 
     */
    
    public double[] getSpecValues() {
        return specValues;
    }
    
    public void setSpecValues(double[] specValues) {
        this.specValues = specValues;
        updateColumnValues(specValues, Column.Spectral_Value);
    }

    public double[] getFluxValues() {
        return fluxValues;
    }

    public void setFluxValues(double[] fluxValues) {
        this.fluxValues = fluxValues;
        updateColumnValues(fluxValues, Column.Flux_Value);
    }

    public double[] getOriginalFluxValues() {
        return originalFluxValues;
    }

    public void setOriginalFluxValues(double[] originalFluxValues) {
        this.originalFluxValues = originalFluxValues;
        updateColumnValues(fluxValues, Column.Original_Flux_Value);
    }

    public double[] getSpecErrValues() {
        return specErrValues;
    }

    public void setSpecErrValues(double[] specErrValues) {
        this.specErrValues = specErrValues;
        updateColumnValues(specErrValues, Column.Spectral_Error);
    }

    public double[] getSpecErrValuesLo() {
        return specErrValuesLo;
    }

    public void setSpecErrValuesLo(double[] specErrValuesLo) {
        this.specErrValuesLo = specErrValuesLo;
        updateColumnValues(specErrValuesLo, Column.Spectral_Error_Low);
    }

    public double[] getSpecErrValuesHi() {
        return specErrValuesHi;
    }

    public void setSpecErrValuesHi(double[] specErrValuesHi) {
        this.specErrValuesHi = specErrValuesHi;
        updateColumnValues(specErrValuesHi, Column.Spectral_Error_High);
    }

    public double[] getFluxErrValues() {
        return fluxErrValues;
    }

    public void setFluxErrValues(double[] fluxErrValues) {
        this.fluxErrValues = fluxErrValues;
        updateColumnValues(fluxErrValues, Column.Flux_Error);
    }

    public double[] getFluxErrValuesLo() {
        return fluxErrValuesLo;
    }

    public void setFluxErrValuesLo(double[] fluxErrValuesLo) {
        this.fluxErrValuesLo = fluxErrValuesLo;
        updateColumnValues(fluxErrValuesLo, Column.FLux_Error_Low);
    }

    public double[] getFluxErrValuesHi() {
        return fluxErrValuesHi;
    }

    public void setFluxErrValuesHi(double[] fluxErrValuesHi) {
        this.fluxErrValuesHi = fluxErrValuesHi;
        updateColumnValues(fluxErrValuesHi, Column.Flux_Error_High);
    }

    /**
     * Attempts to extract data with the specified Utype from the segment.
     * 
     */
    private double[] getDataFromSegment(int utype) {
        double[] ret = null;
        try {
            ret = (double[]) segment.getData().getDataValues(utype);
        } catch (SedNoDataException | SedInconsistentException e) {
            logger.fine("Cannot read data for segment for utype: " + e.getLocalizedMessage());
        }
        return ret;
    }
    
    /**
     * Sets, adds, or removes the relevant data from this star table.
     * 
     */
    private void updateColumnValues(double[] data, Column column) {
        SegmentDataColumn newColumn = new SegmentDataColumn(column, data);
        columns.remove(newColumn);
        
        // If there is nothing in the dataset then remove this column
        if (isEmpty(data)) {
            return;
        }
        
        if (data.length != this.rows) {
            throw new IllegalArgumentException("Data must have equal length to existing segments");
        }
        columns.add(newColumn);
    }

    /**
     * Returns whether or not a double array is empty or contains all NaNs.
     * 
     */
    private static final boolean isEmpty(double[] data) {
        boolean ret = ArrayUtils.isEmpty(data);
        
        if (ret) return ret;
        
        for (int i=0; i<data.length; i++) {
            if (!Double.isNaN(data[i])) return false;
        }
        
        return true;
    }
    
    /**
     * Column info, descriptions, name, and identifiers for flux and spectral
     * values in an SED.
     *
     */
    public enum Column {
        // Columns will always appear in this order!
        Segment_Id("Segment ID", "iris.segment.id", String.class),
        Spectral_Value("X axis values", "iris.spec.value", Double.class),
        Flux_Value("Y axis values", "iris.flux.value", Double.class),
        Original_Flux_Value("Original flux values", "iris.flux.value.original", Double.class),
        Spectral_Error("X axis error values", "iris.spec.value.error", Double.class),
        Spectral_Error_High("X axis error values", "iris.spec.value.error.high", Double.class),
        Spectral_Error_Low("X axis low error values", "iris.spec.value.error.low", Double.class),
        Flux_Error("Y axis error values", "iris.flux.value.error", Double.class),
        Flux_Error_High("Y axis error values", "iris.flux.value.error.high", Double.class),
        FLux_Error_Low("Y axis low error values", "iris.flux.value.error.low", Double.class);
        
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
    /**
     * ColumnData class which stores relevant double array data for spectral/flux values.
     * Note that comperable and compare are all over the Column enum. This is so that the 
     * set of columns in the star table will remain in line and in order with what data is
     * available to consumers of this class.
     *
     */
    private static class SegmentDataColumn extends ColumnData implements Comparable<SegmentDataColumn> {
        
        public final Column column;
        public double[] data;
        
        public SegmentDataColumn(Column column, double[] data) {
            this.column = column;
            this.data = data;
            this.setColumnInfo(column.getColumnInfo());
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

        @Override
        public int compareTo(SegmentDataColumn arg0) {
            SegmentDataColumn o = (SegmentDataColumn) arg0;
            return column.compareTo(o.column);
        }
        
        @Override
        public boolean equals(Object arg0) {
            SegmentDataColumn o = (SegmentDataColumn) arg0;
            return column.equals(o.column);
        }
        
        @Override
        public int hashCode() {
            return column.hashCode();
        }
    }
    
    /**
     * Similar to a ConstantColumn, but with an adjustable name value.
     *
     */
    private static class NameColumn extends ColumnData {
        
        private String name;
        
        public NameColumn(String name) {
            super(Column.Segment_Id.getColumnInfo());
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
}
