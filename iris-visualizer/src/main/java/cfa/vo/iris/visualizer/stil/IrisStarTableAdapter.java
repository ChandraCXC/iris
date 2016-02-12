package cfa.vo.iris.visualizer.stil;

import cfa.vo.iris.sed.stil.SegmentStarTable;
import cfa.vo.iris.sed.stil.SerializingStarTableAdapter;
import cfa.vo.iris.units.UnitsException;
import cfa.vo.sedlib.Segment;
import cfa.vo.sedlib.common.SedInconsistentException;
import cfa.vo.sedlib.common.SedNoDataException;
import uk.ac.starlink.table.StarTable;

public class IrisStarTableAdapter {
    
    private SerializingStarTableAdapter serializingAdapter = new SerializingStarTableAdapter();

    public IrisStarTable convertStarTable(Segment data) {
        try {
            SegmentStarTable segTable = new SegmentStarTable(data);
            StarTable dataTable = serializingAdapter.convertStarTable(data);
            
            return new IrisStarTable(segTable, dataTable);
        } catch (SedNoDataException | SedInconsistentException | UnitsException e) {
            throw new RuntimeException(e);
        }
    }

}
