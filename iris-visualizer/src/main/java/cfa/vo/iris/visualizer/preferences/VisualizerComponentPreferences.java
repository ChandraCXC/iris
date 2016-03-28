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
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import cfa.vo.iris.IWorkspace;
import cfa.vo.iris.events.MultipleSegmentEvent;
import cfa.vo.iris.events.MultipleSegmentListener;
import cfa.vo.iris.events.SedCommand;
import cfa.vo.iris.events.SedEvent;
import cfa.vo.iris.events.SedListener;
import cfa.vo.iris.events.SegmentEvent;
import cfa.vo.iris.events.SegmentEvent.SegmentPayload;
import cfa.vo.iris.events.SegmentListener;
import cfa.vo.iris.sed.ExtSed;
import cfa.vo.iris.visualizer.plotter.OtherPlotPreferences;
import cfa.vo.iris.visualizer.plotter.PlotPreferences;
import cfa.vo.iris.visualizer.plotter.SegmentLayer;
import cfa.vo.iris.visualizer.stil.tables.ColumnInfoMatcher;
import cfa.vo.iris.visualizer.stil.tables.IrisStarTableAdapter;
import cfa.vo.iris.visualizer.stil.tables.UtypeColumnInfoMatcher;
import cfa.vo.sedlib.Segment;

/**
 * Single object location for data and preferences needed by the iris visualizer 
 * components.
 * 
 */
public class VisualizerComponentPreferences {
    
    private static final ExecutorService visualizerExecutor = Executors.newFixedThreadPool(5);
    
    PlotPreferences plotPreferences;
    OtherPlotPreferences otherPlotPreferences;
    IrisStarTableAdapter adapter;
    ColumnInfoMatcher columnInfoMatcher;
    final IWorkspace ws;
    final Map<ExtSed, SedPreferences> sedPreferences;
    
    public VisualizerComponentPreferences(IWorkspace ws) {
        this.ws = ws;
        
        // TODO: change serialization when we have something that works
        this.adapter = new IrisStarTableAdapter(visualizerExecutor);
        
        // Create and add preferences for the SED
        this.sedPreferences = Collections.synchronizedMap(new IdentityHashMap<ExtSed, SedPreferences>());
        for (ExtSed sed : (List<ExtSed>) ws.getSedManager().getSeds()) {
            update(sed);
        }
        
        // TODO: Should this be in preferences?
        this.columnInfoMatcher = new UtypeColumnInfoMatcher();
        
        // Plotter global preferences
        if (this.sedPreferences.isEmpty()) {
            this.plotPreferences = PlotPreferences.getDefaultPlotPreferences();
            this.otherPlotPreferences = OtherPlotPreferences.getDefaultPreferences();
        } else {
            this.plotPreferences = this.getSelectedSedPreferences().getPlotPreferences();
            this.otherPlotPreferences = this.getSelectedSedPreferences().getOtherPlotPreferences();
        }
        
        // Add SED listener
        addSedListeners();
    }
    
    protected void addSedListeners() {
        SegmentEvent.getInstance().add(new VisualizerSegmentListener());
        SedEvent.getInstance().add(new VisualizerSedListener());
        MultipleSegmentEvent.getInstance().add(new VisualizerMultipleSegmentListener());
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
<<<<<<< HEAD
     *  ColumnInfoMatcher used in stacking star tables.
     */
    public ColumnInfoMatcher getColumnInfoMatcher() {
        return columnInfoMatcher;
=======
     *  non-STILTS top level plot preferences for the  plotter.
     */
    public OtherPlotPreferences getOtherPlotPreferences() {
        return otherPlotPreferences;
>>>>>>> Refactor StilPlotter and PlotterView classes. Fix PlotterView::setFixedViewPort.
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
        if (sedPreferences.containsKey(sed)) {
            sedPreferences.get(sed).refresh();
        } else {
            sedPreferences.put(sed, new SedPreferences(sed, adapter));
        }
        //fire(sed, VisualizerCommand.RESET);
    }
    
    /**
     * Adds or updates the segment within the specified SED.
     * @param sed - the sed to which the segment is attached
     * @param segment
     */
    public void update(ExtSed sed, Segment segment) {
        // Do nothing for null segments
        if (segment == null) return;
        
        if (sedPreferences.containsKey(sed)) {
            sedPreferences.get(sed).addSegment(segment);
        } else {
            // The segment will automatically be serialized and attached the the 
            // SedPrefrences since it's assumed to be attached to the SED.
            sedPreferences.put(sed, new SedPreferences(sed, adapter));
        }
        
        // added to fix issue when plot view should be fixed
//        if (this.getSelectedSedPreferences().getPlotPreferences().getFixed()) {
//            fire(sed, VisualizerCommand.REDRAW);
//        } else {
        fire(sed, VisualizerCommand.RESET);
//        }
    }
    
    /**
     * Adds or updates the segments within the specified SED.
     * @param sed - the sed to which the segment is attached
     * @param segments - the list of segments to add
     */
    public void update(ExtSed sed, List<Segment> segments) {
        if (sedPreferences.containsKey(sed)) {
            for (Segment segment : segments) {
                // Do nothing for null segments
                if (segment == null) continue;
                sedPreferences.get(sed).addSegment(segment);
            }
        } else {
            // The segment will automatically be serialized and attached the the 
            // SedPrefrences since it's assumed to be attached to the SED.
            sedPreferences.put(sed, new SedPreferences(sed, adapter));
        }
        // added to fix issue when plot view should be fixed
//        if (this.getSelectedSedPreferences().getPlotPreferences().getFixed()) {
//            fire(sed, VisualizerCommand.REDRAW);
//        } else {
            fire(sed, VisualizerCommand.RESET);
//        }
    }
    
    /**
     * Removes the SED from the preferences map.
     * @param sed
     */
    public void remove(ExtSed sed) {
        if (!sedPreferences.containsKey(sed)) {
            return;
        }
        sedPreferences.get(sed).removeAll();
        sedPreferences.remove(sed);
        //fire(sed, VisualizerCommand.RESET);
    }
    
    /**
     * Removes the segment from the specified Sed Preferences map.
     * @param sed
     * @param segment
     */
    public void remove(ExtSed sed, Segment segment) {
        // Do nothing for null segments
        if (segment == null) return;
        
        if (sedPreferences.containsKey(sed)) {
            sedPreferences.get(sed).removeSegment(segment);
        }
        
        fire(sed, VisualizerCommand.RESET);
    }
    
    /**
     * Removes the segments from the specified Sed Preferences map.
     * @param sed
     * @param segments
     */
    public void remove(ExtSed sed, List<Segment> segments) {
        if (sedPreferences.containsKey(sed)) {
            for (Segment segment : segments) {
                // Do nothing for null segments
                if (segment == null) continue;
                sedPreferences.get(sed).removeSegment(segment);
            }
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
            try {
                processNotification(sed, payload);
            } catch (Exception e) {
                // TODO: This happens asynchronously, what should we do with exceptions?
                e.printStackTrace();
            }
        }
        
        private void processNotification(ExtSed sed, SedCommand payload) {
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
                fire(sed, VisualizerCommand.SELECTED);
            }
            else {
                // Doesn't merit a full reset, this is basically just here for SED name changes
                fire(sed, VisualizerCommand.REDRAW); // should remove this
            }
        }
    }
    
    private class VisualizerSegmentListener implements SegmentListener {

        @Override
        public void process(Segment segment, SegmentPayload payload) {
            try {
                processNotification(segment, payload);
            } catch (Exception e) {
                // TODO: This happens asynchronously, what should we do with exceptions?
                e.printStackTrace();
            }
        }
        
        private void processNotification(Segment segment, SegmentPayload payload) {
            
            ExtSed sed = payload.getSed();
            SedCommand command = payload.getSedCommand();
            
            // Update the SED with the new or updated segment
            if (SedCommand.ADDED.equals(command) ||
                SedCommand.CHANGED.equals(command))
            {
                update(sed, segment);
            }
            
            // Remove the deleted segment from the SED
            else if (SedCommand.REMOVED.equals(command)) {
                remove(sed, segment);
            }
        }
    }
    
    private class VisualizerMultipleSegmentListener implements MultipleSegmentListener {
        
        @Override
        public void process(java.util.List<Segment> segments, SegmentPayload payload) {
            try {
                processNotification(segments, payload);
            } catch (Exception e) {
                // TODO: This happens asynchronously, what should we do with
                // exceptions?
                e.printStackTrace();
            }
        }
        
        private void processNotification(java.util.List<Segment> segments, SegmentPayload payload) {
            ExtSed sed = payload.getSed();
            SedCommand command = payload.getSedCommand();
            
            // Update the SED with the new or updated segments
            if (SedCommand.ADDED.equals(command) ||
                SedCommand.CHANGED.equals(command))
            {
                update(sed, segments);
            }
            
            // Remove the deleted segments from the SED
            else if (SedCommand.REMOVED.equals(command)) {
                remove(sed, segments);
            }
            // update plot preferences
            VisualizerComponentPreferences.this.plotPreferences = 
                    VisualizerComponentPreferences.this.getSelectedSedPreferences().getPlotPreferences();
        }
    }
}
