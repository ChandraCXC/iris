package cfa.vo.iris.visualizer.stil.tables;

import java.io.IOException;
import java.util.List;

import uk.ac.starlink.table.ConcatStarTable;
import uk.ac.starlink.table.StarTable;
import uk.ac.starlink.table.WrapperStarTable;

/**
 * Extension of a ConcatStarTable that supports stacking of any StarTable, regardless
 * of ColumnInfo compatibility.
 *
 */
public class StackedStarTable extends WrapperStarTable {
    
    private ColumnMetadataStarTable metadataTable;
    private ColumnMappingStarTable[] dataTables;

    public StackedStarTable(List<? extends StarTable> tables, ColumnInfoMatcher matcher)
    {
        super(null);
        
        this.metadataTable = new ColumnMetadataStarTable(tables, matcher);
        this.dataTables = new ColumnMappingStarTable[tables.size()];
        
        for (int i=0; i<tables.size(); i++) {
            dataTables[i] = new ColumnMappingStarTable(tables.get(i), metadataTable, matcher);
        }
        
        try {
            this.baseTable = new ConcatStarTable(metadataTable, dataTables);
        } catch (IOException e) {
            throw new IllegalArgumentException(e);
        }
    }
}
