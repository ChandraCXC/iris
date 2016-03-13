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
import java.util.concurrent.Future;

import cfa.vo.iris.sed.stil.SegmentStarTable;
import cfa.vo.iris.units.UnitsException;
import cfa.vo.iris.visualizer.filters.Filter;
import cfa.vo.iris.visualizer.filters.FilterSet;
import cfa.vo.utils.Default;
import uk.ac.starlink.table.DescribedValue;
import uk.ac.starlink.table.EmptyStarTable;
import uk.ac.starlink.table.RowSequence;
import uk.ac.starlink.table.StarTable;
import uk.ac.starlink.table.WrapperRowSequence;
import uk.ac.starlink.table.WrapperStarTable;

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
    
    public void setXUnits(String xunit) throws UnitsException {
        plotterTable.setSpecUnits(Default.getInstance().getUnitsManager().newXUnits(xunit));
    }
    
    public void setYUnits(String yunit) throws UnitsException {
        plotterTable.setFluxUnits(Default.getInstance().getUnitsManager().newYUnits(yunit));
    }
    
    public String getXUnits() {
        return plotterTable.getSpecUnits().toString();
    }
    
    public String getYUnits() {
        return plotterTable.getFluxUnits().toString();
    }

    public void addFilter(Filter filter) {
        filters.add(filter);
    }
    
    public void removeFilter(Filter filter) {
        filters.remove(filter);
    }
    
    public FilterSet getFilters() {
        return filters;
    }
    
    public void setFilters(FilterSet filters) {
        this.filters = filters;
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
        
        if (filters.isEmpty()) {
            return super.getRowSequence();
        }
        
        final BitSet mask = filters.getMasked();
        return new WrapperRowSequence( baseTable.getRowSequence() ) {
            int iBase = -1;
            
            // The iterator skips over masked values
            public boolean next() throws IOException {
                int leng = mask.length();
                while ( mask.get( iBase + 1 ) ) {
                    if ( iBase + 1 >= leng ) {
                        return false;
                    }
                    else {
                        super.next();
                        iBase++;
                    }
                }
                super.next();
                iBase++;
                return true;
            }
        };
    }
}
