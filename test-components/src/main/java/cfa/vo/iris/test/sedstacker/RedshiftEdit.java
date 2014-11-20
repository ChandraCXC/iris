/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cfa.vo.iris.test.sedstacker;

import cfa.vo.iris.test.sedstacker.samp.SedStackerRedshifter;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.undo.AbstractUndoableEdit;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;
import javax.swing.undo.UndoableEdit;

/**
 *
 * @author jbudynk
 */
public class RedshiftEdit extends AbstractUndoableEdit{

    private Stack stack_;
    private ZConfig zconfig_;
    private ExtendedUndoManager undoManager_;
    private SedStackerRedshifter redshifter;

    public RedshiftEdit(Stack stack, ZConfig zconfig, ExtendedUndoManager undoManager) {
	stack_ = stack;
	zconfig_ = zconfig;
	undoManager_ = undoManager;
    }

    public void undo() throws CannotUndoException {
	  
	// Repeat the steps in the UndoManager, except this edit.
      
	List<UndoableEdit> allEdits = undoManager_.getEdits();
	UndoableEdit lastEdit = undoManager_.editToBeUndone();
	List<UndoableEdit> edits = allEdits.subList(0, allEdits.size() - (allEdits.size() - allEdits.lastIndexOf(lastEdit)));
	for(UndoableEdit edit : edits) {
	    edit.redo();
	}
    }

    public void redo() throws CannotRedoException {
	try {
	    // Repeat the steps in the UndoManager, including this edit.
	    redshifter.shift(stack_, stack_.getRedshifts(), zconfig_);
	} catch (Exception ex) {
	    Logger.getLogger(NormalizeEdit.class.getName()).log(Level.SEVERE, null, ex);
	}
    }

    public boolean canUndo() {
	return true;
    }

    public boolean canRedo() {
	return true;
    }

    public String getPresentationName() {
	return "Redshift";
    }

}
