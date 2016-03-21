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
import java.util.UUID;

import org.apache.commons.lang.StringUtils;

import cfa.vo.iris.visualizer.stil.tables.IrisStarTable;

public abstract class Filter {
    
    public final String id;
    
    public Filter() {
        this.id = UUID.randomUUID().toString();
    }
    
    @Override
    public boolean equals(Object other) {
        if (other == null || !(other instanceof Filter)) {
            return false;
        }
        Filter o = (Filter) other;
        return StringUtils.equals(id, o.id);
    }
    
    @Override
    public int hashCode() {
        return id.hashCode();
    }
    
    public abstract BitSet getFilteredRows(IrisStarTable table);
    
    public abstract long cardinality();
    
    public abstract void applyMasks(int[] rows, int startIndex);
    
    public abstract void clearMasks(int[] rows, int startIndex);
}
