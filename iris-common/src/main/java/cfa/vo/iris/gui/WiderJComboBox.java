/**
 * Copyright (C) 2012 Smithsonian Astrophysical Observatory
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
package cfa.vo.iris.gui;

import java.awt.Dimension;
import java.util.Vector;
import javax.swing.ComboBoxModel;
import javax.swing.JComboBox;

/**
 *
 * @author jbudynk
 */
public class WiderJComboBox extends JComboBox{ 
    
//    private String type;
//    private boolean layingOut = false;
//    private int widestLengh = 0;
//    private boolean wide = false;
//
//    public WiderJComboBox() {
//	super();
//    }
//
//    public boolean isWide() {
//	return wide;
//    }
//    //Setting the JComboBox wide
//    public void setWide(boolean wide) {
//	this.wide = wide;
//	widestLengh = getWidestItemWidth();
//    }
//    
//    @Override
//    public Dimension getSize(){
//	Dimension dim = super.getSize();
//	if(!layingOut && isWide())
//	    dim.width = Math.max( widestLengh, dim.width );
//	return dim;
//    }
//
//    public int getWidestItemWidth() {
//
//	int numOfItems = this.getItemCount();
//	Font font = this.getFont();
//	FontMetrics metrics = this.getFontMetrics( font );
//	int widest = 0;
//	for ( int i = 0; i < numOfItems; i++ ) {
//	    Object item = this.getItemAt( i );
//	    int lineWidth = metrics.stringWidth( item.toString() );
//	    widest = Math.max( widest, lineWidth );
//	}
//
//	return widest + 5;
//    }
//    
//    @Override
//    public void doLayout() {
//	try {
//	    layingOut = true;
//	    super.doLayout();
//	} finally {
//	    layingOut = false;
//	}
//    }
//    
//    public String getType() {
//	return type;
//    }
//
//    public void setType( String t ) {
//	type = t;
//    }
 
    public WiderJComboBox() { 
    } 
 
    public WiderJComboBox(final Object items[]){ 
        super(items); 
    } 
 
    public WiderJComboBox(Vector items) { 
        super(items); 
    } 
 
    public WiderJComboBox(ComboBoxModel aModel) { 
        super(aModel); 
    } 
 
    private boolean layingOut = false; 
 
    @Override
    public void doLayout(){ 
        try{ 
            layingOut = true; 
            super.doLayout(); 
        }finally{ 
            layingOut = false; 
        } 
    } 
 
    @Override
    public Dimension getSize(){ 
        Dimension dim = super.getSize(); 
        if(!layingOut) 
            dim.width = Math.max(dim.width, getPreferredSize().width); 
        return dim; 
    } 
}
