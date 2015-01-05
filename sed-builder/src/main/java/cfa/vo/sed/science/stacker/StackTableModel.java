/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cfa.vo.sed.science.stacker;

import cfa.vo.iris.sed.ExtSed;
import static cfa.vo.sed.science.stacker.SedStackerAttachments.NORM_CONSTANT;
import static cfa.vo.sed.science.stacker.SedStackerAttachments.ORIG_REDSHIFT;
import java.util.ArrayList;
import javax.swing.table.AbstractTableModel;

/**
 *
 * @author jbudynk
 */
public class StackTableModel extends AbstractTableModel {
    
    String[][] data = new String[][]{};
    String[] columnNames = new String[] {"Sed ID", "Redshift", "Normalization Constant", "#Points"};
    
    public StackTableModel() {
	
    }

    public StackTableModel(SedStack stack) {
	data = new String[stack.getSeds().size()][4];
	int i =0;
	for (ExtSed sed : stack.getSeds()) {
	    data[i][0] = sed.getId();
	    data[i][1] = (String) sed.getAttachment(ORIG_REDSHIFT);
	    data[i][2] = sed.getAttachment(NORM_CONSTANT).toString();
	    data[i][3] = getNumOfPoints(sed).toString();
	    i++;
	}
    }
    
    @Override
    public int getRowCount() {
	return data.length;
    }

    @Override
    public int getColumnCount() {
	return columnNames.length;
    }

    @Override
    public Object getValueAt(int row, int column) {
	return data[row][column];
    }
    
    @Override
    public String getColumnName(int col) {
	return columnNames[col];
    }
    
    public void setValueAt(String value, int row, int col) {
	data[row][col] = value;
	fireTableCellUpdated(row, col);
    }

    private Integer getNumOfPoints(ExtSed sed) {
	int numOfPoints = 0;
	for (int i = 0; i<sed.getNumberOfSegments(); i++) {
	    numOfPoints += sed.getSegment(i).getLength();
	}
	return numOfPoints;
    }
    
    public void addRow(String[] rowData) {
	System.out.println(getRowCount());
        data[getRowCount()] = rowData;
	System.out.println(getRowCount());
        fireTableRowsInserted(getRowCount() - 1, getRowCount() - 1);
    }
    
}
