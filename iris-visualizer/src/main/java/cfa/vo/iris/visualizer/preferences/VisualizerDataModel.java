package cfa.vo.iris.visualizer.preferences;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.LinkedList;
import java.util.List;

import cfa.vo.iris.fitting.FittingRange;
import cfa.vo.iris.sed.ExtSed;
import cfa.vo.iris.visualizer.plotter.ColorPalette;
import cfa.vo.iris.visualizer.plotter.HSVColorPalette;
import cfa.vo.iris.visualizer.plotter.PlotPreferences;
import cfa.vo.iris.visualizer.stil.tables.IrisStarTable;

import java.util.ArrayList;
import java.util.Arrays;
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
    public static final String PROP_FUNCTION_MODELS = "functionModels";

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
    
    // Xunits for StarTables (default to Angstrom)
    private String xUnits = "Angstrom";
    
    // Yunits for StarTables (default to erg cm**(-2) s**(-1) angstrom**(-1))
    private String yUnits = "erg/s/cm2/Angstrom";
    
    // list of FunctionModels associated with selectedSeds. These tables will be overplotted
    // as solid lines
    private List<FunctionModel> functionModels;
    
    // List of fitting ranges corresponding to the fit functions
    private List<FittingRange> fittingRanges;
    
    private boolean coplotted = false;
    
    public VisualizerDataModel(VisualizerComponentPreferences prefs) {
        this.store = prefs.getDataStore();
        this.preferences = prefs;
        this.pcs = new PropertyChangeSupport(this);
        
        this.setSelectedSeds(new LinkedList<ExtSed>());
        this.setLayerModels(new LinkedList<LayerModel>());
        this.setSedStarTables(new LinkedList<IrisStarTable>());
        this.setSelectedStarTables(new LinkedList<IrisStarTable>());
        this.setFunctionModels(new LinkedList<FunctionModel>());
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
    
    public boolean isCoplotted() {
        return this.coplotted;
    }
    
    // TODO: should we allow SEDs to have multiple FunctionModels attached?
    // If so, we should add a "getFunctionModelsForSed()" here.
    
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
        this.coplotted = CollectionUtils.size(selectedSeds) > 1;
        
        // Here to support empty values for null seds
        List<LayerModel> newSedModels = new LinkedList<>();
        List<IrisStarTable> newSedTables = new LinkedList<>();
        StringBuilder dataModelTitle = new StringBuilder();
        List<FunctionModel> newFunctionModels = new LinkedList<>();
        this.fittingRanges = new LinkedList<>();
        
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
            newSedTables.addAll(sedModel.getDataTables());
            dataModelTitle.append(sed.getId() + " ");

            // For coplotting we plot the entire SED as a single layer
            if (coplotted) {
                newSedModels.add(sedModel.getSedLayerModel());
            }
            // Otherwise we add a single layer for each corresponding segment
            else {
                newSedModels.addAll(sedModel.getLayerModels());
            }
            
            // set a FunctionModel
            // TODO: handle setting multiple function models
            // TODO: Better if check for present fits ('if SED has FunctionModel')
            FunctionModel model = sedModel.getFunctionModel();
            if (model.hasModelValues()) {
                newFunctionModels.add(model);
            }
            
            // Add fitting ranges, if available AND we are not coplotting
            if (!coplotted && sed.getFit() != null) {
                fittingRanges.addAll(sed.getFit().getFittingRanges());
            }
        }
        this.selectedSeds = ObservableCollections.observableList(selectedSeds);
        
        // Update units and colors
        updateUnits();
        updateColors(newSedModels);
        
        // Update existing values
        this.setLayerModels(newSedModels);
        this.setSedStarTables(newSedTables);
        this.setDataModelTitle(dataModelTitle.toString());
        this.setFunctionModels(newFunctionModels);
        
        pcs.firePropertyChange(PROP_SELECTED_SEDS, oldSeds, selectedSeds);
    }
    
    /**
     * Simple function for setting a single selected SED, this is only for SETTING
     * an SED, does not support binding!
     */
    public void setSelectedSed(ExtSed sed) {
        this.setSelectedSeds(Arrays.asList(sed));
    }
    
    private void updateColors(List<LayerModel> layers) {
        // If we are not coplotting then don't mess with the preset layer colors
        if (!coplotted) return;
        
        // Otherwise we need to assign different colors to each layer.
        ColorPalette cp = new HSVColorPalette();
        for (LayerModel layer : layers) {
            String hexColor = ColorPalette.colorToHex(cp.getNextColor());
            layer.setErrorColor(hexColor);
            layer.setMarkColor(hexColor);
        }
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
    
    public List<FunctionModel> getFunctionModels() {
        return functionModels;
    }
    
    public List<FittingRange> getFittingRanges() {
        return fittingRanges;
    }
    
    /**
     * Locked down as these are currently attached to the selectedSeds
     */
    private synchronized void setFunctionModels(List<FunctionModel> newFunctionModels) {
        List<FunctionModel> oldFunctionModels = functionModels;
        this.functionModels = ObservableCollections.observableList(newFunctionModels);
        pcs.firePropertyChange(PROP_FUNCTION_MODELS, oldFunctionModels, functionModels);
    }

    public void refresh() {
        // This is a total cop-out. Just clear all existing preferences and reset
        // with the new selected SED.
        List<ExtSed> oldSeds = this.selectedSeds;
        
        this.setSelectedSeds(new LinkedList<ExtSed>());
        this.setLayerModels(new LinkedList<LayerModel>());
        this.setSedStarTables(new LinkedList<IrisStarTable>());
        this.setSelectedStarTables(new LinkedList<IrisStarTable>());
        this.setFunctionModels(new LinkedList<FunctionModel>());
        
        setSelectedSeds(oldSeds);
    }
    
    public void setUnits(String xunit, String yunit) {
        for (ExtSed sed : this.selectedSeds) {
            this.getSedModel(sed).setUnits(xunit, yunit);
        }
        
        this.setXunits(xunit);
        this.setYunits(yunit);
        
        PlotPreferences pp = preferences.getPlotPreferences(selectedSeds);
        pp.setXUnits(xunit);
        pp.setYUnits(yunit);
    }
    
    /**
     * This method should be called when we want to explicitly bring the version numbers
     * of each SedModel's fitting and evaluated models up to date with the latest version
     * of the Sed. In particular, when changing units we want the version numbers to update.
     * Or when the user explicitly asks to ignore future warnings of the model being out 
     * of date.
     * 
     */
    public void updateFittingVersionNumbers() {
        for (SedModel model : getSedModels()) {
            int version = model.computeVersion();
            if (model.getHasModelFunction()) {
                model.setModelVersion(version);
            }
            if (model.getSed().getFit() != null) {
                model.getSed().getFit().setSedVersion(version);
            }
        }
    }
    
    public String getXunits() {
        return xUnits;
    }
    
    private void setXunits(String xunits) {
        String old = this.xUnits;
        this.xUnits = xunits;
        pcs.firePropertyChange(PROP_XUNITS, old, xunits);
    }
    
    public String getYunits() {
        return yUnits;
    }
    
    private void setYunits(String yunits) {
        String old = this.yUnits;
        this.yUnits = yunits;
        pcs.firePropertyChange(PROP_YUNITS, old, yunits);
    }
}
