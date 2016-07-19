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
import java.util.Arrays;
import java.util.BitSet;
import java.util.Iterator;
import java.util.List;
import java.util.TreeSet;
import java.util.UUID;

import cfa.vo.iris.sed.ExtSed;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;

import uk.ac.starlink.table.ColumnData;
import uk.ac.starlink.table.ColumnInfo;
import uk.ac.starlink.table.RandomStarTable;
import cfa.vo.iris.sed.stil.SegmentColumn.Column;
import cfa.vo.iris.sed.stil.SegmentColumn.FilterColumn;
import cfa.vo.iris.sed.stil.SegmentColumn.NameColumn;
import cfa.vo.iris.sed.stil.SegmentColumn.SegmentDataColumn;
import cfa.vo.iris.units.UnitsException;
import cfa.vo.iris.units.UnitsManager;
import cfa.vo.iris.units.XUnit;
import cfa.vo.iris.units.YUnit;
import cfa.vo.sedlib.Point;
import cfa.vo.sedlib.Segment;
import cfa.vo.sedlib.common.SedInconsistentException;
import cfa.vo.sedlib.common.SedNoDataException;
import cfa.vo.utils.Default;

/**
 * StarTable implementation based on the data contained in an SED segment - designed 
 * specifically for use in plotting applications. The Star table will have at least 2 and 
 * no more than 8 columns representing spectral and flux values and error ranges.
 *
 */
public class SegmentStarTable extends RandomStarTable {
    
    private static final UnitsManager units = Default.getInstance().getUnitsManager();
    
    private Segment segment;
    private XUnit specUnits;
    private YUnit fluxUnits;
    private YUnit originalFluxUnits;
    private final long rows;

    // Columns for name and filtered state
    final NameColumn nameColumn;
    final FilterColumn filteredColumn;
    
    // Maintain a sorted set of data columns
    TreeSet<SegmentColumn> columns;
    
    // Data holders
    private double[] specValues;
    private double[] fluxValues;
    private double[] originalFluxValues;
    private double[] originalFluxErrValues;
    private double[] originalFluxErrValuesHi;
    private double[] originalFluxErrValuesLo;
    private double[] specErrValues;
    private double[] specErrValuesLo;
    private double[] specErrValuesHi;
    private double[] fluxErrValues;
    private double[] fluxErrValuesLo;
    private double[] fluxErrValuesHi;
    private double[] modelValues;
    private double[] residualValues;
    private double[] ratioValues;

    public SegmentStarTable(double[] x, double[] y, String xUnit, String yUnit)
            throws SedNoDataException, UnitsException, SedInconsistentException {
        this(ExtSed.makeSegment(x, y, xUnit, yUnit));
    }

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
        this.originalFluxUnits = units.newYUnits(segment.getFluxAxisUnits());
        this.columns = new TreeSet<SegmentColumn>();
        
        // Name column will always be first
        this.nameColumn = new NameColumn("");
        columns.add(nameColumn);
        
        // Only add a filtered column if there is a non-empty mask
        this.filteredColumn = new FilterColumn();
        
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
        
        // Extract data
        extractSegmentDataToArrays();
        
        // Update column unit values
        updateSpecColumnUnitStrings();
        updateFluxColumnUnitStrings();
    }
    
    private void extractSegmentDataToArrays() {
        
        // Initialize all arrays to NaN
        specValues = getNanDoubleArray((int) rows);
        fluxValues = getNanDoubleArray((int) rows);
        specErrValues = getNanDoubleArray((int) rows);
        specErrValuesLo = getNanDoubleArray((int) rows);
        specErrValuesHi = getNanDoubleArray((int) rows);
        fluxErrValues = getNanDoubleArray((int) rows);
        fluxErrValuesLo = getNanDoubleArray((int) rows);
        fluxErrValuesHi = getNanDoubleArray((int) rows);
        
        // Iterate over each point in the segment and fill in the arrays
        List<Point> pointData = segment.getData().getPoint();
        for (int i=0; i<pointData.size(); i++) {
            Point p = pointData.get(i);
            specValues[i] = (double) p.getSpectralAxis().getValue().getCastValue();
            fluxValues[i] = (double) p.getFluxAxis().getValue().getCastValue();
            
            // Check for spec error values
            if (p.getSpectralAxis().isSetAccuracy()) {
                if (p.getSpectralAxis().getAccuracy().isSetStatError()) {
                    specErrValues[i] = (double) p.getSpectralAxis().getAccuracy().getStatError().getCastValue();
                }
                if (p.getSpectralAxis().getAccuracy().isSetStatErrLow()) {
                    specErrValuesLo[i] = (double) p.getSpectralAxis().getAccuracy().getStatErrLow().getCastValue();
                }
                if (p.getFluxAxis().getAccuracy().isSetStatErrHigh()) {
                    specErrValuesHi[i] = (double) p.getSpectralAxis().getAccuracy().getStatErrHigh().getCastValue();
                }
            }
            
            // Check for flux error values
            if (p.getFluxAxis().isSetAccuracy()) {
                if (p.getFluxAxis().getAccuracy().isSetStatError()) {
                    fluxErrValues[i] = (double) p.getFluxAxis().getAccuracy().getStatError().getCastValue();
                } 
                if (p.getFluxAxis().getAccuracy().isSetStatErrLow()) {
                    fluxErrValuesLo[i] = (double) p.getFluxAxis().getAccuracy().getStatErrLow().getCastValue();
                }
                if (p.getFluxAxis().getAccuracy().isSetStatErrHigh()) {
                    fluxErrValuesHi[i] = (double) p.getFluxAxis().getAccuracy().getStatErrHigh().getCastValue();
                }
            }
        }
        
        // Add spectral, flux, and original flux values
        setSpecValues(specValues);
        setFluxValues(fluxValues);
        setOriginalFluxValues(Arrays.copyOf(fluxValues, fluxValues.length)); // Copies data so these aren't the same
        
        // Add spectral error columns
        setSpecErrValues(specErrValues);
        setSpecErrValuesLo(specErrValuesLo);
        setSpecErrValuesHi(specErrValuesHi);
        
        // Add flux error values
        setFluxErrValues(fluxErrValues);
        setFluxErrValuesLo(fluxErrValuesLo);
        setFluxErrValuesHi(fluxErrValuesHi);
        
        // Set the original flux error values - store for more precise unit conversion
        setOriginalFluxErrValues(getFluxErrValues());
        setOriginalFluxErrValuesHi(getFluxErrValuesHi());
        setOriginalFluxErrValuesLo(getFluxErrValuesLo());
    }
    
    private double[] getNanDoubleArray(int rows) {
        double[] data = new double[rows];
        Arrays.fill(data, Double.NaN);
        return data;
    }
    
    @Override
    public int getColumnCount() {
        return columns.size();
    }
    
    public ColumnData getColumnData(int index) {
        if (index > columns.size()) { 
            throw new IllegalArgumentException(
                    String.format("index out of bounds (%d > %d)", index, columns.size()));
        }
        Iterator<SegmentColumn> it = columns.iterator();
        for (int i=0; i<index; i++) it.next();
        return it.next();
    }
    
    @Override
    public ColumnInfo getColumnInfo(int index) {
        return getColumnData(index).getColumnInfo();
    }
    
    @Override
    public Object getCell(long row, int col) throws IOException {
        return getColumnData(col).readValue(row);
    }

    @Override
    public long getRowCount() {
        return segment.getData().getLength();
    }
    
    @Override
    public void setName(String name) {
        super.setName(name);
        nameColumn.setName(name);
    }
    
    public void setMasked(BitSet masked) {
        filteredColumn.setMasked(masked);
        if (filteredColumn.isEmpty()) {
            columns.remove(filteredColumn);
        } else {
            columns.add(filteredColumn);
        }
    }
    
    public Segment getSegment() {
        return segment;
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
        setSpecValues(units.convertX(specValues, specUnits, newUnit));
        setSpecErrValues(units.convertX(specErrValues, specUnits, newUnit));
        setSpecErrValuesLo(units.convertX(specErrValuesLo, specUnits, newUnit));
        setSpecErrValuesHi(units.convertX(specErrValuesHi, specUnits, newUnit));
        specUnits = newUnit;
        
        // Update column unit values
        updateSpecColumnUnitStrings();
        
        // This may change Y values, so update them accordingly.
        setFluxUnits(fluxUnits);
    }
    
    private void updateSpecColumnUnitStrings() {
        for (int i=0; i<this.getColumnCount(); i++) {
            if (StringUtils.contains(getColumnInfo(i).getUtype(), "spec"))
                getColumnInfo(i).setUnitString(specUnits.toString());
        }
    }
    
    /**
     * Sets the flux axis units for this star table, which updates all relevant flux
     * valued columns in the table.
     * 
     * @param newUnit
     * @throws UnitsException
     */
    public void setFluxUnits(YUnit newUnit) throws UnitsException {
        if (specValues == null) return;
        
        // Convert units
        setFluxValues(units.convertY(getOriginalFluxValues(), specValues, originalFluxUnits, specUnits, newUnit));
        setFluxErrValues(units.convertErrors(getOriginalFluxErrValues(), getOriginalFluxValues(), specValues, originalFluxUnits, specUnits, newUnit));
        setFluxErrValuesLo(units.convertErrors(getOriginalFluxErrValuesLo(), getOriginalFluxValues(), specValues, originalFluxUnits, specUnits, newUnit));
        setFluxErrValuesHi(units.convertErrors(getOriginalFluxErrValuesHi(), getOriginalFluxValues(), specValues, originalFluxUnits, specUnits, newUnit));
        
        fluxUnits = newUnit;
        
        // Update column units
        updateFluxColumnUnitStrings();
    }
    
    private void updateFluxColumnUnitStrings() {
        for (int i=0; i<this.getColumnCount(); i++) {
            if (StringUtils.contains(getColumnInfo(i).getUtype(), "flux"))
                getColumnInfo(i).setUnitString(fluxUnits.toString());
            if (StringUtils.contains(getColumnInfo(i).getUtype(), "original"))
                getColumnInfo(i).setUnitString(originalFluxUnits.toString());
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
    

    /**
     * The original flux error values are not added as columns to the
     * StarTable. To add these to the StarTable, include
     * updateColumnValues(errors, Column.Original_Flux_*) in the
     * setOriginalFluxErrValues*() methods.
     */

    public double[] getOriginalFluxErrValues() {
        return originalFluxErrValues;
    }

    public void setOriginalFluxErrValues(double[] originalFluxErrValues) {
        this.originalFluxErrValues = originalFluxErrValues;
    }
    
    public double[] getOriginalFluxErrValuesHi() {
        return originalFluxErrValuesHi;
    }

    public void setOriginalFluxErrValuesHi(double[] originalFluxErrValuesHi) {
        this.originalFluxErrValuesHi = originalFluxErrValuesHi;
    }
    
    public double[] getOriginalFluxErrValuesLo() {
        return originalFluxErrValuesLo;
    }

    public void setOriginalFluxErrValuesLo(double[] originalFluxErrValuesLo) {
        this.originalFluxErrValuesLo = originalFluxErrValuesLo;
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
        updateColumnValues(fluxErrValuesLo, Column.Flux_Error_Low);
    }

    public double[] getFluxErrValuesHi() {
        return fluxErrValuesHi;
    }

    public void setFluxErrValuesHi(double[] fluxErrValuesHi) {
        this.fluxErrValuesHi = fluxErrValuesHi;
        updateColumnValues(fluxErrValuesHi, Column.Flux_Error_High);
    }

    public double[] getModelValues() {
        return modelValues;
    }

    public void setModelValues(double[] modelValues) {
        this.modelValues = modelValues;
        updateColumnValues(modelValues, Column.Model_Values);
    }

    public double[] getResidualValues() {
        return residualValues;
    }

    public void setResidualValues(double[] residualValues) {
        this.residualValues = residualValues;
        updateColumnValues(residualValues, Column.Residuals);
    }

    public double[] getRatioValues() {
        return ratioValues;
    }

    public void setRatioValues(double[] ratioValues) {
        this.ratioValues = ratioValues;
        updateColumnValues(ratioValues, Column.Ratios);
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
}
