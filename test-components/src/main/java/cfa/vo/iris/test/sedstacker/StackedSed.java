/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cfa.vo.iris.test.sedstacker;

/**
 *
 * @author jbudynk
 */
public class StackedSed extends Stack {
    
    private double[] counts;
    
    public StackedSed(String id) {
	super(id);
    }
    
    public double[] getCounts() {
	return counts;
    }
    
    public void setCounts(double[] counts) {
	this.counts = counts;
    }
}
