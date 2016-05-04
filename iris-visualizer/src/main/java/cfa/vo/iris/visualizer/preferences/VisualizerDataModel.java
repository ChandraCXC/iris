package cfa.vo.iris.visualizer.preferences;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.LinkedList;
import java.util.List;

import cfa.vo.iris.sed.ExtSed;
import cfa.vo.iris.visualizer.stil.tables.IrisStarTable;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Dynamic model for the plotter and metadata browser. Maintains the current state
 * of the Visualizer component.
 *
 */
public class VisualizerDataModel {
    
    public static final String PROP_SELECTED_SEDS = "selectedSeds";
    public static final String PROP_SELECTED_SED = "selectedSed";
    public static final String PROP_SELECTED_SEGMENT_MODELS = "selectedSegmentModels";
    public static final String PROP_SED_STARTABLES = "sedStarTables";
    public static final String PROP_SELECTED_STARTABLES = "selectedStarTables";

    private final PropertyChangeSupport pcs;
    private final VisualizerDataStore store;

    // Seds to display in the visualizer
    // TODO: Support multiple SEDS
    private List<ExtSed> selectedSeds = new LinkedList<>();
    private ExtSed selectedSed;

    // list of star tables associated with selectedSed, these tables will all be plotted
    List<SegmentModel> sedSegmentModels = new LinkedList<>();
    
    // list of star tables associated with selectedSed, these tables will all be plotted
    List<IrisStarTable> sedStarTables = new LinkedList<>();
    
    // list of selected StarTables from selectedTables, or which star tables are shown in the 
    // Metadata browser
    List<IrisStarTable> selectedStarTables = new LinkedList<>();
    
    public VisualizerDataModel(VisualizerDataStore store) {
        this.store = store;
        this.pcs = new PropertyChangeSupport(this);
    }

    public void addPropertyChangeListener(PropertyChangeListener listener) {
        this.pcs.addPropertyChangeListener(listener);
    }

    public void removePropertyChangeListener(PropertyChangeListener listener) {
        this.pcs.removePropertyChangeListener(listener);
    }
    
    public List<SegmentModel> getModelsForSed(ExtSed sed) {
        SedModel model = store.getSedModel(sed);
        if (model == null) return new ArrayList<>();
        
        return new ArrayList<>(model.getAllSegmentModels().values());
    }
    
    /*
     * 
     * Getters and Setters
     * 
     */
    
    
    //TODO: Support lists of SEDs
    public List<ExtSed> getSelectedSeds() {
        return selectedSeds;
    }

    private void setSelectedSeds(List<ExtSed> selectedSeds) {
        List<ExtSed> oldSeds = this.selectedSeds;
        this.selectedSeds = selectedSeds;
        pcs.firePropertyChange(PROP_SELECTED_SEDS, oldSeds, selectedSeds);
    }
    
    public ExtSed getSelectedSed() {
        return selectedSed;
    }

    public void setSelectedSed(ExtSed selectedSed) {
        ExtSed oldSed = this.selectedSed;
        this.selectedSed = selectedSed;
        
        List<SegmentModel> newSedModels = new LinkedList<>();
        List<IrisStarTable> newSedTables = new LinkedList<>();

        // Update models
        SedModel sedModel = store.getSedModel(selectedSed);
        for (int i = 0; i < selectedSed.getNumberOfSegments(); i++) {
            SegmentModel segModel = sedModel.getSegmentModel(selectedSed.getSegment(i));
            newSedModels.add(segModel);
            newSedTables.add(segModel.getInSource());
        }
        
        pcs.firePropertyChange(PROP_SELECTED_SED, oldSed, selectedSed);
        
        this.setSedSegmentModels(newSedModels);
        this.setSedStarTables(newSedTables);
        this.setSelectedSeds(Arrays.asList(selectedSed));
    }
    
    public List<SegmentModel> getSedSegmentModels() {
        return sedSegmentModels;
    }
    
    // Locked down since these are tied to the selected seds
    void setSedSegmentModels(List<SegmentModel> newModels) {
        List<SegmentModel> oldModels = this.sedSegmentModels;
        this.sedSegmentModels = newModels;
        pcs.firePropertyChange(PROP_SELECTED_SEGMENT_MODELS, oldModels, sedSegmentModels);
    }
    
    public List<IrisStarTable> getSedStarTables() {
        return sedStarTables;
    }
    
    // Locked down since these are tied to the selected seds
    public void setSedStarTables(List<IrisStarTable> newTables) {
        List<IrisStarTable> oldTables = sedStarTables;
        this.sedStarTables = newTables;
        pcs.firePropertyChange(PROP_SED_STARTABLES, oldTables, sedStarTables);
    }
    
    public List<IrisStarTable> getSelectedStarTables() {
        return selectedStarTables;
    }

    public void setSelectedStarTables(List<IrisStarTable> newStarTables) {
        List<IrisStarTable> oldStarTables = selectedStarTables;
        this.selectedStarTables = newStarTables;
        pcs.firePropertyChange(PROP_SELECTED_STARTABLES, oldStarTables, selectedStarTables);
    }
}
