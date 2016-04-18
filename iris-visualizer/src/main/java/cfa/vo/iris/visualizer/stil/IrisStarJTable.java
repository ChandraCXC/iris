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

import java.awt.Point;
import java.awt.event.MouseEvent;

import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumnModel;
import java.awt.Rectangle;

import org.apache.commons.lang.StringUtils;
import cfa.vo.iris.utils.UTYPE;
import uk.ac.starlink.table.ColumnInfo;
import uk.ac.starlink.table.StarTable;
import uk.ac.starlink.table.gui.StarJTable;
import uk.ac.starlink.table.gui.StarTableColumn;

/**
 * Simple bean wrapper for the StarJTable class.
 *
 */
public class IrisStarJTable extends StarJTable {
    
    private static final long serialVersionUID = -6504087912203707631L;
    
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
    protected JTableHeader createDefaultTableHeader() {
        return new StarJTableHeader(columnModel);
    }
    
    @Override
    public void setStarTable(StarTable table, boolean showIndex) {
        super.setStarTable(table, showIndex);
        
        if (utypeAsNames) {
            setUtypeColumnNames();
        }
    }
    
    private void setUtypeColumnNames() {
        
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
    
    public void selectRowIndex(int irow) {
        // TODO: Handle sorting when we add it.
        this.selectionModel.addSelectionInterval(irow, irow);
        this.scrollRectToVisible(new Rectangle(this.getCellRect(irow, 0, true)));
    }
    
    protected class StarJTableHeader extends JTableHeader {
        
        private static final long serialVersionUID = -3882589620263074781L;
        
        public StarJTableHeader(TableColumnModel model) {
            super(model);
        }
        
        @Override
        public String getToolTipText(MouseEvent evt) {
            Point p = evt.getPoint();
            int index = columnModel.getColumnIndexAtX(p.x);
            String tt = printColumnInfo((StarTableColumn) columnModel.getColumn(index));
            return tt;
        }
        
        private String printColumnInfo(StarTableColumn column) {
            ColumnInfo info = column.getColumnInfo();
            
            StringBuilder bb = new StringBuilder();
            bb.append("<html>");
            if (!StringUtils.isEmpty(info.getName())) {
                bb.append(String.format("name: %s<br>", info.getName()));
            }
            if (!StringUtils.isEmpty(info.getUnitString())) {
                bb.append(String.format("unit: %s<br>", info.getUnitString()));
            }
            if (!StringUtils.isEmpty(info.getUtype())) {
                bb.append(String.format("utype: %s<br>", info.getUtype()));
            }
            bb.append("</html>");
            
            return bb.toString();
        }
    }
}
