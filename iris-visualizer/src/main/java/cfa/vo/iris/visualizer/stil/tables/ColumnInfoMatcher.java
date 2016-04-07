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

package cfa.vo.iris.visualizer.stil.tables;

import uk.ac.starlink.table.ColumnData;
import uk.ac.starlink.table.ColumnInfo;
import uk.ac.starlink.table.ConstantColumn;

/**
 * Abstract class for compatibility checks between columns of a StarTable when 
 * stacking multiple tables. Implementations should override one or bothe of 
 * these methods.
 *
 */
public abstract class ColumnInfoMatcher {
    
    public boolean isCompatible(ColumnInfo c1, ColumnInfo c2) {
        return false;
    }
    
    public ColumnData getDefaultValueColumn(ColumnInfo c) {
        return new ConstantColumn(c, null);
    }
}
