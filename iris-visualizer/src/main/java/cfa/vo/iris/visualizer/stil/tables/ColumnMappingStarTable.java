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

import cfa.vo.iris.units.UnitsManager;
import cfa.vo.utils.Default;
import uk.ac.starlink.table.ColumnInfo;
import uk.ac.starlink.table.ColumnPermutedStarTable;
import uk.ac.starlink.table.ConstantStarTable;
import uk.ac.starlink.table.JoinStarTable;
import uk.ac.starlink.table.StarTable;

/**
 * Variation of a column permuted StarTable which maps the columns of a given
 * StarTable to match up with a provided metadata table. It is required that
 * the metadata table has more rows than the baseTable. Any excess rows will be
 * filled in using constant empty columns.
 *
 */
public class ColumnMappingStarTable extends ColumnPermutedStarTable {
    
    private static final UnitsManager um = Default.getInstance().getUnitsManager();
    
    private final StarTable metadataTable;
    private final StarTable originalStarTable;
    private final ColumnInfoMatcher matcher;

    public ColumnMappingStarTable(final StarTable baseTable,
                                  StarTable metadataTable,
                                  ColumnInfoMatcher matcher) 
    {
        super(baseTable, new int[metadataTable.getColumnCount()]);
        
        // Verify input
        if (baseTable.getColumnCount() > metadataTable.getColumnCount()) {
            throw new IllegalArgumentException("baseTable (" + 
                       baseTable.getColumnCount() + " columns) must have fewer columns than metadataTable (" +
                       metadataTable.getColumnCount() + ")");
        }
        
        // Init map to all -1
        int[] map = getColumnMap();
        for (int i=0; i<map.length; i++) {
            map[i] = -1;
        }
        
        this.metadataTable = metadataTable;
        this.matcher = matcher;
        this.originalStarTable = baseTable;
        
        computeColumnMap();
        addNullColumns();
    }
    
    private void computeColumnMap() {
        
        for (int i=0; i<metadataTable.getColumnCount(); i++) {
            ColumnInfo meta = metadataTable.getColumnInfo(i);

            for (int j=0; j<baseTable.getColumnCount(); j++) {
                ColumnInfo base = baseTable.getColumnInfo(j);
                
                // If they're compatible, match this column of the base table to the
                // metadataTable
                if (matcher.isCompatible(meta, base)) {
                    // TODO: Units Conversion
                    this.getColumnMap()[i] = j;
                    break;
                }
            }
        }
    }
    
    /**
     * Need to fill in base table with empty columns to match the metadataTable.
     */
    private void addNullColumns() {
        ArrayList<ColumnInfo> infos = new ArrayList<>(
                metadataTable.getColumnCount() - baseTable.getColumnCount());
        
        // Used to map
        int counter = baseTable.getColumnCount();
        int[] map = getColumnMap();
        
        for (int i=0; i<map.length; i++) {
            // If there is no mapping from the base table, then we need to add a new column.
            if (map[i] == -1) {
                ColumnInfo inf = metadataTable.getColumnInfo(i);
                inf.setNullable(true);
                infos.add(inf);
                map[i] = counter;
                counter++;
            }
        }
        
        StarTable table = new ConstantStarTable(infos.toArray(new ColumnInfo[0]),
                new Object[infos.size()], baseTable.getRowCount());
        this.baseTable = new JoinStarTable(new StarTable[] {baseTable, table});
    }
    
    public StarTable getOriginalTable() {
        return originalStarTable;
    }
}
