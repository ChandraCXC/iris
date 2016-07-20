package cfa.vo.iris.visualizer.preferences;

import java.util.Arrays;
import java.util.List;

import cfa.vo.iris.fitting.FittingRange;
import cfa.vo.iris.sed.stil.SegmentColumn;
import cfa.vo.iris.sed.stil.SegmentColumn.Column;
import cfa.vo.iris.units.UnitsException;
import cfa.vo.iris.units.UnitsManager;
import cfa.vo.utils.Default;
import uk.ac.starlink.table.ColumnStarTable;
import uk.ac.starlink.table.StarTable;

public class FittingRangeModel extends LayerModel {
    
    private static final UnitsManager um = Default.getInstance().getUnitsManager();
    private static final String FITTING_LAYER = "Fitting Ranges";

    public FittingRangeModel(List<FittingRange> ranges, String xunit, double yvalue) {
        super(getFittingRangeTable(ranges, xunit, yvalue));
        this.setShowMarks(false);
        this.setErrorColor("Red");
    }

    private static StarTable getFittingRangeTable(List<FittingRange> ranges, String xunit, double yvalue) 
    {
        // Construct Y-column
        double[] yvalues = new double[ranges.size()];
        Arrays.fill(yvalues, yvalue);
        
        // Get fitting ranges from the range list, converting as we go
        double[] xvaluesLow = new double[ranges.size()];
        double[] xvaluesHigh = new double[ranges.size()];
        for (int i=0; i<ranges.size(); i++) {
            double[] tmp = convertUnits(ranges.get(i), xunit);
            xvaluesLow[i] = tmp[0];
            xvaluesHigh[i] = tmp[1];
        }
        
        ColumnStarTable ret = ColumnStarTable.makeTableWithRows(ranges.size());
        ret.addColumn(new SegmentColumn.SegmentDataColumn(Column.Spectral_Error_Low, xvaluesLow));
        ret.addColumn(new SegmentColumn.SegmentDataColumn(Column.Spectral_Error_High, xvaluesHigh));
        ret.addColumn(new SegmentColumn.SegmentDataColumn(Column.Spectral_Error_High, yvalues));
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
