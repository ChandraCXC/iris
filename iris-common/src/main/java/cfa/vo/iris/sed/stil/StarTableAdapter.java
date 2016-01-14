package cfa.vo.iris.sed.stil;

import uk.ac.starlink.table.StarTable;

public interface StarTableAdapter<T> {
    
    /**
     * Takes a data object and returns a StarTable representation of the data for use in a 
     * stil plotter.
     */
    public StarTable convertStarTable(T data);
}
