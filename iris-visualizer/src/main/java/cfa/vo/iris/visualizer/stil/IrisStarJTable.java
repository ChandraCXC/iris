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

package cfa.vo.iris.visualizer.stil;

import org.apache.commons.lang.StringUtils;

import cfa.vo.iris.utils.UTYPE;
import uk.ac.starlink.table.StarTable;
import uk.ac.starlink.table.gui.StarJTable;
import uk.ac.starlink.table.gui.StarTableColumn;

/**
 * Simple bean wrapper for the StarJTable class.
 *
 */
public class IrisStarJTable extends StarJTable {
    
    // Use ColumnNames or Utypes in the column header fields
    private boolean utypeAsNames = false;
    
    public IrisStarJTable() {
        super(false);
    }

    public void setStarTable(StarTable table) {
        // Include the index column for non-null/non-empty star tables.
        boolean showIndex = (table != null && table.getRowCount() > 0);
        setStarTable(table, showIndex);
    }
    
    public boolean isUtypeAsNames() {
        return utypeAsNames;
    }

    public void setUtypeAsNames(boolean utypeAsNames) {
        this.utypeAsNames = utypeAsNames;
    }
    
    @Override
    public void setStarTable(StarTable table, boolean showIndex) {
        super.setStarTable(table, showIndex);
        
        if (!utypeAsNames) {
            return;
        }
        
        // If we're using utypes as column names, override existing settings here.
        for (int i=0;i < columnModel.getColumnCount(); i++) {
            StarTableColumn c = (StarTableColumn) columnModel.getColumn(i);
            
            if (c.getColumnInfo() != null &&
                StringUtils.isNotBlank(c.getColumnInfo().getUtype()))
            {
                String utype = UTYPE.trimPrefix(c.getColumnInfo().getUtype());
                c.setHeaderValue(utype);
            }
        }
    }
}
