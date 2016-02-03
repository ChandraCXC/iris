package cfa.vo.iris.visualizer.preferences;

import java.util.Collections;
import java.util.Map;
import java.util.WeakHashMap;

import cfa.vo.iris.IWorkspace;
import cfa.vo.iris.events.SedCommand;
import cfa.vo.iris.events.SedListener;
import cfa.vo.iris.sed.ExtSed;
import cfa.vo.iris.sed.stil.SegmentStarTableAdapter;
import cfa.vo.iris.sed.stil.StarTableAdapter;
import cfa.vo.iris.visualizer.plotter.PlotPreferences;
import cfa.vo.sedlib.Segment;

/**
 * Single object location for data and preferences needed by the iris visualizer 
 * components.
 * 
 */
public class VisualizerComponentPreferences {
    
    private PlotPreferences plotPreferences;
    private StarTableAdapter<Segment> adapter;
    private final Map<ExtSed, SedPreferences> sedPreferences;
    
    public VisualizerComponentPreferences(IWorkspace ws) {
        this.sedPreferences = Collections.synchronizedMap(new WeakHashMap<ExtSed, SedPreferences>());
        
        // TODO: change this
        this.adapter = new SegmentStarTableAdapter();
    }
    
    public PlotPreferences getPlotPreferences() {
        return plotPreferences;
    }

    public Map<ExtSed, SedPreferences> getSedPreferences() {
        return sedPreferences;
    }

    public StarTableAdapter<Segment> getAdapter() {
        return adapter;
    }

    private void update(ExtSed sed) {
        if (!this.sedPreferences.containsKey(sed)) {
            this.sedPreferences.put(sed, new SedPreferences(sed, adapter));
        }
    }
    
    private class VisualizerSedListener implements SedListener {
        @Override
        public void process(ExtSed source, SedCommand payload) {
            update(source);
        }
    }
}
