package cfa.vo.iris.sed.stil;

import java.util.Map;
import java.util.WeakHashMap;

import cfa.vo.sedlib.Segment;
import uk.ac.starlink.table.StarTable;

public class SegmentStarTableAdapter implements StarTableAdapter<Segment> {
    
    private Map<Segment, StarTable> cache = new WeakHashMap<>();

    @Override
    public StarTable convertStarTable(Segment data) {
        if (cache.containsKey(data)) {
            return cache.get(data);
        }
        
        StarTable newTable = new SegmentStarTable(data);
        cache.put(data, newTable);
        return newTable;
    }

}
