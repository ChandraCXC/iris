/**
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
package cfa.vo.iris.visualizer.masks;

import java.util.BitSet;

import cfa.vo.iris.visualizer.stil.tables.IrisStarTable;

/**
 * Filter based on row indices for a specific StarTable. Applying this
 * filter to other tables will not have any effect. Any row index in rows[] will
 * be filtered in the corresponding table.
 *
 */
public class RowSubsetMask implements Mask {
    
    private BitSet mask;
    private IrisStarTable table;
    
    /**
     * Create a filter which filters out the given rows in the star table. Rows that do not 
     * apply to this star table are ignored.
     * 
     * @param rows
     * @param table
     */
    public RowSubsetMask(int[] rows, IrisStarTable table) {
        this.table = table;
        this.mask = new BitSet();
        
        applyMasks(rows);
    }
    
    @Override
    public BitSet getMaskedRows(IrisStarTable table) {
        // These filters only apply to a single star table.
        if (this.table == table) {
            return mask;
        }
        return new BitSet();
    }
    
    @Override
    public long cardinality() {
        return mask.cardinality();
    }

    /**
     * Removes the specified rows from the mask.
     *
     */
    @Override
    public void clearMasks(int[] rows) {
        for (int i : rows) {
            if (i > table.getBaseTable().getRowCount()) continue;
            mask.clear(i);
        }
    }
    
    /**
     * Adds the specified rows to the mask. 
     *
     */
    @Override
    public void applyMasks(int[] rows) {
        for (int i : rows) {
            if (i > table.getBaseTable().getRowCount()) continue;
            mask.set(i);
        }
    }
}
