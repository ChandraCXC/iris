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

import java.util.List;
import java.util.concurrent.Future;

import cfa.vo.iris.sed.stil.SegmentStarTable;
import cfa.vo.iris.units.UnitsException;
import cfa.vo.utils.Default;
import uk.ac.starlink.table.DescribedValue;
import uk.ac.starlink.table.EmptyStarTable;
import uk.ac.starlink.table.StarTable;
import uk.ac.starlink.table.WrapperStarTable;

public class IrisStarTable extends WrapperStarTable {

    private static final StarTable EMPTY_STARTABLE = new EmptyStarTable();
    
    private Future<StarTable> dataTableHolder;
    private StarTable segmentDataTable;
    private SegmentStarTable plotterTable;
    
    IrisStarTable(SegmentStarTable plotterTable, Future<StarTable> dataTableHolder) {
        this(plotterTable, EMPTY_STARTABLE);
        this.dataTableHolder = dataTableHolder;
    }
    
    public IrisStarTable(SegmentStarTable plotterTable, StarTable dataTable)
    {
        super(plotterTable);
        
        this.segmentDataTable = dataTable;
        this.plotterTable = plotterTable;
        
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
        if (!dataTableHolder.isDone()) {
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
}
