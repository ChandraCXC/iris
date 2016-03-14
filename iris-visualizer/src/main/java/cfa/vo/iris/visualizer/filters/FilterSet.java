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
package cfa.vo.iris.visualizer.filters;

import java.util.BitSet;
import java.util.LinkedHashSet;

import cfa.vo.iris.visualizer.stil.tables.IrisStarTable;

public class FilterSet extends LinkedHashSet<Filter> {
    
    private static final long serialVersionUID = 1L;
    
    private IrisStarTable table;
    private BitSet masked;
    
    public FilterSet(IrisStarTable table) {
        super();
        this.table = table;
        this.masked = new BitSet((int) table.getPlotterTable().getRowCount());
    }
    
    @Override
    public boolean add(Filter filter) {
        boolean changed = super.add(filter);
        if (changed) {
            masked.or(filter.getFilteredRows(table));
        }
        return changed;
    }
    
    @Override
    public void clear() {
        super.clear();
        this.masked.clear();
    }
    
    public boolean remove(Filter filter) {
        boolean changed = super.remove(filter);
        if (changed) {
            this.masked = updateMasked();
        }
        return changed;
    }
    
    public BitSet getMasked() {
        return masked;
    }

    public long cardinality() {
        return masked.cardinality();
    }
    
    public void invert() {
        for (Filter f : this) {
            f.invert();
        }
        
        this.masked = updateMasked();
    }
    
    private BitSet updateMasked() {
        BitSet mask = new BitSet();
        for (Filter f : this) {
            mask.or(f.getFilteredRows(table));
        }
        return mask;
    }
}
