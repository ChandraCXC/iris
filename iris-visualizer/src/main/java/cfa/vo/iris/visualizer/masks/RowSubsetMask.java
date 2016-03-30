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
    
    private int size;
    private BitSet mask;
    private IrisStarTable table;
    
    public RowSubsetMask(int[] rows, IrisStarTable table) {
        this(rows, 0, table);
    }
    
    /**
     * Create a filter which filters out the given rows in the star table, presuming
     * that the first row of the star table is indexed by startIndex. Rows that do not 
     * apply to this star table are ignored.
     * 
     * @param rows
     * @param startIndex
     * @param table
     */
    public RowSubsetMask(int[] rows, int startIndex, IrisStarTable table) {
        this.table = table;
        
        // The size of this filter is the size of the underlying plot table.
        this.size = (int) table.getPlotterTable().getRowCount();
        this.mask = new BitSet();
        
        applyMasks(rows, startIndex);
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

    @Override
    public void clearMasks(int[] rows, int startIndex) {
        for (int i : rows) {
            int index = i - startIndex;
            if (index >= 0 && index < size) {
                mask.clear(index);
            }
        }
    }

    @Override
    public void applyMasks(int[] rows, int startIndex) {
        for (int i : rows) {
            int index = i - startIndex;
            if (index >= 0 && index < size) {
                mask.set(index);
            }
            
        }
    }
}
