/*
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
import java.util.logging.Level;
import java.util.logging.Logger;

import cfa.vo.iris.IWorkspace;
import cfa.vo.iris.events.MultipleSegmentEvent;
import cfa.vo.iris.events.MultipleSegmentListener;
import cfa.vo.iris.events.SedCommand;
import cfa.vo.iris.events.SedEvent;
import cfa.vo.iris.events.SedListener;
import cfa.vo.iris.events.SegmentEvent;
import cfa.vo.iris.events.SegmentListener;
import cfa.vo.iris.gui.NarrowOptionPane;
import cfa.vo.iris.sed.ExtSed;
import cfa.vo.sedlib.Segment;

public class VisualizerEventListener {
    
    private static final Logger logger = Logger.getLogger(VisualizerEventListener.class.getName());
    
    // Data store
    private final VisualizerDataStore dataStore;
    
    // Preferences
    private final VisualizerComponentPreferences preferences;
    
    // Workspace for errors
    private final IWorkspace ws;
    
    public VisualizerEventListener(VisualizerComponentPreferences preferences, IWorkspace ws) {
        
        this.preferences = preferences;
        this.dataStore = preferences.getDataStore();
        this.ws = ws;
        
        addSedListeners();
    }
    
    void addSedListeners() {
        SegmentEvent.getInstance().add(new VisualizerSegmentListener());
        SedEvent.getInstance().add(new VisualizerSedListener());
        MultipleSegmentEvent.getInstance().add(new VisualizerMultipleSegmentListener());
    }

    
    /**
     * These listeners are responsible for detecting any changes in the SEDManger and firing
     * off the appropriate update events for each object in the visualizer component.
     *
     */
    private class VisualizerSedListener implements SedListener {
        
        @Override
        public void process(final ExtSed sed, final SedCommand payload) {
            retryEvent(new Callable<Boolean>() {
                @Override
                public Boolean call() throws Exception {
                    processNotification(sed, payload);
                    return true;
                }
            });
        }
        
        private void processNotification(ExtSed sed, SedCommand payload) {
            // Only take actions if an SED was added, removed, or selected.
            // Rely on Segment events to pick up changes within an SED.
            if (SedCommand.ADDED.equals(payload))
            {
                dataStore.update(sed);
            }
            else if (SedCommand.REMOVED.equals(payload)) {
                dataStore.remove(sed);
            } 
            else if (SedCommand.SELECTED.equals(payload)) {
                // TODO: Revist the idea of a "frozen" workspace, some work removed by 5e7bc42
                preferences.updateSelectedSed(sed);
            }
            else {
                // Doesn't merit a full reset, this is basically just here for SED name changes
                preferences.fireChanges(sed);
            }
        }
    }
    
    private class VisualizerSegmentListener implements SegmentListener {

        @Override
        public void process(final Segment segment, final SegmentEvent.SegmentPayload payload) {
            retryEvent(new Callable<Boolean>() {
                @Override
                public Boolean call() throws Exception {
                    processNotification(segment, payload);
                    return true;
                }
            });
        }
        
        private void processNotification(Segment segment, SegmentEvent.SegmentPayload payload) {
            
            ExtSed sed = payload.getSed();
            SedCommand command = payload.getSedCommand();
            
            // Update the SED with the new or updated segment
            if (SedCommand.ADDED.equals(command) ||
                SedCommand.CHANGED.equals(command))
            {
                dataStore.update(sed, segment);
            }
            
            // Remove the deleted segment from the SED
            else if (SedCommand.REMOVED.equals(command)) {
                dataStore.remove(sed, segment);
            }
        }
    }
    
    private class VisualizerMultipleSegmentListener implements MultipleSegmentListener {
        
        @Override
        public void process(final java.util.List<Segment> segments, final SegmentEvent.SegmentPayload payload) {
            retryEvent(new Callable<Boolean>() {
                @Override
                public Boolean call() throws Exception {
                    processNotification(segments, payload);
                    return true;
                }
            });
        }
        
        private void processNotification(java.util.List<Segment> segments, SegmentEvent.SegmentPayload payload) {
            ExtSed sed = payload.getSed();
            SedCommand command = payload.getSedCommand();
            
            // Update the SED with the new or updated segments
            if (SedCommand.ADDED.equals(command) ||
                SedCommand.CHANGED.equals(command))
            {
                dataStore.update(sed, segments);
            }
            
            // Remove the deleted segments from the SED
            else if (SedCommand.REMOVED.equals(command)) {
                dataStore.remove(sed, segments);
            }
        }
    }
    
    /**
     * Since event processing happens asynchronously, we want to retry in cases of failure. Show 
     * a popup to alert a failure if we cannot process the event.
     * 
     * @param call
     */
    private void retryEvent(Callable<?> call) {
        Exception last = null;
        for (int i=0; i<3; i++) {
            try {
                call.call();
                return;
            } catch (Exception e) {
                last = e;
            }
        }
        
        logger.log(Level.SEVERE, "Error processing SedEvent", last);
        NarrowOptionPane.showMessageDialog(ws.getDesktop(),
                "Error processing SedEvent in the Iris Visualizer, you may want to refresh, or reload your data:\n " +
                        last.getMessage(),
                        "Error",
                        NarrowOptionPane.ERROR_MESSAGE);
    }
}
