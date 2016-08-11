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

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.logging.Level;
import java.util.logging.Logger;

import cfa.vo.iris.sed.ExtSed;
import cfa.vo.iris.sed.stil.SegmentStarTable;
import cfa.vo.iris.sed.stil.SerializingSegmentAdapter;
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
    
    private static final Logger logger = Logger.getLogger(IrisStarTableAdapter.class.getName());
    
    public static final StarTable EMPTY_STARTABLE = new EmptyStarTable();
    
    private final Executor executor;
    
    public IrisStarTableAdapter(Executor executor) {
        this.executor = executor;
    }

    public IrisStarTable convertSegment(Segment data) {
        return convertSegment(data, false);
    }
    
    public IrisStarTable convertSegmentAsync(Segment data) {
        return convertSegment(data, true);
    }
    
    public List<IrisStarTable> convertSed(ExtSed sed) {
        return convertSed(sed, false);
    }

    public List<IrisStarTable> convertSedAsync(ExtSed sed) {
        return convertSed(sed, true);
    }
    
    private IrisStarTable convertSegment(Segment data, boolean async) {
        try {
            SegmentStarTable segTable = new SegmentStarTable(data);
            IrisStarTable ret;
            
            SerializingSegmentAdapter adapter = new SerializingSegmentAdapter();
            if (async) {
                ret = new IrisStarTable(segTable, EMPTY_STARTABLE);
                executor.execute(new AsyncSegmentSerializer(data, adapter, ret));
            } else {
                ret = new IrisStarTable(segTable, adapter.convertStarTable(data));
            }
            
            return ret;
        } catch (SedNoDataException | SedInconsistentException | UnitsException e) {
            throw new RuntimeException(e);
        }
    }
    
    private List<IrisStarTable> convertSed(ExtSed sed, boolean async) {
        SerializingSegmentAdapter adapter = new SerializingSegmentAdapter();
        List<IrisStarTable> newTables = new ArrayList<>(sed.getNumberOfSegments());
        
        try {
            // Iterate over each segment
            for (Segment seg : sed.getSegments()) {
                // Create the segment star table for plotting data
                SegmentStarTable segTable = new SegmentStarTable(seg);
                
                // Always set data table to empty star tables initially
                newTables.add(new IrisStarTable(segTable, EMPTY_STARTABLE));
            }
            
            if (async) {
                executor.execute(new AsyncSedSerializer(sed, adapter, newTables));
            } else {
                List<StarTable> converted = adapter.convertSed(sed);
                setStarTables(newTables, converted);
            }
            
            return newTables;
        } catch (SedNoDataException | SedInconsistentException | UnitsException e) {
            throw new RuntimeException(e);
        }
    }
    
    private static class AsyncSegmentSerializer implements Runnable {
        
        private final Segment data;
        private final SerializingSegmentAdapter adapter;
        private final IrisStarTable table;

        public AsyncSegmentSerializer(Segment data, 
                SerializingSegmentAdapter adapter, 
                IrisStarTable table)
        {
            this.data = data;
            this.adapter = adapter;
            this.table = table;
        }
        
        @Override
        public void run() {
            try {
                // Convert and update the datatable
                StarTable converted = adapter.convertStarTable(data);
                
                // Update the IrisStarTable with the new value
                table.setSegmentMetadataTable(converted);
            } catch (Exception e) {
                logger.log(Level.SEVERE, null, e);
            }
        }
    }
    
    private static class AsyncSedSerializer implements Runnable {
        
        private final ExtSed data;
        private final SerializingSegmentAdapter adapter;
        private final List<IrisStarTable> tables;

        public AsyncSedSerializer(ExtSed data, 
                SerializingSegmentAdapter adapter, 
                List<IrisStarTable> tables)
        {
            this.data = data;
            this.adapter = adapter;
            this.tables = tables;
        }
        
        @Override
        public void run() {
            try {
                // Convert and update the datatable
                List<StarTable> converted = adapter.convertSed(data);
                setStarTables(tables, converted);
            } catch (Exception e) {
                logger.log(Level.SEVERE, null, e);
            }
        }
    }
    
    private static void setStarTables(List<IrisStarTable> newTables, List<StarTable> converted) {
        if (newTables.size() != converted.size()) {
            throw new IllegalArgumentException("lists must have equal size!");
        }
        
        // Update the IrisStarTable with the new value
        for (int i=0; i<converted.size(); i++) {
            newTables.get(i).setSegmentMetadataTable(converted.get(i));
        }
    }
}
