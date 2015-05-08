/**
 * Copyright (C) 2015 Smithsonian Astrophysical Observatory
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

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cfa.vo.sed.science.stacker;

import cfa.vo.iris.sed.ExtSed;
import static cfa.vo.sed.science.stacker.SedStackerAttachments.NORM_CONSTANT;
import static cfa.vo.sed.science.stacker.SedStackerAttachments.ORIG_REDSHIFT;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
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

    public StackTableModel(final SedStack stack) {
	data = new String[stack.getSeds().size()][4];
	int i =0;
	for (ExtSed sed : stack.getSeds()) {
	    data[i][0] = sed.getId();
	    data[i][1] = (String) sed.getAttachment(ORIG_REDSHIFT);
	    data[i][2] = sed.getAttachment(NORM_CONSTANT).toString();
	    data[i][3] = getNumOfPoints(sed).toString();
	    i++;
	}
	
	this.addTableModelListener(new TableModelListener() {

	    @Override
	    public void tableChanged(TableModelEvent e) {
		if (e.getType() == TableModelEvent.UPDATE) {
		    int row = e.getFirstRow();
		    int column = e.getColumn();
		    Object value = ((StackTableModel) e.getSource()).getValueAt(row, column);
		    stack.getSed(row).addAttachment(SedStackerAttachments.REDSHIFT, value);
		    stack.getSed(row).addAttachment(SedStackerAttachments.ORIG_REDSHIFT, value);
		    stack.getOrigSeds().get(row).addAttachment(SedStackerAttachments.REDSHIFT, value);
		    stack.getOrigSeds().get(row).addAttachment(SedStackerAttachments.ORIG_REDSHIFT, value);
		}
	    }
	});
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
    
    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
	    return (columnIndex == 1);
    }
    
    @Override
    public void setValueAt(Object value, int row, int col) {
	data[row][col] = (String) value;
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
