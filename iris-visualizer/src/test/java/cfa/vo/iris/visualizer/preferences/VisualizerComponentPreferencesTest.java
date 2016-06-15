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

import static cfa.vo.iris.test.unit.TestUtils.createSampleSegment;
import static org.junit.Assert.*;

import org.junit.Test;

import cfa.vo.iris.IWorkspace;
import cfa.vo.iris.sed.ExtSed;
import cfa.vo.iris.test.unit.StubWorkspace;
import cfa.vo.iris.test.unit.TestUtils;
import cfa.vo.sedlib.Segment;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class VisualizerComponentPreferencesTest {

    @Test
    public void testPreferences() throws Exception {
        ExtSed sed = new ExtSed("test");
        sed.setManaged(false);
        
        IWorkspace ws = new StubWorkspace();

        VisualizerComponentPreferences prefs = new VisualizerComponentPreferences(ws);
        VisualizerDataStore store = prefs.getDataStore();
        
        assertEquals(0, store.getSedModels().size());
        assertNull(store.getSedModel(sed));
        
        // Add SED
        store.update(sed);
        assertEquals(1, store.getSedModels().size());
        assertEquals(0, store.getSedModel(sed).getLayerModels().size());
        
        // Add segment to SED
        Segment seg1 = createSampleSegment();
        sed.addSegment(seg1);
        store.update(sed, seg1);
        assertEquals(1, store.getSedModels().size());
        assertEquals(1, store.getSedModel(sed).getLayerModels().size());
        
        // Add another segment
        Segment seg2 = createSampleSegment(new double[] {1}, new double[] {2});
        sed.addSegment(seg2);
        store.update(sed, seg2);
        assertEquals(1, store.getSedModels().size());
        assertEquals(2, store.getSedModel(sed).getLayerModels().size());
        
        // Remove the first segment
        sed.remove(seg1);
        store.remove(sed, seg1);
        assertEquals(1, store.getSedModels().size());
        assertEquals(1, store.getSedModel(sed).getLayerModels().size());
        
        // Add a list of segments at once (test MultipleSegmentEvent listener)
        Segment seg3 = createSampleSegment();
        List<Segment> segments = new ArrayList<>();
        segments.add(seg3); segments.add(seg1); // add two segments
        sed.addSegment(segments);
        store.update(sed, segments);
        assertEquals(1, store.getSedModels().size());
        assertEquals(3, store.getSedModel(sed).getLayerModels().size());
        
        sed.remove(segments);
        store.remove(sed, segments);
        assertEquals(1, store.getSedModel(sed).getLayerModels().size());
        
        // Remove the SED
        store.remove(sed);
        assertEquals(0, store.getSedModels().size());
    }
    
    @SuppressWarnings("unchecked")
    @Test
    public void testMultipleSeds() throws Exception {
        ExtSed sed1 = new ExtSed("test1");
        sed1.setManaged(false);
        sed1.addSegment(TestUtils.createSampleSegment(
                new double[] {1}, new double[] {1}, "Angstrom", "erg/s/cm2/Angstrom"));
        sed1.addSegment(TestUtils.createSampleSegment());
        
        ExtSed sed2 = new ExtSed("test2");
        sed2.setManaged(false);
        sed2.addSegment(TestUtils.createSampleSegment(
                new double[] {2}, new double[] {2}, "nm", "mJy"));
        sed2.addSegment(TestUtils.createSampleSegment());
        
        IWorkspace ws = new StubWorkspace();
        ws.getSedManager().add(sed1);
        ws.getSedManager().add(sed2);
        
        VisualizerComponentPreferences prefs = new VisualizerComponentPreferences(ws);
        VisualizerDataStore store = prefs.getDataStore();
        
        store.update(sed1);
        store.update(sed2);
        
        prefs.setBoundToWorkspace(false);
        
        // Add second SED to the DataModel
        prefs.getDataModel().setSelectedSeds(Arrays.asList(sed2));
        assertEquals("nm", prefs.getDataModel().getXUnits());
        assertEquals("mJy", prefs.getDataModel().getYUnits());
        assertEquals(2, prefs.getDataModel().getLayerModels().size());
        assertEquals(2, prefs.getDataModel().getSedStarTables().size());
        
        // Add both seds, should set all units to first SEDs units
        prefs.getDataModel().setSelectedSeds(Arrays.asList(sed1, sed2));
        assertEquals("Angstrom", prefs.getDataModel().getXUnits());
        assertEquals("erg/s/cm2/Angstrom", prefs.getDataModel().getYUnits());
        assertEquals("Angstrom", prefs.getDataModel().getSedModel(sed2).getXUnits());
        assertEquals(2, prefs.getDataModel().getLayerModels().size());
        assertEquals(4, prefs.getDataModel().getSedStarTables().size());
        
        // Go back to the second segment, should be back to the original units
        prefs.getDataModel().setSelectedSeds(Arrays.asList(sed2));
        assertEquals("nm", prefs.getDataModel().getXUnits());
        assertEquals("mJy", prefs.getDataModel().getYUnits());
        assertEquals(2, prefs.getDataModel().getLayerModels().size());
        assertEquals(2, prefs.getDataModel().getSedStarTables().size());
    }
}
