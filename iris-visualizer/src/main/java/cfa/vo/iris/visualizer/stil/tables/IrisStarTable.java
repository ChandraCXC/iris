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


import java.util.Iterator;
import java.io.IOException;
import java.util.BitSet;
import java.util.List;
import java.util.concurrent.Future;

import cfa.vo.iris.sed.stil.SegmentStarTable;
import cfa.vo.iris.units.UnitsException;
import cfa.vo.iris.visualizer.filters.Filter;
import cfa.vo.iris.visualizer.filters.FilterSet;
import cfa.vo.iris.visualizer.filters.RowSubsetFilter;
import cfa.vo.utils.Default;
import uk.ac.starlink.table.DescribedValue;
import uk.ac.starlink.table.EmptyStarTable;
import uk.ac.starlink.table.RowSequence;
import uk.ac.starlink.table.StarTable;
import uk.ac.starlink.table.WrapperRowSequence;
import uk.ac.starlink.table.WrapperStarTable;

/**
 * The primary startable and data holder for the Iris visualization. An IrisStarTable
 * provides all necessary applications for viewing and manipulating data associated
 * with a Segment. In particular,
 * 
 * 1) Is a container for the plotterStarTable - which maintains information associated
 *  with the plotter (e.g. spectral and flux axis values).
 * 2) Maintains a pointer to a the segment's metadata star table - which maintains all
 *  metadata associated with the segment and each point in the segment.
 * 3) Provides functionality for setting spectral and flux axis units for plotting data.
 * 4) Allows filtering of data points for visualization on the plotter and for extraction
 *  to the fitting tool.
 *
 */
public class IrisStarTable extends WrapperStarTable {

    private static final StarTable EMPTY_STARTABLE = new EmptyStarTable();
    
    private Future<StarTable> dataTableHolder;
    private StarTable segmentDataTable;
    private SegmentStarTable plotterTable;
    
    private FilterSet filters;
    
    IrisStarTable(SegmentStarTable plotterTable, Future<StarTable> dataTableHolder) {
        this(plotterTable, EMPTY_STARTABLE);
        this.dataTableHolder = dataTableHolder;
    }
    
    public IrisStarTable(SegmentStarTable plotterTable, StarTable dataTable)
    {
        super(plotterTable);
        
        this.segmentDataTable = dataTable;
        this.plotterTable = plotterTable;
        this.filters = new FilterSet(this);
        
        setName(plotterTable.getName());
    }
    
    @SuppressWarnings("rawtypes")
    @Override 
    public List getParameters() {
        return segmentDataTable.getParameters();
    }
    
    @Override
    public DescribedValue getParameterByName(String parameter) {
        return segmentDataTable.getParameterByName(parameter);
    }
    
    @Override
    public void setParameter(DescribedValue value) {
        segmentDataTable.setParameter(value);
    }
    
    @Override
    public void setName(String name) {
        super.setName(name);
        plotterTable.setName(name);
        segmentDataTable.setName(name);
    }
    
    public StarTable getSegmentDataTable() {
        if (EMPTY_STARTABLE == segmentDataTable) {
            checkDataTable();
        }
        return segmentDataTable;
    }
    
    private void checkDataTable() {
        if (dataTableHolder == null || !dataTableHolder.isDone()) {
            return;
        }
        
        try {
            segmentDataTable = dataTableHolder.get();
        } catch (Exception e) {
            // TODO: Maybe show a warning message to users?
            throw new RuntimeException("Could not serialize segment", e);
        }
    }
    
    public SegmentStarTable getPlotterTable() {
        return plotterTable;
    }
    
    /**
     * Set the spectral axis units for this startable.
     * @param xunit
     * @throws UnitsException
     */
    public void setXUnits(String xunit) throws UnitsException {
        plotterTable.setSpecUnits(Default.getInstance().getUnitsManager().newXUnits(xunit));
    }
    
    /**
     * Set the flux axis units for this startable.
     * @param yunit
     * @throws UnitsException
     */
    public void setYUnits(String yunit) throws UnitsException {
        plotterTable.setFluxUnits(Default.getInstance().getUnitsManager().newYUnits(yunit));
    }
    
    /**
     *
     * @return the spectral axis units for this startable.
     */
    public String getXUnits() {
        return plotterTable.getSpecUnits().toString();
    }
    
    /**
     * 
     * @return the flux axis units for this startable.
     */
    public String getYUnits() {
        return plotterTable.getFluxUnits().toString();
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
     * Add a filter to this startable.
     * @param filter
     */
    public void addFilter(Filter filter) {
        filters.add(filter);
        plotterTable.setMasked(filters.getMasked());
    }
    
    /**
     * Remove a filter from this startable.
     * @param filter
     */
    public void removeFilter(Filter filter) {
        filters.remove(filter);
        plotterTable.setMasked(filters.getMasked());
    }
    
    /**
     * 
     * @return the set of filters that have been applied to this startable.
     */
    public FilterSet getFilters() {
        return filters;
    }
    
    /**
     * Remove all filters that have been applied to this startable.
     */
    public void clearFilters() {
        this.filters.clear();
        plotterTable.setMasked(filters.getMasked());
    }
    
    /**
     * @return the filtered set of spectral axis data values.
     */
    public double[] getSpectralDataValues() {
        return getFilteredValues(plotterTable.getSpecValues());
    }
    
    /**
     * @return the filtered set of flux axis data values.
     */
    public double[] getFluxDataValues() {
        return getFilteredValues(plotterTable.getFluxValues());
    }
    
    /*
     * Uses the BitSet masked to return a subset of data from the 
     * provided double[].
     */
    private double[] getFilteredValues(double[] data) {
        
        int rows = (int) getRowCount();
        double[] values = new double[rows];
        
        BitSet masked = filters.getMasked();
        int c = 0;
        for (int i=0; i<(int) plotterTable.getRowCount(); i++) {
            // Add only non-masked values.
            if (!masked.get(i)) {
                values[c++] = data[i];
            }
        }
        return values;
    }
    
    /**
     * We provide random access iff there are no filters applied to this
     * star table.
     */
    @Override
    public boolean isRandom() {
        return filters.isEmpty();
    }
    
    @Override
    public long getRowCount() {
        return super.getRowCount() - filters.cardinality();
    }
    
    /**
     * Returns a RowSequence relevant to the StarTable and the filters that have
     * been applied.
     * 
     */
    @Override
    public RowSequence getRowSequence() throws IOException {
        
        final BitSet mask = filters.getMasked();
        return new WrapperRowSequence( baseTable.getRowSequence() ) {
            int row = -1; // Current row in plotterTable
            int baseLength = (int) plotterTable.getRowCount();
            
            // The iterator skips over masked values
            public boolean next() throws IOException {
                row++;
                
                // If we are past the last row in the filtered table, we are done
                if (!super.next()) {
                    return false;
                }
                
                // Skip over filtered points
                while (mask.get(row)) {
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
     * Applies a set of RowSubsetFilters to a list of startables, in order. In particular, for
     * two startables <t1, t2> with 3 rows each, providing the array int[] {0,3} would filter
     * the first row in each t1 and t2.
     * 
     * @param tables
     * @param selectedRows
     */
    public static void applyFilters(List<IrisStarTable> tables, int[] selectedRows) {
        // Apply each filter to the selected star tables in order.
        int index = 0;
        
        for (IrisStarTable table : tables) {
            int length = (int) table.getPlotterTable().getRowCount();
            table.addFilter(new RowSubsetFilter(selectedRows, index, table));
            index = index + length;
        }
    }
    
    /**
     * Removes all filters from the specified star tables.
     * @param tables
     */
    public static void clearFilters(List<IrisStarTable> tables) {
        for (IrisStarTable table : tables) {
            table.clearFilters();
        }
    }
}
