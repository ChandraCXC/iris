package cfa.vo.iris.visualizer.stil.tables;

import cfa.vo.iris.sed.stil.SegmentColumn;
import cfa.vo.iris.sed.stil.SegmentColumn.Column;
import uk.ac.starlink.table.ColumnData;
import uk.ac.starlink.table.ColumnInfo;

/**
 * ColumnInfoMatcher specifically for SegmentStarTable columns
 * and information.
 *
 */
public class SegmentColumnInfoMatcher extends ColumnInfoMatcher {

    @Override
    public boolean isCompatible(ColumnInfo c1, ColumnInfo c2) {
        Column column1 = getColumnForInfo(c1);
        Column column2 = getColumnForInfo(c2);
        
        return column1.equals(column2);
    }
    
    @Override
    public ColumnData getDefaultValueColumn(ColumnInfo c) {
        Column column = getColumnForInfo(c);
        if (Column.Masked.equals(column)) {
            return new SegmentColumn.FilterColumn();
        }
        
        return super.getDefaultValueColumn(c);
    }
    
    private Column getColumnForInfo(ColumnInfo c) {
        try {
            return Column.getColumn(c.getName());
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Cannot compare non-Column enum ColumnInfo objects", e);
        }
    }
}
