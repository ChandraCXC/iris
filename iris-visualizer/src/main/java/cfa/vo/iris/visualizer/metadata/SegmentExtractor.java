package cfa.vo.iris.visualizer.metadata;

import java.util.ArrayList;
import java.util.List;

import cfa.vo.iris.sed.ExtSed;
import cfa.vo.iris.visualizer.stil.tables.IrisStarTable;
import cfa.vo.sedlib.Point;
import cfa.vo.sedlib.Segment;
import cfa.vo.sedlib.common.SedInconsistentException;
import cfa.vo.sedlib.common.SedNoDataException;

/**
 * Used to extract a list of segments from a list of IrisStarTables and selected
 * indexes in those StarTables.
 *
 */
public class SegmentExtractor {
    
    private final int[][]  selectedRows;
    private final IrisStarTable[] selectedTables;
    
    /**
     * @param tables - List of IrisStarTables, in order
     * @param selection - Array of selected ine in the tables, if they were stacked on top of 
     *                  each other.
     */
    public SegmentExtractor(IrisStarTable[] tables, int[][] selection) {
        
        if (tables == null || selection == null) {
            throw new IllegalArgumentException("Neither tables nor selection can be null");
        }
        
        if (tables.length != selection.length) {
            throw new IllegalArgumentException("Arrays must have equal length");
        }
        
        this.selectedTables = tables;
        this.selectedRows = selection;
    }
    

    
    /**
     * Process and create a list of Segments with subsets of points.
     */
    public ExtSed constructSed() throws SedInconsistentException, SedNoDataException {
        
        ExtSed sed = new ExtSed("FilterSed", false);

        // Iterate over each row in the selection, and make a new segment as
        // necessary.
        for (int i=0; i<selectedTables.length; i++) {
            int[] rows = selectedRows[i];
            IrisStarTable table = selectedTables[i];
            
            Segment newSegment = processTable(table, rows);
            
            if (newSegment != null) {
                sed.addSegment(newSegment);
            }
        }
        
        // TODO: Investigate which fields need to be checked and adjusted in the
        // new sed. Then either uncomment this or set them elsewhere.
        // sed.checkChar();
        
        return sed;
    }
    
    private Segment processTable(IrisStarTable table, int[] rows) {
        
        // If there are no selected rows skip this segment
        if (rows.length == 0) {
            return null;
        }
        
        Segment originalSegment = table.getPlotterDataTable().getSegment();
        List<Point> oldPoints = originalSegment.getData().getPoint();
        List<Point> newPoints = new ArrayList<>(rows.length);
        
        // Add selected datapoints to the new points list
        for (int i : rows) {
            newPoints.add(oldPoints.get(i));
        }
        
        // Clone the old segment
        Segment newSegment = (Segment) originalSegment.clone();
        
        // Replace point data
        newSegment.getData().setPoint(newPoints);
        
        // Ask nicely for the system to clean up old data
        System.gc();
        
        return newSegment;
    }
}
