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
