package cfa.vo.iris.visualizer.stil.tables;

import uk.ac.starlink.table.ColumnInfo;

/**
 * Interface for compatibility checks between columns of a StarTable.
 *
 */
public interface ColumnInfoMatcher {
    public boolean isCompatible(ColumnInfo c1, ColumnInfo c2);
}
