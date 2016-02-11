package cfa.vo.iris.visualizer.stil;

import cfa.vo.iris.sed.stil.SegmentStarTable;
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
}
