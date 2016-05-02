package cfa.vo.iris.visualizer.stil;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;

import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;

import org.apache.commons.lang.StringUtils;

import cfa.vo.iris.utils.UTYPE;
import cfa.vo.iris.visualizer.stil.tables.IrisStarTable;
import uk.ac.starlink.table.DescribedValue;
import uk.ac.starlink.table.StarTable;
import uk.ac.starlink.table.ValueInfo;
import uk.ac.starlink.table.gui.StarJTable;

/**
 * JTable for displaying metadata associated with a list of StarTables.
 *
 */
public class MetadataJTable extends JTable {

    private static final long serialVersionUID = -4767073627017513336L;

    private List<IrisStarTable> selectedStarTables = new ArrayList<>();
    
    public List<IrisStarTable> getSelectedStarTables() {
        return selectedStarTables;
    }

    public void setSelectedStarTables(List<IrisStarTable> newTables) {
        this.selectedStarTables = newTables;
    
        List<StarTable> dataTables = new ArrayList<>();
        for (IrisStarTable table : newTables) {
            dataTables.add(table.getSegmentMetadataTable());
        }
        
        this.setModel(new IrisMetadataTableModel(dataTables));
        StarJTable.configureColumnWidths(this, 200, 10);
    }
    
    static class IrisMetadataTableModel extends AbstractTableModel {
        
        private static final long serialVersionUID = 1L;
        
        private List<StarTable> starTableList;
        Object[][] data;
        ColumnHeader[] columnHeaders;
        String[] columnNames;
        
        public IrisMetadataTableModel() {
            starTableList = new ArrayList<>();;
            data = new Object[0][];
            columnHeaders = new ColumnHeader[0];
            columnNames = new String[0];
        };
        
        public IrisMetadataTableModel(List<StarTable> tables) {
            setStarTableList(tables);
        }
        
        public void setStarTableList(List<StarTable> tables) {
            this.starTableList = tables;
            
            // get a list of all unique parameters from the star tables
            columnHeaders = getColumnHeaders();
            
            // Setup column names
            setColumnNames();
            
            // Add values
            initData();
        }
        
        private ColumnHeader[] getColumnHeaders() {
            
            LinkedHashSet<ColumnHeader> headers = new LinkedHashSet<>();
            
            // Get a unique set of column headers
            for (StarTable table : starTableList) {
                for (Object o : table.getParameters()) {
                    ColumnHeader tmp = new ColumnHeader((DescribedValue) o);
                    if (!headers.contains(tmp)) {
                        headers.add(tmp);
                    }
                }
            }
            
            return headers.toArray(new ColumnHeader[0]);
        }
        
        private void setColumnNames() {
            // First column will always be the name of the star table
            columnNames = new String[columnHeaders.length + 1];
            columnNames[0] = "name";
            for (int i=1; i<columnNames.length; i++) {
                columnNames[i] = columnHeaders[i-1].columnName;
            }
        }
        
        private void initData() {
            this.data = new Object[starTableList.size()][];
            
            for (int i=0; i<starTableList.size(); i++) {
                StarTable table = starTableList.get(i);
                data[i] = new Object[columnNames.length];
                
                // First entry is always a name
                data[i][0] = table.getName();
                
                for (int j=0; j<columnHeaders.length; j++) {
                    DescribedValue val = table.getParameterByName(columnHeaders[j].name);
                    if (val == null) {
                        data[i][j+1] = null;
                    }
                    else {
                        data[i][j+1] = val.getInfo().formatValue(val.getValue(), 255);
                    }
                }
            }
        }
        
        @Override
        public String getColumnName(int col) {
            return columnNames[col];
        }
        
        @Override
        public int getColumnCount() {
            return columnNames.length;
        }

        @Override
        public int getRowCount() {
            return data.length;
        }

        @Override
        public Object getValueAt(int arg0, int arg1) {
            return data[arg0][arg1];
        }
        
        static class ColumnHeader {
            
            public String name;
            public String columnName;
            public ValueInfo inf;
            
            public ColumnHeader(DescribedValue val) {
                this.inf = val.getInfo();
                this.name = inf.getName();
                
                if (val.getInfo() != null && 
                    StringUtils.isNotBlank(val.getInfo().getUtype()))
                {
                    String utype = UTYPE.trimPrefix(val.getInfo().getUtype());
                    this.columnName = utype;
                }
                else {
                    this.columnName = name;
                }
            }
            
            @Override
            public boolean equals(Object other) {
                if (!(other instanceof ColumnHeader))
                    return false;
                
                ColumnHeader o = (ColumnHeader) other;
                
                // Different classes cannot be equal
                if (!inf.getContentClass().equals(o.inf.getContentClass())) {
                    return false;
                }
                
                // If utypes are equal they are equal
                if (UTYPE.compareUtypes(inf.getUtype(), o.inf.getUtype())) {
                    return true;
                }
                
                // If names are equal, they are equal
                if (StringUtils.equalsIgnoreCase(name, o.name)) {
                    return true;
                }
                
                return false;
            }
            
            @Override
            public int hashCode() {
                return name.hashCode();
            }
        }
    }
}
