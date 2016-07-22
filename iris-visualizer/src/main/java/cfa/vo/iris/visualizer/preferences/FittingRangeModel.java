/*
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
package cfa.vo.iris.visualizer.preferences;

import java.util.Arrays;
import java.util.List;

import cfa.vo.iris.fitting.FittingRange;
import cfa.vo.iris.sed.stil.SegmentColumn;
import cfa.vo.iris.sed.stil.SegmentColumn.Column;
import cfa.vo.iris.units.UnitsException;
import cfa.vo.iris.units.UnitsManager;
import cfa.vo.iris.visualizer.plotter.ShapeType;
import cfa.vo.utils.Default;
import uk.ac.starlink.table.ColumnStarTable;
import uk.ac.starlink.table.StarTable;

public class FittingRangeModel extends LayerModel {
    
    private static final UnitsManager um = Default.getInstance().getUnitsManager();
    
    private static final String FITTING_LAYER = "Fitting Ranges";
    private static final String COLOR = "3305ff";

    public FittingRangeModel(List<FittingRange> ranges, String xunit, double yvalue) {
        super(getFittingRangeTable(ranges, xunit, yvalue));
        
        // Set Color to Blue
        this.setMarkColor(COLOR);
        this.setErrorColor(COLOR);
        
        // Actually looks okay
        this.setMarkType(ShapeType.filled_triangle_up);
        this.setSize(0);
    }

    private static StarTable getFittingRangeTable(List<FittingRange> ranges, String xunit, double yvalue) 
    {
        // Construct Y-column
        double[] yvalues = new double[ranges.size()];
        Arrays.fill(yvalues, yvalue);
        
        // Get fitting ranges from the range list, converting as we go
        double[] xvaluesLow = new double[ranges.size()];
        double[] xvaluesHigh = new double[ranges.size()];
        double[] xmiddles = new double[ranges.size()];
        for (int i=0; i<ranges.size(); i++) {
            double[] tmp = convertUnits(ranges.get(i), xunit);
            double low = tmp[0];
            double high = tmp[1];
            
            // May have switched depending on plotter units
            if (low > high) {
                low = tmp[1];
                high = tmp[0];
            }
            
            xvaluesLow[i] = low;
            xvaluesHigh[i] = high;
            xmiddles[i] = (low + high)/2;
        }
        
        ColumnStarTable ret = ColumnStarTable.makeTableWithRows(ranges.size());
        ret.addColumn(new SegmentColumn.SegmentDataColumn(Column.Spectral_Value, xmiddles));
        ret.addColumn(new SegmentColumn.SegmentDataColumn(Column.Spectral_Error_Low, xvaluesLow));
        ret.addColumn(new SegmentColumn.SegmentDataColumn(Column.Spectral_Error_High, xvaluesHigh));
        ret.addColumn(new SegmentColumn.SegmentDataColumn(Column.Flux_Value, yvalues));
        ret.setName(FITTING_LAYER);
        
        return ret;
    }
    
    private static double[] convertUnits(FittingRange r, String xunit) {
        try {
            return um.convertX(
                    new double[] {r.getStartPoint(), r.getEndPoint()},
                    r.getXUnit().getString(),
                    xunit);
        } catch (UnitsException e) {
            throw new RuntimeException(e);
        }
    }
}
