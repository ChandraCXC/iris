/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cfa.vo.iris.test.sedstacker;

import java.util.ArrayList;
import java.util.List;
import javax.swing.undo.UndoManager;
import javax.swing.undo.UndoableEdit;

/**
 *
 * @author jbudynk
 */
public class ExtendedUndoManager extends UndoManager {
    
    private int indexOfNextAdd;
    
    @Override
    public synchronized UndoableEdit editToBeUndone() {
	UndoableEdit result;
 
	for (int i = indexOfNextAdd - 1; i >= 0; i--) {
	    result = (UndoableEdit) edits.get(i);
	    return result;
	}
	return null;
    }
    
    public synchronized List<UndoableEdit> getEdits() {
	List<UndoableEdit> array = new ArrayList<UndoableEdit>(edits.size());
	return array;
    }
}
