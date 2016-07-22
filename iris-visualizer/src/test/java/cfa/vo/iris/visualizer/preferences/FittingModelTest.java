/*
 * Copyright 2016 Chandra X-Ray Observatory.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package cfa.vo.iris.visualizer.preferences;

import java.util.Arrays;

import org.junit.Test;

import cfa.vo.iris.fitting.FittingRange;
import cfa.vo.iris.sed.quantities.XUnit;
import uk.ac.starlink.table.StarTable;

import static org.junit.Assert.*;

public class FittingModelTest {
    
    @Test
    public void testFittingModel() throws Exception {
        FittingRange range1 = new FittingRange(10, 1000, XUnit.NM);
        FittingRange range2 = new FittingRange(3, 5, XUnit.CM);
        
        FittingRangeModel model = new FittingRangeModel(Arrays.asList(range1, range2), "Angstrom", 1);
        
        StarTable table = model.getInSource();
        assertEquals(2, table.getRowCount());
        assertArrayEquals(new Object[] {5050.0, 100.0, 10000.0, 1.0}, table.getRow(0));
        assertArrayEquals(new Object[] {4E8, 3E8, 5E8, 1.0}, table.getRow(1));
        
        assertEquals(2, model.getNumberOfLayers());
        assertFalse(model.getShowLines());
        assertTrue(model.isShowMarks());
        assertTrue(model.isShowErrorBars());
    }
}
