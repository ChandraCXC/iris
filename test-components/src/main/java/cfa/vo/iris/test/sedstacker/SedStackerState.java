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
public interface SedStackerState {
    
    public void shiftSeds();
    public void normalizeSeds();
    public void stackSeds();
    
}
