package cfa.vo.iris.visualizer.metadata;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import cfa.vo.iris.visualizer.stil.tables.IrisStarTable;
import cfa.vo.sedlib.ArrayOfPoint;
import cfa.vo.sedlib.Characterization;
import cfa.vo.sedlib.CoordSys;
import cfa.vo.sedlib.Curation;
import cfa.vo.sedlib.DataID;
import cfa.vo.sedlib.DerivedData;
import cfa.vo.sedlib.Point;
import cfa.vo.sedlib.Segment;
import cfa.vo.sedlib.Target;
import cfa.vo.sedlib.TextParam;

/**
 * Used to extract a list of segments from a list of IrisStarTables and selected
 * indicies in those StarTables.
 *
 */
public class SegmentExtractor {
    
    private List<IrisStarTable> tables;
    private int[] selection;
    
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
    public List<Segment> getSegments() {
        // Iterate over each row in the selection, and make a new segment as
        // necessary.
        
        List<Segment> newSegments = new LinkedList<>();
        
        // Current starting index of the table in our list of tables
        int tableStart = 0;
        for (IrisStarTable table : tables) {
            newSegments.add(processTable(tableStart, table));
            tableStart = tableStart + (int) table.getRowCount();
        }
        
        return newSegments;
    }
    
    private Segment processTable(int tableStart, IrisStarTable table) {
        
        // Mark the index of the end of this table in the selection
        long end = table.getRowCount() + tableStart;
        
        Segment oldSegment = table.getPlotterTable().getSegment();
        List<Point> oldPoints = oldSegment.getData().getPoint();
        List<Point> newPoints = new ArrayList<>();
        
        while (index < selection.length && selection[index] < end) {
            newPoints.add(oldPoints.get(selection[index] - tableStart));
            index++;
        }
        
        Segment newSegment = copySegmentMetadata(oldSegment, newPoints);
        return newSegment;
    }
    
    /**
     * Copies everything except for the point data into a new segment. ArrayOfPoints
     * may be huge, so this is more efficient than cloning.
     * 
     */
    private Segment copySegmentMetadata(Segment oldSegment, List<Point> newPoints) {
        
        Segment segment = new Segment();
        
        if (oldSegment.isSetTarget())
            segment.setTarget((Target) oldSegment.getTarget().clone());
        if (oldSegment.isSetChar())
            segment.setChar((Characterization) oldSegment.getChar().clone());
        if (oldSegment.isSetCoordSys())
            segment.setCoordSys((CoordSys)oldSegment.getCoordSys().clone());
        if (oldSegment.isSetCuration())
            segment.setCuration((Curation)oldSegment.getCuration().clone());
        if (oldSegment.isSetDataID())
            segment.setDataID((DataID)oldSegment.getDataID().clone());
        if (oldSegment.isSetDerived())
            segment.setDerived((DerivedData)oldSegment.createDerived().clone());
        if (oldSegment.isSetType())
            segment.setType((TextParam)oldSegment.getType().clone());
        if (oldSegment.isSetTimeSI())
            segment.setTimeSI((TextParam)oldSegment.getTimeSI().clone());
        if (oldSegment.isSetSpectralSI())
            segment.setSpectralSI((TextParam)oldSegment.getSpectralSI().clone());
        if (oldSegment.isSetFluxSI())
            segment.setFluxSI((TextParam)oldSegment.getFluxSI().clone());
        
        ArrayOfPoint newData = new ArrayOfPoint();
        newData.setPoint(newPoints);
        segment.setData(newData);
        
        return segment;
    }

}
