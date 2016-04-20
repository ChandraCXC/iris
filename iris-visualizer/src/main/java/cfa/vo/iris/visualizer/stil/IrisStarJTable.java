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
import java.util.LinkedList;
import java.util.List;

import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumnModel;
import java.awt.Rectangle;

import org.apache.commons.lang.StringUtils;
import cfa.vo.iris.utils.UTYPE;
import cfa.vo.iris.visualizer.stil.tables.ColumnInfoMatcher;
import cfa.vo.iris.visualizer.stil.tables.IrisStarTable;
import cfa.vo.iris.visualizer.stil.tables.SegmentColumnInfoMatcher;
import cfa.vo.iris.visualizer.stil.tables.StackedStarTable;
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
    
    // Use the plotter data (segment) star tables or the metadata star tables
    private boolean usePlotterDataTables;

    private ColumnInfoMatcher columnInfoMatcher;
    private List<IrisStarTable> selectedStarTables;

    public IrisStarJTable() {
        super(false);
        
        // By default we use use the plotter data, as it is generally available first
        columnInfoMatcher = new SegmentColumnInfoMatcher();
        usePlotterDataTables = true;
    }
    
    public List<IrisStarTable> getSelectedStarTables() {
        return selectedStarTables;
    }

    /**
     * Updates the selected list of star tables and creates a new stacked star table to represent
     * the concatenated tables in a single JTable.
     * 
     * @param selectedStarTables
     */
    public void setSelectedStarTables(List<IrisStarTable> selectedStarTables) {
        if (selectedStarTables == null) {
            return;
        }
        
        // Include the index column for non-null/non-empty star tables.
        boolean showIndex = (selectedStarTables.size() > 0);
        
        this.selectedStarTables = selectedStarTables;
        List<StarTable> dataTables = new LinkedList<>();
        
        for (IrisStarTable table : selectedStarTables) {
            if (usePlotterDataTables) {
                dataTables.add(table.getPlotterTable());
            } else {
                dataTables.add(table.getSegmentDataTable());
            }
        }
        
        this.setStarTable(new StackedStarTable(dataTables, columnInfoMatcher), showIndex);
        IrisStarJTable.configureColumnWidths(this, 200, 20);
    }
    
    public boolean isUtypeAsNames() {
        return utypeAsNames;
    }

    public void setUtypeAsNames(boolean utypeAsNames) {
        this.utypeAsNames = utypeAsNames;
    }
    
    public boolean isUsePlotterDataTables() {
        return usePlotterDataTables;
    }

    public void setUsePlotterDataTables(boolean usePlotterDataTables) {
        this.usePlotterDataTables = usePlotterDataTables;
    }

    public ColumnInfoMatcher getColumnInfoMatcher() {
        return columnInfoMatcher;
    }

    public void setColumnInfoMatcher(ColumnInfoMatcher columnInfoMatcher) {
        this.columnInfoMatcher = columnInfoMatcher;
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
    
    public void selectRowIndex(int starTableIndex, int irow) {
        // TODO: Handle sorting when we add it.
        
        // irow corresponds to the row in the (possibly masked) IrisStarTable, we need to 
        // map it back to the correct row in the dataTable.
        IrisStarTable selectedTable = this.selectedStarTables.get(starTableIndex);
        int trueRow = selectedTable.getBaseTableRow(irow);
        
        // Actual row is the trueRow plus the length of all the other tables (based on the
        // base table! Not the masked table!)
        for (int i=0; i<starTableIndex; i++) {
            trueRow += this.selectedStarTables.get(i).getBaseTable().getRowCount();
        }
        
        this.selectionModel.addSelectionInterval(trueRow, trueRow);
        this.scrollRectToVisible(new Rectangle(this.getCellRect(trueRow, 0, true)));
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
