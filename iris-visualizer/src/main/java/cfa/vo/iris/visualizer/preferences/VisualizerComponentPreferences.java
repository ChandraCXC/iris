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

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
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
import cfa.vo.iris.visualizer.plotter.PlotterView;
import cfa.vo.iris.visualizer.plotter.SegmentLayer;
import cfa.vo.sedlib.Segment;

/**
 * Single object location for data and preferences needed by the iris visualizer 
 * components.
 * 
 */
public class VisualizerComponentPreferences {
    
    PlotPreferences plotPreferences;
    StarTableAdapter<Segment> adapter;
    final IWorkspace ws;
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
     *  Preferences for the given SED
     */
    public SedPreferences getSedPreferences(ExtSed sed) {
        return sedPreferences.get(sed);
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
        
        return p.getAllSegmentPreferences().values();
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
            sedPreferences.get(sed).refresh();
        }
    }
    
    /**
     * This listener is responsible for detecting any changes in the SEDManger and firing
     * off the appropriate update events for each object in the visualizer component.
     *
     */
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
                sedPreferences.get(source).removeAll();
            }
            
            VisualizerChangeEvent.getInstance().fire(source, VisualizerCommand.RESET);
        }
    }
}
