package cfa.vo.iris.visualizer.metadata;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import cfa.vo.iris.sed.ExtSed;
import cfa.vo.iris.visualizer.stil.tables.IrisStarTable;
import cfa.vo.sedlib.Point;
import cfa.vo.sedlib.Segment;
import cfa.vo.sedlib.common.SedInconsistentException;
import cfa.vo.sedlib.common.SedNoDataException;

/**
 * Used to extract a list of segments from a list of IrisStarTables and selected
 * indicies in those StarTables.
 *
 */
public class SegmentExtractor {
    
    private final List<IrisStarTable> tables;
    private final int[] selection;
    
    // Current position in iterating over the selection list
    private int index;
    
    /**
     * @param tables - List of IrisStarTables, in order
     * @param selection - Array of selected indicies in the tables, if they were stacked on top of 
     *                  each other.
     */
    public SegmentExtractor(List<IrisStarTable> tables, int[] selection) {
        
        if (tables == null) {
            throw new IllegalArgumentException("Tables cannot be null");
        }
        if (selection == null) {
            throw new IllegalArgumentException("List of points cannot be null");
        }
        
        this.index = 0;
        this.tables = tables;
        this.selection = selection;
        Arrays.sort(selection);
    }
    
    /**
     * Process and create a list of Segments with subsets of points.
     */
    public ExtSed constructSed() throws SedInconsistentException, SedNoDataException {
        
        // Iterate over each row in the selection, and make a new segment as
        // necessary.
        ExtSed sed = new ExtSed("FilterSed", false);
        
        // Current starting index of the table in our list of tables
        int tableStart = 0;
        for (IrisStarTable table : tables) {
            Segment newSegment = processTable(tableStart, table);
            if (newSegment != null) {
                sed.addSegment(newSegment);
            }
            tableStart = tableStart + (int) table.getRowCount();
        }
        
        return sed;
    }
    
    private Segment processTable(int tableStart, IrisStarTable table) {
        
        // Mark the index of the end of this table in the selection
        long end = table.getRowCount() + tableStart;
        
        Segment oldSegment = table.getPlotterDataTable().getSegment();
        List<Point> oldPoints = oldSegment.getData().getPoint();
        List<Point> newPoints = new ArrayList<>();
        
        // The index in the star table is the selection index minus the start index
        while (index < selection.length && selection[index] < end) {
            newPoints.add(oldPoints.get(selection[index] - tableStart));
            index++;
        }
        
        if (newPoints.size() == 0) {
            return null;
        }
        
        // Clone the old segment
        Segment newSegment = (Segment) oldSegment.clone();
        
        // Overwrite with new data
        newSegment.getData().setPoint(newPoints);
        
        // Ask nicely if the system will clean up old data
        System.gc();
        
        return newSegment;
    }
}
