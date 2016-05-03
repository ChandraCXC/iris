package cfa.vo.iris.visualizer.preferences;

import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;

import cfa.vo.iris.sed.ExtSed;
import cfa.vo.iris.visualizer.stil.tables.IrisStarTableAdapter;
import cfa.vo.sedlib.Segment;

/**
 * 'Persistence' layer for all SED data related to the Iris Visualizer.
 *
 */
public class VisualizerDataStore {
    
    // Converts a segment into an Iris StarTable
    IrisStarTableAdapter adapter;

    // All preferences for each ExtSed in the workspace
    final Map<ExtSed, SedModel> sedModels;
    
    VisualizerDataModel dataModel;
    
    public VisualizerDataStore(ExecutorService visualizerExecutor) {
        this.adapter = new IrisStarTableAdapter(visualizerExecutor);
        this.sedModels = Collections.synchronizedMap(new IdentityHashMap<ExtSed, SedModel>());
        this.dataModel = new VisualizerDataModel(this);
    }
    
    public VisualizerDataModel getDataModel() {
        return dataModel;
    }

    /**
     * @return Preferences map for each SED.
     */
    public Map<ExtSed, SedModel> getSedModels() {
        return sedModels;
    }
    
    /**
     * @return Preferences for the given SED
     * @param sed
     */
    public SedModel getSedPreferences(ExtSed sed) {
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
            sedModels.put(sed, new SedModel(sed, adapter));
        }
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
            sedModels.get(sed).addSegment(segment);
        } else {
            // The segment will automatically be serialized and attached the the 
            // SedPrefrences since it's assumed to be attached to the SED.
            sedModels.put(sed, new SedModel(sed, adapter));
        }
    }
    
    /**
     * Adds or updates the segments within the specified SED.
     * @param sed - the sed to which the segment is attached
     * @param segments - the list of segments to add
     */
    public void update(ExtSed sed, List<Segment> segments) {
        if (sedModels.containsKey(sed)) {
            for (Segment segment : segments) {
                // Do nothing for null segments
                if (segment == null) continue;
                sedModels.get(sed).addSegment(segment);
            }
        } else {
            // The segment will automatically be serialized and attached the the 
            // SedPrefrences since it's assumed to be attached to the SED.
            sedModels.put(sed, new SedModel(sed, adapter));
        }
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
    }
}
