package cfa.vo.iris.visualizer.preferences;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;

import cfa.vo.iris.IWorkspace;
import cfa.vo.iris.events.SedCommand;
import cfa.vo.iris.events.SedEvent;
import cfa.vo.iris.events.SedListener;
import cfa.vo.iris.sed.ExtSed;
import cfa.vo.iris.sed.stil.SegmentStarTableAdapter;
import cfa.vo.iris.sed.stil.StarTableAdapter;
import cfa.vo.iris.visualizer.plotter.PlotPreferences;
import cfa.vo.iris.visualizer.plotter.SegmentLayer;
import cfa.vo.sedlib.Segment;

/**
 * Single object location for data and preferences needed by the iris visualizer 
 * components.
 * 
 */
public class VisualizerComponentPreferences {
    
    private PlotPreferences plotPreferences;
    private StarTableAdapter<Segment> adapter;
    private IWorkspace ws;
    final Map<ExtSed, SedPreferences> sedPreferences;
    
    public VisualizerComponentPreferences(IWorkspace ws) {
        this.ws = ws;
        
        // TODO: change serialization when we have something that works
        this.adapter = new SegmentStarTableAdapter();
        
        // Create and add preferences for the SED
        this.sedPreferences = Collections.synchronizedMap(new WeakHashMap<ExtSed, SedPreferences>());
        for (ExtSed sed : (List<ExtSed>) ws.getSedManager().getSeds()) {
            update(sed);
        }
        
        // Plotter global preferences
        this.plotPreferences = PlotPreferences.getDefaultPlotPreferences();
        
        SedEvent.getInstance().add(new VisualizerSedListener());
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
     *  Preferences map for each SED.
     */
    public Map<ExtSed, SedPreferences> getSedPreferences() {
        return sedPreferences;
    }

    /**
     * @return
     *  Preferences for the currently selected SED in the workspace.
     */
    public SedPreferences getSelectedSedPreferences() {
        return sedPreferences.get((ExtSed) ws.getSedManager().getSelected());
    }
    

    /**
     * @return
     *  Collection of all the segment layers attached to the currently selected SED.
     */
    public Collection<SegmentLayer> getSelectedLayers() {
        SedPreferences p = getSelectedSedPreferences();
        if (p == null) {
            return Collections.emptyList();
        }
        
        return p.getSegmentPreferences().values();
    }

    /**
     * @return
     *  The Segment -> StarTable adapter currently in use in the workspace.
     */
    public StarTableAdapter<Segment> getAdapter() {
        return adapter;
    }

    private void update(ExtSed sed) {
        if (!sedPreferences.containsKey(sed)) {
            sedPreferences.put(sed, new SedPreferences(sed, adapter));
        } else {
            sedPreferences.get(sed).refresh(sed);
        }
    }
    
    private class VisualizerSedListener implements SedListener {
        
        @Override
        public void process(ExtSed source, SedCommand payload) {
            if (payload.equals(SedCommand.ADDED) ||
                payload.equals(SedCommand.CHANGED))
            {
                update(source);
            }
            
            else if (payload.equals(SedCommand.REMOVED)) {
                sedPreferences.remove(source);
                sedPreferences.get(source).remove(source);
            }
        }
    }
}
