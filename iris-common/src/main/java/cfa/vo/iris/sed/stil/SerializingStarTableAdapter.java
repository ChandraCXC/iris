/**
 * Copyright (C) 2016 Smithsonian Astrophysical Observatory
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package cfa.vo.iris.sed.stil;

import java.io.ByteArrayOutputStream;
import java.util.Iterator;

import cfa.vo.sedlib.Sed;
import cfa.vo.sedlib.Segment;
import cfa.vo.sedlib.io.VOTableSerializer;
import uk.ac.starlink.table.DescribedValue;
import uk.ac.starlink.table.StarTable;
import uk.ac.starlink.table.StoragePolicy;
import uk.ac.starlink.table.TableSequence;
import uk.ac.starlink.util.ByteArrayDataSource;
import uk.ac.starlink.votable.VOTableBuilder;


public class SerializingStarTableAdapter implements StarTableAdapter<Segment> {

    @Override
    public StarTable convertStarTable(Segment data) {
        Sed tmp = new Sed();
        StarTable table;
        
        try {
            tmp.addSegment(data);
            ByteArrayOutputStream os = toVOTable(tmp);
            table = convertOSStream(os);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        
        cleanParameters(table);
        return table;
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
    
    private void cleanParameters(StarTable table) {
        Iterator<?> it = table.getParameters().iterator();
        while (it.hasNext()) {
            DescribedValue v = (DescribedValue) it.next();
            if (v.getValue() == null) {
                it.remove();
            }
        }
    }
}

