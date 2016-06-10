package cfa.vo.iris.visualizer.preferences;

import cfa.vo.iris.events.MultipleSegmentEvent;
import cfa.vo.iris.events.MultipleSegmentListener;
import cfa.vo.iris.events.SedCommand;
import cfa.vo.iris.events.SedEvent;
import cfa.vo.iris.events.SedListener;
import cfa.vo.iris.events.SegmentEvent;
import cfa.vo.iris.events.SegmentListener;

import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.logging.Level;
import java.util.logging.Logger;

import cfa.vo.iris.sed.ExtSed;
import cfa.vo.iris.visualizer.stil.tables.IrisStarTableAdapter;
import cfa.vo.sedlib.Segment;

/**
 * 'Persistence' layer for all SED data related to the Iris Visualizer.
 *
 */
public class VisualizerDataStore {
    
    private static final Logger logger = Logger.getLogger(VisualizerDataStore.class.getName());
    
    private VisualizerComponentPreferences preferences;
    
    // For converting Segments to StarTables
    private IrisStarTableAdapter adapter;
    
    // All preferences for each ExtSed in the workspace
    final Map<ExtSed, SedModel> sedModels;
    
    public VisualizerDataStore(ExecutorService visualizerExecutor, VisualizerComponentPreferences preferences) {
        this.sedModels = Collections.synchronizedMap(new IdentityHashMap<ExtSed, SedModel>());
        this.preferences = preferences;
        this.adapter = new IrisStarTableAdapter(visualizerExecutor, preferences);
        
        addSedListeners();
    }
    
    protected void addSedListeners() {
        SegmentEvent.getInstance().add(new VisualizerSegmentListener());
        SedEvent.getInstance().add(new VisualizerSedListener());
        MultipleSegmentEvent.getInstance().add(new VisualizerMultipleSegmentListener());
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
            sedModels.put(sed, new SedModel(sed, adapter));
        }
        
        preferences.getDataModel().fireChanges(sed);
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
        
        preferences.getDataModel().fireChanges(sed);
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
        
        preferences.getDataModel().fireChanges(sed);
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
        
        preferences.getDataModel().setSelectedSed(null);
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
        
        preferences.getDataModel().fireChanges(sed);
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
        
        preferences.getDataModel().fireChanges(sed);
    }
    
    /**
     * These listeners are responsible for detecting any changes in the SEDManger and firing
     * off the appropriate update events for each object in the visualizer component.
     *
     */
    private class VisualizerSedListener implements SedListener {
        
        @Override
        public void process(ExtSed sed, SedCommand payload) {
            try {
                processNotification(sed, payload);
            } catch (Exception e) {
                // TODO: This happens asynchronously, what should we do with exceptions?
                logger.log(Level.SEVERE, "Exception in visualizer data processing", e);
            }
        }
        
        private void processNotification(ExtSed sed, SedCommand payload) {
            // Only take actions if an SED was added, removed, or selected.
            // Rely on Segment events to pick up changes within an SED.
            if (SedCommand.ADDED.equals(payload))
            {
                update(sed);
            }
            else if (SedCommand.REMOVED.equals(payload)) {
                remove(sed);
            } 
            else if (SedCommand.SELECTED.equals(payload)) {
                // If the visualizer is tied to the workspace, update the SED
                if (preferences.isBoundToWorkspace()) {
                    preferences.getDataModel().setSelectedSed(sed);
                }
            }
            else {
                // Doesn't merit a full reset, this is basically just here for SED name changes
                preferences.getDataModel().fireChanges(sed);
            }
        }
    }
    
    private class VisualizerSegmentListener implements SegmentListener {

        @Override
        public void process(Segment segment, SegmentEvent.SegmentPayload payload) {
            try {
                processNotification(segment, payload);
            } catch (Exception e) {
                // TODO: This happens asynchronously, what should we do with exceptions?
                logger.log(Level.SEVERE, "Exception in visualizer data processing", e);
            }
        }
        
        private void processNotification(Segment segment, SegmentEvent.SegmentPayload payload) {
            
            ExtSed sed = payload.getSed();
            SedCommand command = payload.getSedCommand();
            
            // Update the SED with the new or updated segment
            if (SedCommand.ADDED.equals(command) ||
                SedCommand.CHANGED.equals(command))
            {
                update(sed, segment);
            }
            
            // Remove the deleted segment from the SED
            else if (SedCommand.REMOVED.equals(command)) {
                remove(sed, segment);
            }
        }
    }
    
    private class VisualizerMultipleSegmentListener implements MultipleSegmentListener {
        
        @Override
        public void process(java.util.List<Segment> segments, SegmentEvent.SegmentPayload payload) {
            try {
                processNotification(segments, payload);
            } catch (Exception e) {
                // TODO: This happens asynchronously, what should we do with
                // exceptions?
                logger.log(Level.SEVERE, "Exception in visualizer data processing", e);
            }
        }
        
        private void processNotification(java.util.List<Segment> segments, SegmentEvent.SegmentPayload payload) {
            ExtSed sed = payload.getSed();
            SedCommand command = payload.getSedCommand();
            
            // Update the SED with the new or updated segments
            if (SedCommand.ADDED.equals(command) ||
                SedCommand.CHANGED.equals(command))
            {
                update(sed, segments);
            }
            
            // Remove the deleted segments from the SED
            else if (SedCommand.REMOVED.equals(command)) {
                remove(sed, segments);
            }
        }
    }
}
