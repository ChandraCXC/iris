/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package cfa.vo.iris.sed;

/**
 *
 * @author olaurino
 */
public interface ISpectralSegment {
    void set(IUType utype, Object value) throws SedException;
    void set(IUTypedValue value);
    IUTypedValue get(IUType utype);
}
