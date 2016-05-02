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
    final Map<ExtSed, SedModel> sedPreferences;
    
    VisualizerDataModel dataModel;
    
    public VisualizerDataStore(ExecutorService visualizerExecutor) {
        this.adapter = new IrisStarTableAdapter(visualizerExecutor);
        this.sedPreferences = Collections.synchronizedMap(new IdentityHashMap<ExtSed, SedModel>());
        this.dataModel = new VisualizerDataModel(this);
    }
    
    public VisualizerDataModel getDataModel() {
        return dataModel;
    }

    /**
     * @return Preferences map for each SED.
     */
    public Map<ExtSed, SedModel> getSedPreferences() {
        return sedPreferences;
    }
    
    /**
     * @return Preferences for the given SED
     */
    public SedModel getSedPreferences(ExtSed sed) {
        return sedPreferences.get(sed);
    }

    /**
     * Adds or updates the SED to the preferences map.
     * @param sed
     */
    public void update(ExtSed sed) {
        if (sedPreferences.containsKey(sed)) {
            sedPreferences.get(sed).refresh();
        } else {
            sedPreferences.put(sed, new SedModel(sed, adapter));
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
        
        if (sedPreferences.containsKey(sed)) {
            sedPreferences.get(sed).addSegment(segment);
        } else {
            // The segment will automatically be serialized and attached the the 
            // SedPrefrences since it's assumed to be attached to the SED.
            sedPreferences.put(sed, new SedModel(sed, adapter));
        }
    }
    
    /**
     * Adds or updates the segments within the specified SED.
     * @param sed - the sed to which the segment is attached
     * @param segments - the list of segments to add
     */
    public void update(ExtSed sed, List<Segment> segments) {
        if (sedPreferences.containsKey(sed)) {
            for (Segment segment : segments) {
                // Do nothing for null segments
                if (segment == null) continue;
                sedPreferences.get(sed).addSegment(segment);
            }
        } else {
            // The segment will automatically be serialized and attached the the 
            // SedPrefrences since it's assumed to be attached to the SED.
            sedPreferences.put(sed, new SedModel(sed, adapter));
        }
    }
    
    /**
     * Removes the SED from the preferences map.
     * @param sed
     */
    public void remove(ExtSed sed) {
        if (!sedPreferences.containsKey(sed)) {
            return;
        }
        sedPreferences.get(sed).removeAll();
        sedPreferences.remove(sed);
        //fire(sed, VisualizerCommand.RESET);
    }
    
    /**
     * Removes the segment from the specified Sed Preferences map.
     * @param sed
     * @param segment
     */
    public void remove(ExtSed sed, Segment segment) {
        // Do nothing for null segments
        if (segment == null) return;
        
        if (sedPreferences.containsKey(sed)) {
            sedPreferences.get(sed).removeSegment(segment);
        }
    }
    
    /**
     * Removes the segments from the specified Sed Preferences map.
     * @param sed
     * @param segments
     */
    public void remove(ExtSed sed, List<Segment> segments) {
        if (sedPreferences.containsKey(sed)) {
            for (Segment segment : segments) {
                // Do nothing for null segments
                if (segment == null) continue;
                sedPreferences.get(sed).removeSegment(segment);
            }
        }
    }
}
