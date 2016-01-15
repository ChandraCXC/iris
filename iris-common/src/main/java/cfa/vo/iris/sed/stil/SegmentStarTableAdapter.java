package cfa.vo.iris.sed.stil;

import cfa.vo.sedlib.ISegment;
import uk.ac.starlink.table.StarTable;

public class SegmentStarTableAdapter implements StarTableAdapter<ISegment> {

    @Override
    public StarTable convertStarTable(ISegment data) {
        return new SegmentStarTable(data);
    }

}
