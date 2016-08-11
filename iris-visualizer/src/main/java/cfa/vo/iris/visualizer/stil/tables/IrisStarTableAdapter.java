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

package cfa.vo.iris.visualizer.stil.tables;

import java.util.concurrent.Executor;

import cfa.vo.iris.sed.stil.SegmentStarTable;
import cfa.vo.iris.sed.stil.SerializingStarTableAdapter;
import cfa.vo.iris.sed.stil.StarTableAdapter;
import cfa.vo.iris.units.UnitsException;
import cfa.vo.sedlib.Segment;
import cfa.vo.sedlib.common.SedInconsistentException;
import cfa.vo.sedlib.common.SedNoDataException;
import uk.ac.starlink.table.EmptyStarTable;
import uk.ac.starlink.table.StarTable;

/**
 * Adapter for converting a SedLibSegment to an IrisStarTable for use by 
 * the IrisVisualizer. The class can execute the conversion synchronously 
 * or asynchronously - which users may want to do in the case of large 
 * (>3000 point) segments.
 * 
 * If a name is specified in the conversion method the adapter will apply 
 * the name to the new StarTables.
 *
 */
public class IrisStarTableAdapter {
    
    public static final StarTable EMPTY_STARTABLE = new EmptyStarTable();
    
    private final Executor executor;
    
    public IrisStarTableAdapter(Executor executor) {
        this.executor = executor;
    }

    public IrisStarTable convertSegment(Segment data) {
        return convert(data, false);
    }
    
    public IrisStarTable convertSegmentAsync(Segment data) {
        return convert(data, true);
    }
    
    private IrisStarTable convert(Segment data, boolean async) {
        try {
            SegmentStarTable segTable = new SegmentStarTable(data);
            IrisStarTable ret;
            
            SerializingStarTableAdapter adapter = new SerializingStarTableAdapter();
            if (async) {
                ret = new IrisStarTable(segTable, EMPTY_STARTABLE);
                executor.execute(new AsyncSerializer(data, adapter, ret));
            } else {
                ret = new IrisStarTable(segTable, adapter.convertStarTable(data));
            }
            
            return ret;
        } catch (SedNoDataException | SedInconsistentException | UnitsException e) {
            throw new RuntimeException(e);
        }
    }
    
    private static class AsyncSerializer implements Runnable {
        
        private final Segment data;
        private final StarTableAdapter<Segment> adapter;
        private final IrisStarTable table;

        public AsyncSerializer(Segment data, 
                StarTableAdapter<Segment> adapter, 
                IrisStarTable table)
        {
            this.data = data;
            this.adapter = adapter;
            this.table = table;
        }
        
        @Override
        public void run() {
            // Convert and update the datatable
            StarTable converted = adapter.convertStarTable(data);
            
            // Update the IrisStarTable with the new value
            table.setSegmentMetadataTable(converted);
        }
    }
}
