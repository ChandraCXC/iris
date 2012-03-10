/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package cfa.vo.iris.interop;

/**
 *
 * @author olaurino
 */
public interface VaoMessage extends VoTableMessage {
    public VaoPayload getVaoPayload();
    public void setVaoPayload(VaoPayload pl);
}
