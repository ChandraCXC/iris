/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cfa.vo.iris.test.sedstacker;

import cfa.vo.iris.test.sedstacker.samp.SedStackerStacker;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.undo.AbstractUndoableEdit;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;
import javax.swing.undo.UndoableEdit;

/**
TODO: Should this be an undoable function? (Oct 21).
  YES - because we would want the user to see the normalization, 
        redshift, and stacking options.
  NO  - because the user can save that info themselves.
*/


/**
 *
 * @author jbudynk
 */
public class StackEdit extends AbstractUndoableEdit {

    private Stack stack_;
    private StackConfig stackconfig_;
    private ExtendedUndoManager undoManager_;
    private SedStackerStacker stacker;

    public StackEdit(Stack stack, StackConfig stackconfig, ExtendedUndoManager undoManager) {
	stack_ = stack;
	stackconfig_ = stackconfig;
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
	    stacker.stack(stack_, stackconfig_);
	} catch (Exception ex) {
	    Logger.getLogger(StackEdit.class.getName()).log(Level.SEVERE, null, ex);
	}
    }

    // TODO (Oct 20) : Do I want to keep this as an undo/redoable function??
    public boolean canUndo() {
	return true;
    }

    public boolean canRedo() {
	return true;
    }

    public String getPresentationName() {
	return "Stacking";
    }
}
