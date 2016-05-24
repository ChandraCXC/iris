package cfa.vo.iris.visualizer.preferences;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.LinkedList;
import java.util.List;

import cfa.vo.iris.sed.ExtSed;
import cfa.vo.iris.visualizer.stil.tables.IrisStarTable;

import java.util.ArrayList;
import org.apache.commons.collections.CollectionUtils;
import org.jdesktop.observablecollections.ObservableCollections;

/**
 * Dynamic model for the plotter and metadata browser. Maintains the current state
 * of the Visualizer component.
 *
 */
public class VisualizerDataModel {
    
    public static final String PROP_DATAMODEL_TITLE = "dataModelTitle";
    public static final String PROP_SELECTED_SEDS = "selectedSeds";
    public static final String PROP_SELECTED_SED = "selectedSed";
    public static final String PROP_SED_SEGMENT_MODELS = "sedSegmentModels";
    public static final String PROP_SED_STARTABLES = "sedStarTables";
    public static final String PROP_SELECTED_STARTABLES = "selectedStarTables";

    private final PropertyChangeSupport pcs;
    private final VisualizerDataStore store;

    // Name of the window browser, is adjustable and currently tied to the selectedSed
    // TODO: Support multiple SEDS
    private String dataModelTitle = null;

    // Seds to display in the visualizer
    // TODO: Support multiple SEDS
    private List<ExtSed> selectedSeds;
    private ExtSed selectedSed;

    // list of star tables associated with selectedSed, these tables will all be plotted
    List<SegmentModel> sedSegmentModels;
    
    // list of star tables associated with selectedSed, these tables will all be plotted
    List<IrisStarTable> sedStarTables;
    
    // list of selected StarTables from selectedTables, or which star tables are shown in the 
    // Metadata browser
    List<IrisStarTable> selectedStarTables;
    
    public VisualizerDataModel(VisualizerDataStore store) {
        this.store = store;
        this.pcs = new PropertyChangeSupport(this);
        
        this.setSelectedSeds(new LinkedList<ExtSed>());
        this.setSedSegmentModels(new LinkedList<SegmentModel>());
        this.setSedStarTables(new LinkedList<IrisStarTable>());
        this.setSelectedStarTables(new LinkedList<IrisStarTable>());
    }

    public void addPropertyChangeListener(PropertyChangeListener listener) {
        this.pcs.addPropertyChangeListener(listener);
    }

    public void removePropertyChangeListener(PropertyChangeListener listener) {
        this.pcs.removePropertyChangeListener(listener);
    }
    
    /**
     * Return a list of SedModels - in the same order as the SEDs appear in the the
     * selectedSeds list.
     * @return
     */
    public List<SedModel> getSedModels() {
        List<SedModel> models = new ArrayList<>();
        
        for (ExtSed sed : this.selectedSeds) {
            models.add(store.getSedModel(sed));
        }
        
        return models;
    }

    public SedModel getSedModel(ExtSed sed) {
        if (sed == null) {
            throw new IllegalArgumentException("Sed cannot be null");
        }
        return store.getSedModel(sed);
    }
    
    /**
     * Return a list of SegmentModels for the SED - in the same order as the Segments
     * appear in the SED.
     * @param sed
     * @return 
     */
    public List<SegmentModel> getModelsForSed(ExtSed sed) {
        
        SedModel sedModel = store.getSedModel(sed);
        List<SegmentModel> models = new ArrayList<>();
        
        // Return an empty list if the model has been removed from the store.
        // (meaning the Sed was removed from the workspace).
        if (sedModel == null) {
            return models;
        }
        
        for (int i=0; i<sed.getNumberOfSegments(); i++) {
            models.add(sedModel.getSegmentModel(sed.getSegment(i)));
        }
        
        return models;
    }
    
    /*
     * 
     * Getters and Setters
     * 
     */
    
    public String getDataModelTitle() {
        return dataModelTitle;
    }

    public synchronized void setDataModelTitle(String dataModelTitle) {
        String oldTitle = this.dataModelTitle;
        this.dataModelTitle = dataModelTitle;
        pcs.firePropertyChange(PROP_DATAMODEL_TITLE, oldTitle, dataModelTitle);
    }
    
    public List<ExtSed> getSelectedSeds() {
        return selectedSeds;
    }

    synchronized void setSelectedSeds(List<ExtSed> selectedSeds) {
        if (CollectionUtils.size(selectedSeds) > 1) {
            //TODO: Support lists of SEDs
            throw new IllegalArgumentException("Can only select 1 sed at a time");
        }
        List<ExtSed> oldSeds = this.selectedSeds;
        this.selectedSeds = ObservableCollections.observableList(selectedSeds);
        pcs.firePropertyChange(PROP_SELECTED_SEDS, oldSeds, selectedSeds);
    }
    
    public ExtSed getSelectedSed() {
        return selectedSed;
    }

    public synchronized void setSelectedSed(ExtSed selectedSed) {
        ExtSed oldSed = this.selectedSed;
        this.selectedSed = selectedSed;
        
        // Here to support empty values for null seds
        List<SegmentModel> newSedModels = new LinkedList<>();
        List<IrisStarTable> newSedTables = new LinkedList<>();
        List<ExtSed> newSelectedSeds = new LinkedList<>();
        String dataModelTitle = "";
        
        // TODO: Handle multiple SEDs
        if (selectedSed != null) {
            // Add models to the SED
            SedModel sedModel = store.getSedModel(selectedSed);
            for (int i = 0; i < selectedSed.getNumberOfSegments(); i++) {
                SegmentModel segModel = sedModel.getSegmentModel(selectedSed.getSegment(i));
                newSedModels.add(segModel);
                newSedTables.add(segModel.getInSource());
            }
            
            dataModelTitle = selectedSed.getId();
            newSelectedSeds.add(selectedSed);
        }
        
        // Update existing values
        this.setSedSegmentModels(newSedModels);
        this.setSedStarTables(newSedTables);
        this.setDataModelTitle(dataModelTitle);
        this.setSelectedSeds(newSelectedSeds);
        
        pcs.firePropertyChange(PROP_SELECTED_SED, oldSed, selectedSed);
    }
    
    public List<SegmentModel> getSedSegmentModels() {
        return sedSegmentModels;
    }
    
    // Locked down since these are tied to the selected seds
    synchronized void setSedSegmentModels(List<SegmentModel> newModels) {
        List<SegmentModel> oldModels = this.sedSegmentModels;
        this.sedSegmentModels = ObservableCollections.observableList(newModels);
        pcs.firePropertyChange(PROP_SED_SEGMENT_MODELS, oldModels, sedSegmentModels);
    }
    
    public List<IrisStarTable> getSedStarTables() {
        return sedStarTables;
    }
    
    // Locked down since these are tied to the selected seds
    public synchronized void setSedStarTables(List<IrisStarTable> newTables) {
        List<IrisStarTable> oldTables = sedStarTables;
        this.sedStarTables = ObservableCollections.observableList(newTables);
        pcs.firePropertyChange(PROP_SED_STARTABLES, oldTables, sedStarTables);
    }
    
    public List<IrisStarTable> getSelectedStarTables() {
        return selectedStarTables;
    }

    public synchronized void setSelectedStarTables(List<IrisStarTable> newStarTables) {
        List<IrisStarTable> oldStarTables = selectedStarTables;
        this.selectedStarTables = ObservableCollections.observableList(newStarTables);
        pcs.firePropertyChange(PROP_SELECTED_STARTABLES, oldStarTables, selectedStarTables);
    }
    
    /**
     * Can be used by external callers to property changes for the specified SED. Property 
     * changes will be fired if and only if the specified SED is in the list of currently
     * selected SEDs.
     * 
     * @param sed
     */
    public void fireChanges(ExtSed sed) {
        if (selectedSed == sed) {
            setSelectedSed(sed);
        }
    }
    
    public void refresh() {
        // This is a total cop-out. Just clear all existing preferences and reset
        // with the new selected SED.
        this.setSelectedSeds(new LinkedList<ExtSed>());
        this.setSedSegmentModels(new LinkedList<SegmentModel>());
        this.setSedStarTables(new LinkedList<IrisStarTable>());
        this.setSelectedStarTables(new LinkedList<IrisStarTable>());
        
        setSelectedSed(selectedSed);
    }
}
