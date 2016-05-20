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

package cfa.vo.iris.visualizer.preferences;

import java.util.Collection;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import cfa.vo.iris.IWorkspace;
import cfa.vo.iris.events.MultipleSegmentEvent;
import cfa.vo.iris.events.MultipleSegmentListener;
import cfa.vo.iris.events.SedCommand;
import cfa.vo.iris.events.SedEvent;
import cfa.vo.iris.events.SedListener;
import cfa.vo.iris.events.SegmentEvent;
import cfa.vo.iris.events.SegmentEvent.SegmentPayload;
import cfa.vo.iris.events.SegmentListener;
import cfa.vo.iris.sed.ExtSed;
import cfa.vo.iris.visualizer.plotter.MouseListenerManager;
import cfa.vo.iris.visualizer.metadata.SegmentExtractor;
import cfa.vo.iris.visualizer.plotter.PlotPreferences;
import cfa.vo.iris.visualizer.plotter.SegmentModel;
import cfa.vo.iris.visualizer.stil.IrisStarJTable.RowSelection;
import cfa.vo.iris.visualizer.stil.tables.IrisStarTableAdapter;
import cfa.vo.sedlib.Segment;
import cfa.vo.sedlib.common.SedInconsistentException;
import cfa.vo.sedlib.common.SedNoDataException;

/**
 * Single object location for data and preferences needed by the iris visualizer 
 * components.
 * 
 */
public class VisualizerComponentPreferences {
    
    private static final ExecutorService visualizerExecutor = Executors.newFixedThreadPool(5);
    
    // Top level preferences for the plotter
    PlotPreferences plotPreferences;
    
    // For accessing plot mouse listeners
    MouseListenerManager mouseListenerManager;
    
    // Converts a segment into an Iris StarTable
    IrisStarTableAdapter adapter;
    
    // Pointer to the Iris workspace
    final IWorkspace ws;
    
    // All preferences for each ExtSed in the workspace
    final Map<ExtSed, SedModel> sedPreferences;
    
    // Sed to display in the Plotter (TODO: support multiple SEDs.)
    ExtSed selectedSed;

    @SuppressWarnings("unchecked")
    public VisualizerComponentPreferences(IWorkspace ws) {
        this.ws = ws;
        
        // For converting segments to IrisStarTables
        this.adapter = new IrisStarTableAdapter(visualizerExecutor);
        
        // Manages all mouse listeners over the plotter
        this.mouseListenerManager = new MouseListenerManager();
        
        // Create and add preferences for the SED
        this.sedPreferences = Collections.synchronizedMap(new IdentityHashMap<ExtSed, SedModel>());
        for (ExtSed sed : (List<ExtSed>) ws.getSedManager().getSeds()) {
            update(sed);
        }
        this.selectedSed = (ExtSed) ws.getSedManager().getSelected();
        
        // Plotter global preferences
        if (this.sedPreferences.isEmpty()) {
            this.plotPreferences = PlotPreferences.getDefaultPlotPreferences();
        } else {
            this.plotPreferences = this.getSedPreferences(getSelectedSed()).getPlotPreferences();
        }
        
        // Add SED listener
        addSedListeners();
    }
    
    protected void addSedListeners() {
        SegmentEvent.getInstance().add(new VisualizerSegmentListener());
        SedEvent.getInstance().add(new VisualizerSedListener());
        MultipleSegmentEvent.getInstance().add(new VisualizerMultipleSegmentListener());
    }

    /**
     * @return
     *  Top level plot preferences for the stil plotter.
     */
    public PlotPreferences getPlotPreferences() {
        return plotPreferences;
    }

    /**
     * @return
     *  Currently selected SED in the workspace
     */
    public ExtSed getSelectedSed() {
        return selectedSed;
    }

    /**
     * Sets selected SED
     * @param selectedSed
     */
    public void setSelectedSed(ExtSed selectedSed) {
        this.selectedSed = selectedSed;
    }

    /**
     * @return
     *  The Segment -> StarTable adapter currently in use in the workspace.
     */
    public IrisStarTableAdapter getAdapter() {
        return adapter;
    }
    
    /**
     * @return
     *  MouseListeners for the stil plotter.
     */
    public MouseListenerManager getMouseListenerManager() {
        return mouseListenerManager;
    }
    
    /**
     * @return
     *  Collection of all the segment layers attached to the currently selected SED.
     */
    public Collection<SegmentModel> getSelectedLayers() {
        SedModel p = getSedPreferences(getSelectedSed());
        if (p == null) {
            return Collections.emptyList();
        }
        
        return p.getAllSegmentPreferences().values();
    }

    /**
     * @return
     *  Preferences map for each SED.
     */
    public Map<ExtSed, SedModel> getSedPreferences() {
        return sedPreferences;
    }
    
    /**
     * @return
     *  Preferences for the given SED
     */
    public SedModel getSedPreferences(ExtSed sed) {
        return sedPreferences.get(sed);
    }
    
    /**
     * Used by the metadata browser to extract a selection of rows from the browser
     * into a new ExtSed. We use the SegmentExtractor class to construct a new Sed
     * from the selected set of StarTables, then  pass it back to the 
     * SedManager and let the SedListener do the work of asynchronously 
     * notifying/processing the new ExtSed back into the VisualizerComponent.
     * 
     * @param selection
     * @return
     * @throws SedNoDataException 
     * @throws SedInconsistentException 
     */
    public ExtSed createNewWorkspaceSed(RowSelection selection) throws SedInconsistentException, SedNoDataException {
        
        // Extract selected rows to new Segments
        final SegmentExtractor extractor = 
                new SegmentExtractor(selection.selectedTables, selection.selectedRows);
        
        try {
            ExtSed newSed = extractor.constructSed();
            ws.getSedManager().add(newSed);
            return newSed;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
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
        //fire(sed, VisualizerCommand.RESET);
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
        
        fire(sed, VisualizerCommand.RESET);
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
        fire(sed, VisualizerCommand.RESET);
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
        
        fire(sed, VisualizerCommand.RESET);
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
        
        fire(sed, VisualizerCommand.RESET);
    }
    
    protected void fire(ExtSed source, VisualizerCommand command) {
        VisualizerChangeEvent.getInstance().fire(source, command);
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
                e.printStackTrace();
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
                setSelectedSed(sed);
                fire(sed, VisualizerCommand.SELECTED);
            }
            else {
                // Doesn't merit a full reset, this is basically just here for SED name changes
                fire(sed, VisualizerCommand.REDRAW); // should remove this
            }
        }
    }
    
    private class VisualizerSegmentListener implements SegmentListener {

        @Override
        public void process(Segment segment, SegmentPayload payload) {
            try {
                processNotification(segment, payload);
            } catch (Exception e) {
                // TODO: This happens asynchronously, what should we do with exceptions?
                e.printStackTrace();
            }
        }
        
        private void processNotification(Segment segment, SegmentPayload payload) {
            
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
        public void process(java.util.List<Segment> segments, SegmentPayload payload) {
            try {
                processNotification(segments, payload);
            } catch (Exception e) {
                // TODO: This happens asynchronously, what should we do with
                // exceptions?
                e.printStackTrace();
            }
        }
        
        private void processNotification(java.util.List<Segment> segments, SegmentPayload payload) {
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
            // update plot preferences
            VisualizerComponentPreferences.this.plotPreferences = 
                    VisualizerComponentPreferences.this.getSedPreferences(sed).getPlotPreferences();
        }
    }
}
