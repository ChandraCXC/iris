/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cfa.vo.iris.test.sedstacker;

import cfa.vo.iris.events.SedCommand;
import cfa.vo.iris.events.SedEvent;
import cfa.vo.iris.logging.LogEntry;
import cfa.vo.iris.logging.LogEvent;
import cfa.vo.iris.sed.ExtSed;
import cfa.vo.iris.sed.ISedManager;
import cfa.vo.iris.sed.SedlibSedManager;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

/**
 *
 * @author jbudynk
 */
public class SedStackerManager {
    
    private SedlibSedManager sedManager;
    private Stack selected;
    private TreeMap<String, Stack> stackMap = new TreeMap();
    
    public SedStackerManager(SedlibSedManager sedManager) {
	this.sedManager = sedManager;
    }
    
    public SedlibSedManager getSedManger() {
	return this.sedManager;
    }
    
    public void add(Stack stack) {
        String id = stack.getId();
        int c = 0;
        while (exists(id + (c == 0 ? "" : "." + c))) {
            c++;
        }
        stack.setId(id + (c == 0 ? "" : "." + c));
        stackMap.put(stack.getId(), stack);
	
	// add a Sed to the Builder with the Stack attached.
	sedManager.add(stack);
	ExtSed sed = sedManager.getSelected();
	sed.addAttachment("stack:manager", this);
	
        SedEvent.getInstance().fire(sed, SedCommand.ADDED);
        LogEvent.getInstance().fire(this, new LogEntry("SED added: " + sed.getId(), this));
        select(stack  );
    }
    
    public void remove(String id) {
	
	ExtSed sed = getSed(stackMap.get(id));
        stackMap.remove(id);
	sedManager.remove(id);
	
        SedEvent.getInstance().fire(sed, SedCommand.REMOVED);
        LogEvent.getInstance().fire(this, new LogEntry("SED removed: " + id, this));
        if(this.getStacks().isEmpty()) {
            Stack newstack = this.newStack("Stack");
            this.select(newstack);
        } else {
            this.select(stackMap.lastEntry().getValue());
        }
	
    }
    
    public Stack newStack(String id) {
	int c = 0;
        while (exists(id + (c == 0 ? "" : "." + c))) {
            c++;
        }
        id = id + (c == 0 ? "" : "." + c);
        Stack stack = new Stack(id);
        stackMap.put(id, stack);
	
	// Create a new Sed and attach the Stack to it
	sedManager.add(stack);
	ExtSed sed = sedManager.getSelected();
	sed.addAttachment("stack:manager", this);
        SedEvent.getInstance().fire(sed, SedCommand.ADDED);
        LogEvent.getInstance().fire(this, new LogEntry("Stack created: " + id, this));
        select(stack);
        return stack;
    }
    
    public Stack copy(Stack stack) {
	
	Stack newStack = (Stack) stack.clone();
	newStack.setId(stack.getId()+"_Copy");
	
	return newStack;
    }
    
    public boolean exists(String id) {
        return stackMap.containsKey(id);
    }
    
    public boolean exists(Stack stack) {
	return stackMap.containsValue(stack);
    }
    
    public void addAttachment(String id, String attachmentId, Object object) {
	stackMap.get(id).addAttachment(attachmentId, object);
    }
    
    public void addAttachment(Stack stack, String attachmentId, Object object) {
	stack.addAttachment(attachmentId, object);
    }
    
    public void removeAttachment(String id, String attachmentId) {
	stackMap.get(id).removeAttachment(attachmentId);
    }
    
    public void removeAttachment(Stack stack, String attachmentId) {
	stack.removeAttachment(attachmentId);
    }
    
    public Object getAttachment(String id, String attachmentId) {
	return stackMap.get(id).getAttachment(attachmentId);
    }
    
    public Object getAttachment(Stack stack, String attachmentId) {
	return stack.getAttachment(attachmentId);
    }
    
    public List<Stack> getStacks() {
	
	List<Stack> stacks = new ArrayList();
	for (Stack stack : stackMap.values()) {
	    stacks.add(stack);
	}
	return stacks;
    }
    
    public Stack getSelected() {
	return selected;
    }
    
    public void select(Stack stack) {
	this.selected = stack;
	ExtSed sed = getSed(stack);
	sedManager.select(sed);
        SedEvent.getInstance().fire(sed, SedCommand.SELECTED);
        LogEvent.getInstance().fire(this, new LogEntry("Stack selected: " + sed.getId(), this));
    }
    
    public void renameStack(Stack stack, String newId) {
        String oldId = stack.getId();
        stackMap.remove(oldId);
        stack.setId(newId);
        stackMap.put(newId, stack);
	ExtSed sed = getSed(stack);
        SedEvent.getInstance().fire(sed, SedCommand.CHANGED);
        LogEvent.getInstance().fire(this, new LogEntry("SED changed: " + oldId + " -> " + newId, sed));
	sedManager.rename(sed, newId);
    }
    
    public ExtSed getSed(Stack stack) {
	
	for(ExtSed sed : sedManager.getSeds()) {
	    if(sed.getId().equals(stack.getId())) {
		return sed;
	    }
	}
	return null;
    }
    
}
