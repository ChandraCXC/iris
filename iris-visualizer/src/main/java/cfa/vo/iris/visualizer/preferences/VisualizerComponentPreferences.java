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

import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
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
import cfa.vo.iris.visualizer.metadata.IrisStarJTable.RowSelection;
import cfa.vo.sedlib.Segment;

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
    
    // Pointer to the Iris workspace
    final IWorkspace ws;
    
    // Persistence for Iris Visualizer data
    final VisualizerDataStore dataStore;

    public VisualizerComponentPreferences(IWorkspace ws) {
        this.ws = ws;
        
        this.dataStore = new VisualizerDataStore(visualizerExecutor);
        this.mouseListenerManager = new MouseListenerManager();
        
        this.plotPreferences = PlotPreferences.getDefaultPlotPreferences();
        addSedListeners();
    }
    
    protected void addSedListeners() {
        SegmentEvent.getInstance().add(new VisualizerSegmentListener());
        SedEvent.getInstance().add(new VisualizerSedListener());
        MultipleSegmentEvent.getInstance().add(new VisualizerMultipleSegmentListener());
    }

    /**
     * @return Top level plot preferences for the stil plotter.
     */
    public PlotPreferences getPlotPreferences() {
        return plotPreferences;
    }
    
    /**
     * @return Visualizer persistence layer
     */
    public VisualizerDataStore getDataStore() {
        return dataStore;
    }
    
    /**
     * @return the Visualizer data model
     */
    public VisualizerDataModel getDataModel() {
        return dataStore.getDataModel();
    }

    /**
     * @return
     *  Currently selected SED in the workspace
     */
    public ExtSed getSelectedSed() {
        return getDataModel().getSelectedSed();
    }

    /**
     * Sets selected SED
     * @param selectedSed
     */
    public void setSelectedSed(ExtSed selectedSed) {
        getDataModel().setSelectedSed(selectedSed);
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
     *  Preferences map for each SED.
     */
    public Map<ExtSed, SedModel> getSedModels() {
        return dataStore.getSedModels();
    }
    
    /**
     * @return
     *  Preferences for the given SED
     */
    public SedModel getSedModel(ExtSed sed) {
        return dataStore.getSedModel(sed);
    }
    
    /**
     * Used by the metadata browser to extract a selection of rows from the browser
     * into a new ExtSed. We use the SegmentExtractor class to construct a new Sed
     * from the selected set of StarTables, then asynchronously pass it back to the 
     * SedManager and let the SedListener do the work of notifying/processing the 
     * new ExtSed back into the VisualizerComponent.
     * 
     * @param selection
     * @return
     */
    public void createNewWorkspaceSed(RowSelection selection) {
        
        // Extract selected rows to new Segments
        final SegmentExtractor extractor = 
                new SegmentExtractor(selection.selectedTables, selection.selectedRows);
        
        visualizerExecutor.submit(new Callable<ExtSed>() {
            @SuppressWarnings("unchecked")
            @Override
            public ExtSed call() throws Exception {
                ws.getSedManager().add(extractor.constructSed());
                return null;
            }
        });
    }

    /**
     * Adds or updates the SED to the preferences map.
     * @param sed
     */
    public void update(ExtSed sed) {
        dataStore.update(sed);
    }
    
    /**
     * Adds or updates the segment within the specified SED.
     * @param sed - the sed to which the segment is attached
     * @param segment
     */
    public void update(ExtSed sed, Segment segment) {
        dataStore.update(sed, segment);
        fire(sed, VisualizerCommand.RESET);
    }
    
    /**
     * Adds or updates the segments within the specified SED.
     * @param sed - the sed to which the segment is attached
     * @param segments - the list of segments to add
     */
    public void update(ExtSed sed, List<Segment> segments) {
        dataStore.update(sed, segments);
        fire(sed, VisualizerCommand.RESET);
    }
    
    /**
     * Removes the SED from the preferences map.
     * @param sed
     */
    public void remove(ExtSed sed) {
        dataStore.remove(sed);
    }
    
    /**
     * Removes the segment from the specified Sed Preferences map.
     * @param sed
     * @param segment
     */
    public void remove(ExtSed sed, Segment segment) {
        dataStore.remove(sed, segment);
        fire(sed, VisualizerCommand.RESET);
    }
    
    /**
     * Removes the segments from the specified Sed Preferences map.
     * @param sed
     * @param segments
     */
    public void remove(ExtSed sed, List<Segment> segments) {
        dataStore.remove(sed, segments);
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
                setSelectedSed(sed);
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
                    VisualizerComponentPreferences.this.getSedModel(sed).getPlotPreferences();
        }
    }
}
