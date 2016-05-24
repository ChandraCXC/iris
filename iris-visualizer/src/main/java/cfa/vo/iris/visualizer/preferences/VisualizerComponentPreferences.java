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
import cfa.vo.iris.sed.ExtSed;
import cfa.vo.iris.visualizer.plotter.MouseListenerManager;
import cfa.vo.iris.visualizer.metadata.SegmentExtractor;
import cfa.vo.iris.visualizer.plotter.PlotPreferences;
import cfa.vo.iris.visualizer.metadata.IrisStarJTable.RowSelection;
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
    private final PlotPreferences plotPreferences;
    
    // For accessing plot mouse listeners
    private final MouseListenerManager mouseListenerManager;
    
    // Pointer to the Iris workspace
    private final IWorkspace ws;
    
    // Persistence for Iris Visualizer data
    private final VisualizerDataStore dataStore;

    public VisualizerComponentPreferences(IWorkspace ws) {
        this.ws = ws;
        
        this.dataStore = new VisualizerDataStore(visualizerExecutor, ws);
        this.mouseListenerManager = new MouseListenerManager();
        
        this.plotPreferences = PlotPreferences.getDefaultPlotPreferences();
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
        
        final ExtSed sed = (ExtSed) ws.getSedManager().newSed("FilterSed");
        
        // Extract selected rows to new Segments
        final SegmentExtractor extractor = 
                new SegmentExtractor(selection.selectedTables, selection.selectedRows, sed);
        
        visualizerExecutor.submit(new Callable<ExtSed>() {
            @Override
            public ExtSed call() throws Exception {
                extractor.constructSed();
                return null;
            }
        });
        
        return sed;
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
    }
    
    /**
     * Adds or updates the segments within the specified SED.
     * @param sed - the sed to which the segment is attached
     * @param segments - the list of segments to add
     */
    public void update(ExtSed sed, List<Segment> segments) {
        dataStore.update(sed, segments);
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
    }
    
    /**
     * Removes the segments from the specified Sed Preferences map.
     * @param sed
     * @param segments
     */
    public void remove(ExtSed sed, List<Segment> segments) {
        dataStore.remove(sed, segments);
    }
}
