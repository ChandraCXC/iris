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
package cfa.vo.iris.visualizer.plotter;

import org.junit.Test;

import cfa.vo.iris.IWorkspace;
import cfa.vo.iris.sed.ExtSed;
import cfa.vo.iris.test.unit.StubWorkspace;
import cfa.vo.iris.test.unit.TestUtils;
import cfa.vo.iris.visualizer.preferences.VisualizerComponentPreferences;

import static org.junit.Assert.*;

import java.util.Arrays;

import uk.ac.starlink.ttools.plot2.task.PointSelectionEvent;

public class PlotPointSelectionTest {

    @Test
    public void testStilPointSelectionMappingNonCoplot() throws Exception {
        IWorkspace ws = new StubWorkspace();
        VisualizerComponentPreferences prefs = new VisualizerComponentPreferences(ws);
        
        StubListener listener = new StubListener();
        listener.dataModel = prefs.getDataModel();
        
        PointSelectionEvent evt = createEvent(new long[] {});
        listener.pointSelected(evt);
        
        // Should not have been called since there's nothing in the dataModel
        if (listener.called) fail();
        
        // Add sed to the model
        ExtSed sed = new ExtSed("test");
        sed.addSegment(TestUtils.createSampleSegment());
        sed.addSegment(TestUtils.createSampleSegment());
        prefs.getDataStore().update(sed);
        prefs.getDataModel().setSelectedSeds(Arrays.asList(sed));
        
        // Should return second starTable, last row
        evt = createEvent(new long[] {-1,-1,2,2});
        listener.setExpectations(1, 2);
        listener.pointSelected(evt);
        
        // Should return first starTable, first row
        evt = createEvent(new long[] {1,1,2,2});
        listener.setExpectations(0, 1);
        listener.pointSelected(evt);
        
        if (!listener.called) fail();
    }
    
    @Test
    public void testCoplotPointSelection() throws Exception {
        IWorkspace ws = new StubWorkspace();
        VisualizerComponentPreferences prefs = new VisualizerComponentPreferences(ws);
        
        StubListener listener = new StubListener();
        listener.dataModel = prefs.getDataModel();
        
        // Add two seds, one with 3 segments, one with 1 segment
        ExtSed sed1 = new ExtSed("test1");
        sed1.addSegment(TestUtils.createSampleSegment());
        sed1.addSegment(TestUtils.createSampleSegment());
        sed1.addSegment(TestUtils.createSampleSegment());
        ExtSed sed2 = new ExtSed("test2");
        sed2.addSegment(TestUtils.createSampleSegment());
        
        prefs.getDataStore().update(sed1);
        prefs.getDataStore().update(sed2);
        prefs.getDataModel().setSelectedSeds(Arrays.asList(sed1, sed2));
        
        // Should return 1st starTable, 1st row
        PointSelectionEvent evt = createEvent(new long[] {0,0,0,0});
        listener.setExpectations(0, 0);
        listener.pointSelected(evt);
        
        // Should return 2nd starTable, 2nd row
        evt = createEvent(new long[] {4,4,0,0});
        listener.setExpectations(1, 1);
        listener.pointSelected(evt);
        
        // Should return 4th starTable, 1st row
        evt = createEvent(new long[] {-1,-1,0,0});
        listener.setExpectations(3, 0);
        listener.pointSelected(evt);
        
        // Should return 3th starTable, 3rd row
        evt = createEvent(new long[] {8,8,0,0});
        listener.setExpectations(2, 2);
        listener.pointSelected(evt);
    }
    
    private PointSelectionEvent createEvent(long[] arr) {
        return new PointSelectionEvent(new Object(), null, arr);
    }
    
    private static class StubListener extends StilPlotterPointSelectionListener {
        
        public boolean called = false;
        public int checkIndex;
        public int checkIrow;

        @Override
        public void handleSelection(int starTableIndex, int irow,
                PointSelectionEvent evt)
        {
            called = true;
            assertEquals(this.checkIndex, starTableIndex);
            assertEquals(this.checkIrow, irow);
        }
        
        public void setExpectations(int checkIndex, int checkIrow) {
            called = false;
            this.checkIndex =checkIndex;
            this.checkIrow = checkIrow;
        }
    }
}
