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

package cfa.vo.iris.visualizer.stil;

import cfa.vo.iris.sed.stil.SegmentStarTable;
import cfa.vo.iris.units.UnitsException;
import cfa.vo.utils.Default;
import uk.ac.starlink.table.JoinStarTable;
import uk.ac.starlink.table.StarTable;

public class IrisStarTable extends JoinStarTable {
    
    private StarTable dataTable;
    private SegmentStarTable plotterTable;
    
    public IrisStarTable(SegmentStarTable plotterTable, StarTable dataTable)
    {
        super(new StarTable[] {plotterTable, dataTable});
        
        this.dataTable = dataTable;
        this.plotterTable = plotterTable;
        
        setName(plotterTable.getName());
    }
    
    public StarTable getDataTable() {
        return dataTable;
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
