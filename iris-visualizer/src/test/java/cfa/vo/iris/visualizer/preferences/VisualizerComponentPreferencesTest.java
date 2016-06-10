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
import cfa.vo.sedlib.Segment;
import java.util.ArrayList;
import java.util.List;

public class VisualizerComponentPreferencesTest {

    @Test
    public void testPreferences() throws Exception {
        ExtSed sed = new ExtSed("test");
        IWorkspace ws = new StubWorkspace();

        VisualizerComponentPreferences prefs = new VisualizerComponentPreferences(ws);
        VisualizerDataStore store = prefs.getDataStore();
        
        assertEquals(0, store.getSedModels().size());
        assertNull(store.getSedModel(sed));
        
        
        // Add SED
        store.update(sed);
        assertEquals(1, store.getSedModels().size());
        assertEquals(0, store.getSedModel(sed).getAllSegmentModels().size());
        
        // Add segment to SED
        Segment seg1 = createSampleSegment();
        sed.addSegment(seg1);
        store.update(sed, seg1);
        assertEquals(1, store.getSedModels().size());
        assertEquals(1, store.getSedModel(sed).getAllSegmentModels().size());
        
        // Add another segment
        Segment seg2 = createSampleSegment(new double[] {1}, new double[] {2});
        sed.addSegment(seg2);
        store.update(sed, seg2);
        assertEquals(1, store.getSedModels().size());
        assertEquals(2, store.getSedModel(sed).getAllSegmentModels().size());
        
        // Remove the first segment
        sed.remove(seg1);
        store.remove(sed, seg1);
        assertEquals(1, store.getSedModels().size());
        assertEquals(1, store.getSedModel(sed).getAllSegmentModels().size());
        
        // Add a list of segments at once (test MultipleSegmentEvent listener)
        Segment seg3 = createSampleSegment();
        List<Segment> segments = new ArrayList<>();
        segments.add(seg3); segments.add(seg1); // add two segments
        sed.addSegment(segments);
        store.update(sed, segments);
        assertEquals(1, store.getSedModels().size());
        assertEquals(3, store.getSedModel(sed).getAllSegmentModels().size());
        
        sed.remove(segments);
        store.remove(sed, segments);
        assertEquals(1, store.getSedModel(sed).getAllSegmentModels().size());        
        
        
        // Remove the SED
        store.remove(sed);
        assertEquals(0, store.getSedModels().size());
    }
}
