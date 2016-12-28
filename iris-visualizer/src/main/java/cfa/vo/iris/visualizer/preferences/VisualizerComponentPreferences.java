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

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import org.apache.commons.collections.CollectionUtils;

import com.google.common.collect.MapMaker;

import cfa.vo.iris.IWorkspace;
import cfa.vo.iris.fitting.FitController;
import cfa.vo.iris.sed.ExtSed;
import cfa.vo.iris.visualizer.plotter.MouseListenerManager;
import cfa.vo.iris.visualizer.plotter.PlotPreferences;
import cfa.vo.iris.visualizer.metadata.SegmentExtractor;
import cfa.vo.iris.visualizer.metadata.IrisStarJTable.RowSelection;
import cfa.vo.sedlib.common.SedInconsistentException;
import cfa.vo.sedlib.common.SedNoDataException;

import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Single object location for data and preferences needed by the iris visualizer 
 * components.
 * 
 */
public class VisualizerComponentPreferences {
    
    // Executor for async serialization and other internal tasks
    final Executor visualizerExecutor;
    
    // For accessing plot mouse listeners
    private final MouseListenerManager mouseListenerManager;
    
    // Pointer to the Iris workspace
    private final IWorkspace ws;
    
    // Listener for processing SedEvents
    final VisualizerEventListener listener;
    
    // Persistence for Iris Visualizer data
    private final VisualizerDataStore dataStore;
    
    // Pointer to current data model in the view. Any visualizer components that need access to the
    // current state of the Visualizer should access the current model through this class.
    //
    // TODO: This should become dynamic, and potentially support changing depending on which SEDs
    // are in the plotter.
    private VisualizerDataModel dataModel;
    
    // If this is true, the visualizer will be bound to, and will always plot the selected SED.
    private boolean boundToWorkspace;
    
    // Map of plot preferences for SEDs or collections of SEDs. Weak reference keyed map will not
    // prevent GC from removing elements with no other pointers.
    private Map<Object, PlotPreferences> preferencesStore;
    
    // Standard PlotPreferences for an empty plot
    private final PlotPreferences DEFAULT_PLOT_PREFERENCES = PlotPreferences.getDefaultPlotPreferences();
    
    public VisualizerComponentPreferences(IWorkspace ws) {
        this(ws, Executors.newFixedThreadPool(20));
    }
    
    public VisualizerComponentPreferences(IWorkspace ws, Executor visualizerExecutor) {
        this.ws = ws;
        
        this.visualizerExecutor = visualizerExecutor;
        
        this.dataStore = new VisualizerDataStore(visualizerExecutor, this);
        
        this.dataModel = new VisualizerDataModel(this);
        
        this.mouseListenerManager = new MouseListenerManager(this);
        
        this.boundToWorkspace = true;
        
        this.listener = new VisualizerEventListener(this, ws);
        
        Map<Object, PlotPreferences> builder = new MapMaker().weakKeys().makeMap();
        preferencesStore = Collections.synchronizedMap(builder);
    }
    
    /**
     * @return Visualizer persistent store
     */
    public VisualizerDataStore getDataStore() {
        return dataStore;
    }
    
    /**
     * @return the Visualizer data model
     */
    public VisualizerDataModel getDataModel() {
        return dataModel;
    }
    
    /**
     * @return
     *  MouseListeners for the stil plotter.
     */
    public MouseListenerManager getMouseListenerManager() {
        return mouseListenerManager;
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
        
        final ExtSed sed = new ExtSed("FilterSed", false);
        
        // Extract selected rows to new Segments
        final SegmentExtractor extractor =
                new SegmentExtractor(selection.selectedTables, selection.selectedRows, sed);
        extractor.constructSed();
        
        return sed;
    }
    
    /**
     * Asynchronously evaluates the model using the fit controller specified. The visualizer
     * will refresh if the specified sed model is currently live.
     * 
     */
    public void evaluateModel(final SedModel model, final FitController controller) {
        
        // Do nothing for null data 
        if (controller == null || model == null) {
            return;
        }
        
        final VisualizerDataModel dataModel = this.getDataModel();
        try {
            controller.evaluateModel(model);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        
        // Refresh the model once completed
        if (dataModel.getSelectedSeds().contains(model.getSed())) {
            dataModel.refresh();
        }
    }
    
    /**
     * @return A list of all ExtSeds available in the Workspace. 
     */
    @SuppressWarnings("unchecked")
    public List<ExtSed> getAvailableSeds() {
        return (List<ExtSed>) ws.getSedManager().getSeds();
    }
    
    /**
     * Bind or unbind the visualizer using these preferences to the selected SED in the IWorkspace.
     */
    protected void setBoundToWorkspace(boolean arg1) {
        this.boundToWorkspace = arg1;
        
        // If re-binding to workspace we need to set the selected SED.
        if (boundToWorkspace) {
            this.updateSelectedSed((ExtSed) ws.getSedManager().getSelected());
        }
    }

    /**
     * Used to refresh the datamodel's view of the specified SED, if applicable.
     * @param sed
     */
    public void fireChanges(ExtSed sed) {
        List<ExtSed> selectedSeds = dataModel.getSelectedSeds();
        if (selectedSeds.contains((sed))) {
            dataModel.setSelectedSeds(selectedSeds);
        }
    }

    /**
     * If the visualizer is bound to the workspace, this method will clear the settings from the
     * DataModel and replace them with the specified SED. Otherwise it ignores the call.
     * @param sed
     */
    public void updateSelectedSed(ExtSed sed) {
        if (sed == null) {
            dataModel.setSelectedSeds(new LinkedList<ExtSed>());
        } else {
            dataModel.setSelectedSeds(Arrays.asList(sed));
        }
    }

    /**
     * Handles removing the specified SED from the current DataModel.
     * @param sed
     */
    public void removeSed(ExtSed sed) {
        List<ExtSed> selectedSeds = dataModel.getSelectedSeds();
        if (selectedSeds.contains(sed)) {
            List<ExtSed> newSeds = new LinkedList<>(selectedSeds);
            newSeds.remove(sed);
            dataModel.setSelectedSeds(newSeds);
        }
    }

    public PlotPreferences getPlotPreferences(List<ExtSed> newSeds) {
        
        // Use default preferences for empty seds
        if (CollectionUtils.isEmpty(newSeds)) {
            return this.DEFAULT_PLOT_PREFERENCES;
        }
        
        // If it's a single SED, key it off of the SED for long-stored preferences.
        // List keys are only guaranteed to last as long as that list of preferences is
        // stored in the plotter.
        Object key = CollectionUtils.size(newSeds) == 1 ? newSeds.get(0) : newSeds;
        
        if (preferencesStore.containsKey(key)) {
            return preferencesStore.get(key);
        }
        else {
            // Otherwise place a new default preferences key into the map
            PlotPreferences prefs = PlotPreferences.getDefaultPlotPreferences();
            preferencesStore.put(key, prefs);
            return prefs;
        }
    }

    public void addSed(ExtSed sed) {
        ws.getSedManager().add(sed);
    }
}
