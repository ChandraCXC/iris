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
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;

import cfa.vo.iris.IWorkspace;
import cfa.vo.iris.events.SedCommand;
import cfa.vo.iris.events.SedEvent;
import cfa.vo.iris.events.SedListener;
import cfa.vo.iris.events.SegmentEvent;
import cfa.vo.iris.events.SegmentEvent.SegmentPayload;
import cfa.vo.iris.events.SegmentListener;
import cfa.vo.iris.sed.ExtSed;
import cfa.vo.iris.visualizer.plotter.PlotPreferences;
import cfa.vo.iris.visualizer.plotter.SegmentLayer;
import cfa.vo.iris.visualizer.stil.IrisStarTableAdapter;
import cfa.vo.sedlib.Segment;

/**
 * Single object location for data and preferences needed by the iris visualizer 
 * components.
 * 
 */
public class VisualizerComponentPreferences {
    
    PlotPreferences plotPreferences;
    IrisStarTableAdapter adapter;
    final IWorkspace ws;
    final Map<ExtSed, SedPreferences> sedPreferences;
    
    public VisualizerComponentPreferences(IWorkspace ws) {
        this.ws = ws;
        
        // TODO: change serialization when we have something that works
        this.adapter = new IrisStarTableAdapter();
        
        // Create and add preferences for the SED
        this.sedPreferences = Collections.synchronizedMap(new WeakHashMap<ExtSed, SedPreferences>());
        for (ExtSed sed : (List<ExtSed>) ws.getSedManager().getSeds()) {
            update(sed);
        }
        
        // Plotter global preferences
        this.plotPreferences = PlotPreferences.getDefaultPlotPreferences();
        
        // Add SED listener
        addSedListeners();
    }
    
    protected void addSedListeners() {
        SegmentEvent.getInstance().add(new VisualizerSegmentListener());
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
     *  Preferences for the currently selected SED in the workspace.
     */
    public SedPreferences getSelectedSedPreferences() {
        return sedPreferences.get((ExtSed) ws.getSedManager().getSelected());
    }

    /**
     * @return
     *  The Segment -> StarTable adapter currently in use in the workspace.
     */
    public IrisStarTableAdapter getAdapter() {
        return adapter;
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
     * Adds or updates the SED to the preferences map.
     * @param sed
     */
    public void update(ExtSed sed) {
        if (!sedPreferences.containsKey(sed)) {
            sedPreferences.put(sed, new SedPreferences(sed, adapter));
        } else {
            sedPreferences.get(sed).refresh();
        }
        fire(sed, VisualizerCommand.RESET);
    }
    
    /**
     * Adds or updates the segment within the specified SED.
     * @param sed
     * @param segment
     */
    public void update(ExtSed sed, Segment segment) {
        if (!sedPreferences.containsKey(sed)) {
            sedPreferences.put(sed, new SedPreferences(sed, adapter));
        } else {
            sedPreferences.get(sed).addSegment(segment);
        }
        fire(sed, VisualizerCommand.RESET);
    }
    
    /**
     * Removes the SED from the preferences map.
     * @param sed
     */
    public void remove(ExtSed sed) {
        sedPreferences.remove(sed);
        sedPreferences.get(sed).removeAll();
        fire(sed, VisualizerCommand.RESET);
    }
    
    /**
     * Removes the segment from the specified Sed Preferences map.
     * @param sed
     * @param segment
     */
    public void remove(ExtSed sed, Segment segment) {
        if (sedPreferences.containsKey(sed)) {
            sedPreferences.get(sed).removeSegment(segment);
        }
        
        fire(sed, VisualizerCommand.RESET);
    }
    
    protected void fire(ExtSed source, VisualizerCommand command) {
        VisualizerChangeEvent.getInstance().fire(source, command);
    }
    
    /**
     * These listeners are responsible for detecting any changes in the SEDManger and firing
     * off the appropriate update events for each object in the visualizer component.
     *
     */
    private class VisualizerSedListener implements SedListener {
        
        @Override
        public void process(ExtSed sed, SedCommand payload) {
            
            // Only take actions if an SED was added, removed, or selected.
            // Rely on Segment events to pick up changes within an SED.
            if (SedCommand.ADDED.equals(payload))
            {
                update(sed);
            }
            else if (SedCommand.REMOVED.equals(payload)) {
                remove(sed);
            }
            else if (SedCommand.SELECTED.equals(payload)) {
                fire(sed, VisualizerCommand.RESET);
            }
            else {
                // Doesn't merit a full reset, this is basically just here for SED name changes
                fire(sed, VisualizerCommand.REDRAW);
            }
        }
    }
    
    private class VisualizerSegmentListener implements SegmentListener {

        @Override
        public void process(Segment segment, SegmentPayload payload) {
            ExtSed sed = payload.getSed();
            SedCommand command = payload.getSedCommand();
            
            if (SedCommand.ADDED.equals(command) ||
                    SedCommand.CHANGED.equals(command)) 
            {
                update(sed, segment);
            }
            else if (SedCommand.REMOVED.equals(command)) {
                remove(sed, segment);
            }
//            else {
//                fire(sed, VisualizerCommand.RESET);
//            }
        }
    }
}
