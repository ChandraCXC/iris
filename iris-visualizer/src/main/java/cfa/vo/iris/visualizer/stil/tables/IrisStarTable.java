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

package cfa.vo.iris.visualizer.stil.tables;


import java.io.IOException;
import java.util.BitSet;
import java.util.List;

import cfa.vo.iris.sed.stil.IrisDataStarTable;
import cfa.vo.iris.sed.stil.SegmentStarTable;
import cfa.vo.iris.units.UnitsException;
import cfa.vo.iris.visualizer.masks.Mask;
import cfa.vo.iris.visualizer.masks.RowSubsetMask;
import cfa.vo.utils.Default;
import uk.ac.starlink.table.DescribedValue;
import uk.ac.starlink.table.RowSequence;
import uk.ac.starlink.table.StarTable;
import uk.ac.starlink.table.WrapperRowSequence;
import uk.ac.starlink.table.WrapperStarTable;

/**
 * The primary startable and data holder for the Iris visualization. An IrisStarTable
 * provides all necessary applications for viewing and manipulating data associated
 * with a Segment. In particular,
 * 
 * 1) Is a container for the plotterDataTable - which maintains information associated
 *  with the plotter (e.g. spectral and flux axis values).
 * 2) Maintains a pointer to a the segment's metadata star table - which maintains all
 *  metadata associated with the segment and each point in the segment.
 * 3) Provides functionality for setting spectral and flux axis units for plotting data.
 * 4) Allows filtering of data points for visualization on the plotter and for extraction
 *  to the fitting tool.
 *
 */
public class IrisStarTable extends WrapperStarTable implements IrisDataStarTable {
    
    private StarTable segmentMetadataTable;
    private SegmentStarTable plotterDataTable;
    
    private Mask mask;
    
    IrisStarTable(SegmentStarTable plotterTable) {
        this(plotterTable, null);
    }
    
    public IrisStarTable(SegmentStarTable plotterTable, StarTable dataTable)
    {
        super(plotterTable);
        
        this.segmentMetadataTable = dataTable;
        this.plotterDataTable = plotterTable;
        this.mask = new RowSubsetMask(new int[0], this);
        
        setName(plotterTable.getName());
    }
    
    @SuppressWarnings("rawtypes")
    @Override 
    public List getParameters() {
        return segmentMetadataTable.getParameters();
    }
    
    @Override
    public DescribedValue getParameterByName(String parameter) {
        return segmentMetadataTable.getParameterByName(parameter);
    }
    
    @Override
    public void setParameter(DescribedValue value) {
        segmentMetadataTable.setParameter(value);
    }
    
    @Override
    public void setName(String name) {
        super.setName(name);
        plotterDataTable.setName(name);
        segmentMetadataTable.setName(name);
    }

    @Override
    public double[] getSpecValues() {
        return getFilteredValues(plotterDataTable.getSpecValues());
    }

    @Override
    public double[] getFluxValues() {
        return getFilteredValues(plotterDataTable.getFluxValues());
    }

    @Override
    public double[] getSpecErrValues() {
        return getFilteredValues(plotterDataTable.getSpecErrValues());
    }

    @Override
    public double[] getSpecErrValuesLo() {
        return getFilteredValues(plotterDataTable.getSpecErrValuesLo());
    }

    @Override
    public double[] getSpecErrValuesHi() {
        return getFilteredValues(plotterDataTable.getSpecErrValuesHi());
    }

    @Override
    public double[] getFluxErrValues() {
        return getFilteredValues(plotterDataTable.getFluxErrValues());
    }

    @Override
    public double[] getFluxErrValuesLo() {
        return getFilteredValues(plotterDataTable.getFluxErrValuesLo());
    }

    @Override
    public double[] getFluxErrValuesHi() {
        return getFilteredValues(plotterDataTable.getFluxErrValuesHi());
    }
    
    /**
     * Uses the BitSet mask to return a subset of data from the 
     * provided double[].
     */
    private double[] getFilteredValues(double[] data) {
        
        int rows = (int) getRowCount();
        double[] values = new double[rows];
        
        BitSet masked = mask.getMaskedRows(this);
        int c = 0;
        for (int i=0; i<(int) plotterDataTable.getRowCount(); i++) {
            // Add only non-masked values.
            if (!masked.get(i)) {
                values[c++] = data[i];
            }
        }
        return values;
    }
    
    /**
     * Updates the segment data table for async serialization.
     */
    void setSegmentMetadataTable(StarTable metadataTable) {
        this.segmentMetadataTable = metadataTable;
    }
    
    public StarTable getSegmentMetadataTable() {
        return segmentMetadataTable;
    }
    
    public SegmentStarTable getPlotterDataTable() {
        return plotterDataTable;
    }
    
    /**
     * Set the spectral axis units for this startable.
     * @param xunit
     * @throws UnitsException
     */
    public void setXUnits(String xunit) throws UnitsException {
        plotterDataTable.setSpecUnits(Default.getInstance().getUnitsManager().newXUnits(xunit));
    }
    
    /**
     * Set the flux axis units for this startable.
     * @param yunit
     * @throws UnitsException
     */
    public void setYUnits(String yunit) throws UnitsException {
        plotterDataTable.setFluxUnits(Default.getInstance().getUnitsManager().newYUnits(yunit));
    }
    
    /**
     *
     * @return the spectral axis units for this startable.
     */
    public String getXUnits() {
        return plotterDataTable.getSpecUnits().toString();
    }
    
    /**
     * 
     * @return the flux axis units for this startable.
     */
    public String getYUnits() {
        return plotterDataTable.getFluxUnits().toString();
    }
    
    /**
     * Returns the row start index for the specified table in the list of StarTables.
     */
    public static int getTableStartIndex(List<IrisStarTable> tables, IrisStarTable table) {
        // TODO: Handle masking when we merge this.
        int index = 0;
        
        for (IrisStarTable t : tables) {
            if (t.equals(table)) {
                return index;
            }
            index = index + ((int) t.getRowCount());
        }
        
        return -1;
    }
    
    /**
     * @return BitSet of masked rows in the table.
     */
    public BitSet getMasked() {
        return mask.getMaskedRows(this);
    }

    /**
     * Mask rows from this StarTable.
     * @param filter
     */
    public void applyMasks(int[] rows) {
        mask.applyMasks(rows);
        plotterDataTable.setMasked(mask.getMaskedRows(this));
    }
    
    /**
     * Remove the mask from rows on this StarTable.
     * @param filter
     */
    public void clearMasks(int[] rows) {
        mask.clearMasks(rows);
        plotterDataTable.setMasked(mask.getMaskedRows(this));
    }
    
    /**
     * Remove the mask from rows on this StarTable.
     * @param filter
     */
    public void clearMasks() {
        mask = new RowSubsetMask(new int[0], this);
        plotterDataTable.setMasked(mask.getMaskedRows(this));
    }
    
    /**
     * We provide random access if and only if there are no filters applied to this
     * star table.
     */
    @Override
    public boolean isRandom() {
        return mask.cardinality() == 0;
    }
    
    @Override
    public long getRowCount() {
        return super.getRowCount() - mask.cardinality();
    }

    /**
     * Returns the corresponding row in the basetable taking into account the masks
     * that may be applied to this table. The return amount is essentially
     * 
     * irow + (#rows filtered less than irow)
     * 
     * Will return -1 if irow > this.getRowCount()
     * 
     */
    public int getBaseTableRow(int irow) {
        // Cannot be past the last index
        if (irow >= this.getRowCount()) {
            return -1;
        }
        
        // If there is no mask the mapping is simple
        if (isRandom()) {
            return irow;
        }
        
        // Otherwise we have to count to the current row
        final BitSet masked = mask.getMaskedRows(this);
        
        // 0th row is first clear bit
        int trueIndex = masked.nextClearBit(0);
        for (int i=0; i<irow; i++) {
            trueIndex = masked.nextClearBit(trueIndex+1);
        }
        
        return trueIndex;
    }
    
    /**
     * Returns a RowSequence relevant to the StarTable and the filters that have
     * been applied.
     * 
     */
    @Override
    public RowSequence getRowSequence() throws IOException {
        
        final BitSet masked = mask.getMaskedRows(this);
        return new WrapperRowSequence( baseTable.getRowSequence() ) {
            int row = -1; // Current row in plotterTable
            int baseLength = (int) plotterDataTable.getRowCount();
            
            // The iterator skips over masked values
            public boolean next() throws IOException {
                row++;
                
                // If we are past the last row in the filtered table, we are done
                if (!super.next()) {
                    return false;
                }
                
                // Skip over filtered points
                while (masked.get(row)) {
                    // If we are past the last row in the actual table then there 
                    // are no more points
                    if (row + 1 >= baseLength) {
                        return false;
                    }
                    
                    super.next();
                    row++;
                }
                return true;
            }
        };
    }
    
    /**
     * Removes all filters from the specified star tables.
     * @param tables
     */
    public static void clearAllMasks(List<IrisStarTable> tables) {
        for (IrisStarTable table : tables) {
            table.clearMasks();
        }
    }
}
