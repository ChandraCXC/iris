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

import java.io.IOException;
import java.util.List;

import uk.ac.starlink.table.ConcatStarTable;
import uk.ac.starlink.table.StarTable;
import uk.ac.starlink.table.WrapperStarTable;

/**
 * Extension of a ConcatStarTable that supports stacking of any StarTable, regardless
 * of ColumnInfo compatibility.
 *
 */
public class StackedStarTable extends WrapperStarTable {
    
    private ColumnMetadataStarTable metadataTable;
    private ColumnMappingStarTable[] dataTables;

    public StackedStarTable(List<? extends StarTable> tables, ColumnInfoMatcher matcher)
    {
        super(null);
        
        this.metadataTable = new ColumnMetadataStarTable(tables, matcher);
        this.dataTables = new ColumnMappingStarTable[tables.size()];
        
        for (int i=0; i<tables.size(); i++) {
            dataTables[i] = new ColumnMappingStarTable(tables.get(i), metadataTable, matcher);
        }
        
        try {
            this.baseTable = new ConcatStarTable(metadataTable, dataTables);
        } catch (IOException e) {
            throw new IllegalArgumentException(e);
        }
    }
}
