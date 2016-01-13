package cfa.vo.iris.visualizer.stil;

import cfa.vo.iris.sed.stil.SegmentStarTableWrapper;
import cfa.vo.sedlib.ISegment;
import uk.ac.starlink.table.StarTable;

public class SegmentStarTableAdapter implements StarTableAdapter<ISegment> {

    @Override
    public StarTable convertStarTable(ISegment data) {
        return new SegmentStarTableWrapper(data);
    }

}
