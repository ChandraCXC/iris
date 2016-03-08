package cfa.vo.iris.visualizer.stil.tables;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import uk.ac.starlink.table.ColumnData;
import uk.ac.starlink.table.ColumnInfo;
import uk.ac.starlink.table.ColumnStarTable;
import uk.ac.starlink.table.ConstantColumn;
import uk.ac.starlink.table.StarTable;

/**
 * StarTable for producing a minimal metadata table for a list of StarTables.
 *
 */
public class ColumnMetadataStarTable extends ColumnStarTable {
    
    private List<ColumnInfo> columnInfoList;
    private List<StarTable> starTables;
    private ColumnInfoMatcher matcher;

    public ColumnMetadataStarTable(List<? extends StarTable> tables, ColumnInfoMatcher matcher) {
        super();
        this.columnInfoList = new LinkedList<>();
        this.starTables = new ArrayList<>(tables.size());
        this.matcher = matcher;

        // Iterate over all ColumnInfos and add all unique columns.
        for (StarTable table : tables) {
            starTables.add(table);
            for (int i=0; i<table.getColumnCount(); i++) {
                if (!hasMatch(table.getColumnInfo(i))) {
                    columnInfoList.add(table.getColumnInfo(i));
                }
            }
        }
        
        // Setup star table with relevant column data.
        for (ColumnInfo c : columnInfoList) {
            ColumnData data = new ConstantColumn(c, null);
            addColumn(data);
        }
    }
    
    private boolean hasMatch(ColumnInfo data) {
        for (ColumnInfo info : columnInfoList) {
            if (matcher.isCompatible(data, info)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public long getRowCount() {
        return 0;
    }
}
