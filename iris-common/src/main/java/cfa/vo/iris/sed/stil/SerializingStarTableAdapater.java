package cfa.vo.iris.sed.stil;

import java.io.ByteArrayOutputStream;

import cfa.vo.sedlib.Sed;
import cfa.vo.sedlib.Segment;
import cfa.vo.sedlib.io.VOTableSerializer;
import uk.ac.starlink.table.StarTable;
import uk.ac.starlink.table.StoragePolicy;
import uk.ac.starlink.table.TableSequence;
import uk.ac.starlink.util.ByteArrayDataSource;
import uk.ac.starlink.votable.VOTableBuilder;


public class SerializingStarTableAdapater implements StarTableAdapter<Segment> {

    @Override
    public StarTable convertStarTable(Segment data) {
        try {
            Sed tmp = new Sed();
            tmp.addSegment(data);
            ByteArrayOutputStream os = toVOTable(tmp);
            StarTable table = convertOSStream(os);
            return table;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    
    private ByteArrayOutputStream toVOTable(Sed sed) throws Exception {
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        
        VOTableSerializer serializer = new VOTableSerializer();
        serializer.serialize(os, sed);
        
        return os;
    }

    private StarTable convertOSStream(ByteArrayOutputStream os) throws Exception {
        ByteArrayDataSource ds = new ByteArrayDataSource("Iris DS", os.toByteArray());
        VOTableBuilder votBuilder = new VOTableBuilder();
        TableSequence seq = votBuilder.makeStarTables(ds, StoragePolicy.getDefaultPolicy());
        return seq.nextTable();
    }
}

