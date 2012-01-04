/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package cfa.vo.iris.sed;

/**
 *
 * @author olaurino
 */
public interface IUTypedValue {
    IUType getUType();
    Object getValue();
    void setValue(Object value) throws SedException;
}
