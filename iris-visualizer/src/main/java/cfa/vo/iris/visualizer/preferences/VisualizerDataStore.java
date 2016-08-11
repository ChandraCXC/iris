/*
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
package cfa.vo.iris.visualizer.preferences;

import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executor;
import cfa.vo.iris.sed.ExtSed;
import cfa.vo.iris.visualizer.stil.tables.IrisStarTableAdapter;
import cfa.vo.sedlib.Segment;

/**
 * 'Persistence' layer for all SED data related to the Iris Visualizer.
 *
 */
public class VisualizerDataStore {
    
    
    private VisualizerComponentPreferences preferences;
    
    // For converting Segments to StarTables
    private IrisStarTableAdapter adapter;
    
    // All preferences for each ExtSed in the workspace
    final Map<ExtSed, SedModel> sedModels;
    
    public VisualizerDataStore(Executor executor, VisualizerComponentPreferences preferences) {
        this.sedModels = Collections.synchronizedMap(new IdentityHashMap<ExtSed, SedModel>());
        this.preferences = preferences;
        this.adapter = new IrisStarTableAdapter(executor);
    }
    
    /**
     * @return Preferences map for each SED.
     */
    public Map<ExtSed, SedModel> getSedModels() {
        return Collections.unmodifiableMap(sedModels);
    }
    
    /**
     * @return Preferences for the given SED
     * @param sed
     */
    public SedModel getSedModel(ExtSed sed) {
        return sedModels.get(sed);
    }

    /**
     * Adds or updates the SED to the preferences map.
     * @param sed
     */
    public void update(ExtSed sed) {
        if (sedModels.containsKey(sed)) {
            sedModels.get(sed).refresh();
        } else {
            addNewSedModel(sed);
        }
        
        preferences.fireChanges(sed);
    }

    /**
     * Adds or updates the segment within the specified SED.
     * @param sed - the sed to which the segment is attached
     * @param segment
     */
    public void update(ExtSed sed, Segment segment) {
        // Do nothing for null segments
        if (segment == null) return;
        
        if (sedModels.containsKey(sed)) {
            SedModel model = sedModels.get(sed);
            model.updateSegment(segment);
        } else {
            // The segment will automatically be serialized and attached the the 
            // SedPrefrences since it's assumed to be attached to the SED.
            addNewSedModel(sed);
        }
        
        preferences.fireChanges(sed);
    }
    
    /**
     * Adds or updates the segments within the specified SED.
     * @param sed - the sed to which the segment is attached
     * @param segments - the list of segments to add
     */
    public void update(ExtSed sed, List<Segment> segments) {
        if (sedModels.containsKey(sed)) {
            SedModel model = sedModels.get(sed);
            for (Segment segment : segments) {
                // Do nothing for null segments
                if (segment == null) continue;
                model.updateSegments(segments);
            }
        } else {
            // The segment will automatically be serialized and attached the the 
            // SedPrefrences since it's assumed to be attached to the SED.
            addNewSedModel(sed);
        }
        
        preferences.fireChanges(sed);
    }
    
    /**
     * Removes the SED from the preferences map.
     * @param sed
     */
    public void remove(ExtSed sed) {
        if (!sedModels.containsKey(sed)) {
            return;
        }
        sedModels.get(sed).removeAll();
        sedModels.remove(sed);
        
        preferences.removeSed(sed);
    }
    
    /**
     * Removes the segment from the specified Sed Preferences map.
     * @param sed
     * @param segment
     */
    public void remove(ExtSed sed, Segment segment) {
        // Do nothing for null segments
        if (segment == null) return;
        
        if (sedModels.containsKey(sed)) {
            sedModels.get(sed).removeSegment(segment);
        }
        
        preferences.fireChanges(sed);
    }
    
    /**
     * Removes the segments from the specified Sed Preferences map.
     * @param sed
     * @param segments
     */
    public void remove(ExtSed sed, List<Segment> segments) {
        if (sedModels.containsKey(sed)) {
            for (Segment segment : segments) {
                // Do nothing for null segments
                if (segment == null) continue;
                sedModels.get(sed).removeSegment(segment);
            }
        }
        
        preferences.fireChanges(sed);
    }
    
    /**
     * Adds a new SedModel to the map keyed off the specified SED
     * @param sed
     */
    private void addNewSedModel(ExtSed sed) {
        SedModel newModel = new SedModel(sed, adapter);
        sedModels.put(sed, newModel);
    }
}
