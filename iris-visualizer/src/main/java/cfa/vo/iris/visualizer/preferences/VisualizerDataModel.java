package cfa.vo.iris.visualizer.preferences;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.LinkedList;
import java.util.List;

import cfa.vo.iris.sed.ExtSed;
import cfa.vo.iris.visualizer.plotter.PlotPreferences;
import cfa.vo.iris.visualizer.stil.tables.IrisStarTable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.jdesktop.observablecollections.ObservableCollections;

/**
 * Dynamic model for the plotter and metadata browser. Maintains the current state
 * of the Visualizer component.
 *
 */
public class VisualizerDataModel {
    
    public static final String PROP_DATAMODEL_TITLE = "dataModelTitle";
    public static final String PROP_SELECTED_SEDS = "selectedSeds";
    public static final String PROP_LAYER_MODELS = "layerModels";
    public static final String PROP_SED_STARTABLES = "sedStarTables";
    public static final String PROP_SELECTED_STARTABLES = "selectedStarTables";
    public static final String PROP_XUNITS = "xunits";
    public static final String PROP_YUNITS = "yunits";

    private final PropertyChangeSupport pcs;
    private final VisualizerDataStore store;
    private final VisualizerComponentPreferences preferences;

    // Name of the window browser, is adjustable and currently tied to the list of selected Seds
    private String dataModelTitle = null;

    // Seds to display in the visualizer
    private List<ExtSed> selectedSeds;

    // List of LayerModels to be used in the plotter, a layer can either be an entire SED or a
    // single segment, depending on user preferences.
    private List<LayerModel> layerModels;
    
    // list of star tables associated with selectedSeds, these tables will all be plotted. May
    // not be in 1-1 correspondence with the LayerModels.
    private List<IrisStarTable> sedStarTables;
    
    // list of selected StarTables from selectedTables, or which star tables are shown in the 
    // Metadata browser
    private List<IrisStarTable> selectedStarTables;
    
    // Xunits for StarTables
    private String xUnits = "";
    
    // Yunits for StarTables
    private String yUnits = "";
    
    // Plot preferences for use by the stil plotter
    
    public VisualizerDataModel(VisualizerComponentPreferences prefs) {
        this.store = prefs.getDataStore();
        this.preferences = prefs;
        this.pcs = new PropertyChangeSupport(this);
        
        this.setSelectedSeds(new LinkedList<ExtSed>());
        this.setLayerModels(new LinkedList<LayerModel>());
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
    public List<LayerModel> getModelsForSed(ExtSed sed) {
        SedModel sedModel = store.getSedModel(sed);
        if (sedModel == null) {
            return new ArrayList<>();
        }
        return sedModel.getLayerModels();
    }
    
    /**
     * Return a list of IrisStarTables for the SED - in the same order as the Segments
     * appear in the SED.
     * @param sed
     * @return
     */
    public List<IrisStarTable> getStarTablesForSed(ExtSed sed) {
        SedModel sedModel = store.getSedModel(sed);
        if (sedModel == null) {
            return new ArrayList<>();
        }
        return sedModel.getDataTables();
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

    public synchronized void setSelectedSeds(List<ExtSed> selectedSeds) {
        List<ExtSed> oldSeds = this.selectedSeds;
        
        // Here to support empty values for null seds
        List<LayerModel> newSedModels = new LinkedList<>();
        List<IrisStarTable> newSedTables = new LinkedList<>();
        StringBuilder dataModelTitle = new StringBuilder();
        
        Iterator<ExtSed> it = selectedSeds.iterator();
        while (it.hasNext()) {
            
            // this list supports null entries, remove them if present
            ExtSed sed = it.next();
            if (sed == null) {
                it.remove();
                continue;
            }
            
            // Add models to the SED
            SedModel sedModel = store.getSedModel(sed);
            newSedModels.addAll(sedModel.getLayerModels());
            newSedTables.addAll(sedModel.getDataTables());
            dataModelTitle.append(sed.getId() + " ");
        }
        this.selectedSeds = ObservableCollections.observableList(selectedSeds);
        
        // TODO: Colors, etc... Also, how can we make units preferences easier?!?
        updateUnits();
        
        // Update existing values
        this.setLayerModels(newSedModels);
        this.setSedStarTables(newSedTables);
        this.setDataModelTitle(dataModelTitle.toString());
        
        pcs.firePropertyChange(PROP_SELECTED_SEDS, oldSeds, selectedSeds);
    }
    
    private void updateUnits() {

        // If unit preferences are set, use them. Otherwise apply them.
        PlotPreferences pp = preferences.getPlotPreferences(selectedSeds);
        String xunits = pp.getXUnits();
        String yunits = pp.getYUnits();
        if (StringUtils.isNotBlank(xunits) && StringUtils.isNotBlank(yunits)) {
            setUnits(xunits, yunits);
            return;
        }
        
        // Otherwise match the units to the first SED in the list (if available)
        else if (CollectionUtils.size(selectedSeds) > 0) {
            SedModel model = store.getSedModel(selectedSeds.get(0));
            xunits = model.getXUnits();
            yunits = model.getYUnits();
            if (StringUtils.isNotBlank(xunits) && StringUtils.isNotBlank(yunits)) {
                setUnits(xunits, yunits);
                pp.setXUnits(model.getXUnits());
                pp.setYUnits(model.getYUnits());
                return;
            }
        }
        
        // Otherwise there are no SEDs in the model, so units should stay blank.
    }
    
    public List<LayerModel> getLayerModels() {
        return Collections.unmodifiableList(layerModels);
    }
    
    // Locked down since these are tied to the selected seds
    synchronized void setLayerModels(List<LayerModel> newModels) {
        List<LayerModel> oldModels = this.layerModels;
        this.layerModels = ObservableCollections.observableList(newModels);
        pcs.firePropertyChange(PROP_LAYER_MODELS, oldModels, layerModels);
    }
    
    public List<IrisStarTable> getSedStarTables() {
        return Collections.unmodifiableList(sedStarTables);
    }
    
    // Locked down since these are tied to the selected seds
    synchronized void setSedStarTables(List<IrisStarTable> newTables) {
        List<IrisStarTable> oldTables = sedStarTables;
        this.sedStarTables = ObservableCollections.observableList(newTables);
        pcs.firePropertyChange(PROP_SED_STARTABLES, oldTables, sedStarTables);
    }
    
    public List<IrisStarTable> getSelectedStarTables() {
        return Collections.unmodifiableList(selectedStarTables);
    }

    public synchronized void setSelectedStarTables(List<IrisStarTable> newStarTables) {
        List<IrisStarTable> oldStarTables = selectedStarTables;
        this.selectedStarTables = ObservableCollections.observableList(newStarTables);
        pcs.firePropertyChange(PROP_SELECTED_STARTABLES, oldStarTables, selectedStarTables);
    }
    
    public void refresh() {
        // This is a total cop-out. Just clear all existing preferences and reset
        // with the new selected SED.
        List<ExtSed> oldSeds = this.selectedSeds;
        
        this.setSelectedSeds(new LinkedList<ExtSed>());
        this.setLayerModels(new LinkedList<LayerModel>());
        this.setSedStarTables(new LinkedList<IrisStarTable>());
        this.setSelectedStarTables(new LinkedList<IrisStarTable>());
        
        setSelectedSeds(oldSeds);
    }
    
    public void setUnits(String xunit, String yunit) {
        for (ExtSed sed : this.selectedSeds) {
            this.getSedModel(sed).setUnits(xunit, yunit);
        }
        
        this.setXUnits(xunit);
        this.setYUnits(yunit);
        
        PlotPreferences pp = preferences.getPlotPreferences(selectedSeds);
        pp.setXUnits(xunit);
        pp.setYUnits(yunit);
    }
    
    public String getXUnits() {
        return xUnits;
    }
    
    private void setXUnits(String xunits) {
        String old = this.xUnits;
        this.xUnits = xunits;
        pcs.firePropertyChange(PROP_XUNITS, old, xunits);
    }
    
    public String getYUnits() {
        return yUnits;
    }
    
    private void setYUnits(String yunits) {
        String old = this.yUnits;
        this.yUnits = yunits;
        pcs.firePropertyChange(PROP_YUNITS, old, yunits);
    }
}
