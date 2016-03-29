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

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import uk.ac.starlink.table.ColumnData;
import uk.ac.starlink.table.ColumnInfo;
import uk.ac.starlink.table.ColumnStarTable;
import uk.ac.starlink.table.ConstantColumn;
import uk.ac.starlink.table.StarTable;

/**
 * StarTable for producing a minimal metadata table for a list of StarTables.
 *
 */
public class ColumnMetadataStarTable extends ColumnStarTable {
    
    private List<ColumnInfo> columnInfoList;
    private List<StarTable> starTables;
    private ColumnInfoMatcher matcher;

    public ColumnMetadataStarTable(List<? extends StarTable> tables, ColumnInfoMatcher matcher) {
        super();
        this.columnInfoList = new LinkedList<>();
        this.starTables = new ArrayList<>(tables.size());
        this.matcher = matcher;

        // Iterate over all ColumnInfos and add all unique columns.
        for (StarTable table : tables) {
            starTables.add(table);
            for (int i=0; i<table.getColumnCount(); i++) {
                if (!hasMatch(table.getColumnInfo(i))) {
                    columnInfoList.add(table.getColumnInfo(i));
                }
            }
        }
        
        // Setup star table with relevant column data.
        for (ColumnInfo c : columnInfoList) {
            ColumnData data = new ConstantColumn(c, null);
            addColumn(data);
        }
    }
    
    private boolean hasMatch(ColumnInfo data) {
        for (ColumnInfo info : columnInfoList) {
            if (matcher.isCompatible(data, info)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public long getRowCount() {
        return 0;
    }
}
