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

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import cfa.vo.iris.IWorkspace;
import cfa.vo.iris.sed.ExtSed;
import cfa.vo.iris.visualizer.plotter.MouseListenerManager;
import cfa.vo.iris.visualizer.metadata.SegmentExtractor;
import cfa.vo.iris.visualizer.metadata.IrisStarJTable.RowSelection;
import cfa.vo.sedlib.common.SedInconsistentException;
import cfa.vo.sedlib.common.SedNoDataException;

/**
 * Single object location for data and preferences needed by the iris visualizer 
 * components.
 * 
 */
public class VisualizerComponentPreferences {
    
    private static final ExecutorService visualizerExecutor = Executors.newFixedThreadPool(5);
    
    // For accessing plot mouse listeners
    private final MouseListenerManager mouseListenerManager;
    
    // Pointer to the Iris workspace
    private final IWorkspace ws;
    
    // Persistence for Iris Visualizer data
    private final VisualizerDataStore dataStore;
    
    // Pointer to current data model in the view. Any visualizer components that need access to the
    // current state of the Visualizer should access the current model through this class.
    //
    // TODO: This should become dynamic, and potentially support changing depending on which SEDs
    // are in the plotter.
    private VisualizerDataModel dataModel;
    
    // If this is true, the visualizer will be bound to, and will always plot the selected SED.
    private boolean boundToWorkspace = true;
    
    public VisualizerComponentPreferences(IWorkspace ws) {
        this.ws = ws;
        
        this.dataStore = new VisualizerDataStore(visualizerExecutor, this);
        
        this.dataModel = new VisualizerDataModel(dataStore);
        
        this.mouseListenerManager = new MouseListenerManager();
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
     * @return if the visualizer using these preferences is bound to the IWorkspace
     */
    protected boolean isBoundToWorkspace() {
        return boundToWorkspace;
    }
    
    /**
     * Bind or unbind the visualizer using these preferences to the selected SED in the IWorkspace.
     */
    protected void setBoundToWorkspace(boolean boundToWorkspace) {
        this.boundToWorkspace = boundToWorkspace;
    }
}
