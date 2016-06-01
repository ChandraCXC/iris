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

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

import org.apache.commons.lang.StringUtils;

import cfa.vo.iris.sed.stil.SegmentStarTable;
import cfa.vo.iris.sed.stil.SerializingStarTableAdapter;
import cfa.vo.iris.sed.stil.StarTableAdapter;
import cfa.vo.iris.units.UnitsException;
import cfa.vo.iris.visualizer.preferences.VisualizerChangeEvent;
import cfa.vo.iris.visualizer.preferences.VisualizerCommand;
import cfa.vo.sedlib.Segment;
import cfa.vo.sedlib.common.SedInconsistentException;
import cfa.vo.sedlib.common.SedNoDataException;
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
    
    private final ExecutorService executor;
    
    public IrisStarTableAdapter(ExecutorService executor) {
        this.executor = executor;
    }

    public IrisStarTable convertSegment(Segment data) {
        return convert(data, false, null);
    }

    public IrisStarTable convertSegment(Segment data, String name) {
        return convert(data, false, name);
    }
    
    public IrisStarTable convertSegmentAsync(Segment data) {
        return convert(data, true, null);
    }
    
    public IrisStarTable convertSegmentAsync(Segment data, String name) {
        return convert(data, true, name);
    }
    
    private IrisStarTable convert(Segment data, boolean async, String name) {
        try {
            SegmentStarTable segTable = new SegmentStarTable(data);
            IrisStarTable ret;
            
            SerializingStarTableAdapter adapter = new SerializingStarTableAdapter();
            if (async) {
                Future<StarTable> val = executor.submit(new AsyncSerializer(data, adapter, name));
                ret = new IrisStarTable(segTable, val);
            } else {
                ret = new IrisStarTable(segTable, adapter.convertStarTable(data));
            }
            
            // Set the name of the new star table if a name has been specified
            if (StringUtils.isNotBlank(name)) {
                ret.setName(name);
            }
            
            return ret;
        } catch (SedNoDataException | SedInconsistentException | UnitsException e) {
            throw new RuntimeException(e);
        }
    }
    
    private static class AsyncSerializer implements Callable<StarTable> {
        
        private final Segment data;
        private final StarTableAdapter<Segment> adapter;
        private final String name;

        public AsyncSerializer(Segment data, StarTableAdapter<Segment> adapter, String name) {
            this.data = data;
            this.adapter = adapter;
            this.name = name;
        }
        
        @Override
        public StarTable call() throws Exception {
            // Convert and update the datatable
            StarTable converted = adapter.convertStarTable(data);

            // Set the name of the new star table if a name has been specified
            if (StringUtils.isNotBlank(name)) {
                converted.setName(name);
            }
            
            // Notify components of a change
            notifyVisualizerComponents();
            return converted;
        }
        
        private void notifyVisualizerComponents() {
            VisualizerChangeEvent.getInstance().fire(null, VisualizerCommand.REDRAW);
        }
    }
}
