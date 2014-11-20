/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cfa.vo.iris.test.sedstacker;

import cfa.vo.iris.sed.ExtSed;
import java.util.ArrayList;

/**
 *
 * @author jbudynk
 */
public class OmarStack extends ArrayList<ExtSed> implements IStack {
    
    private String id;
    
    public OmarStack(String id) {
	this.id = id;
    }

    @Override
    public String getId() {
	return id;
    }

    @Override
    public void setId(String id) {
	this.id = id;
    }

    @Override
    public ExtSed getStackedSed() {
	return stack();
    }

    private ExtSed stack() {
	throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
}
