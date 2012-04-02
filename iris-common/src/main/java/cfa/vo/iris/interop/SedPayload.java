/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package cfa.vo.iris.interop;

/**
 *
 * @author olaurino
 */
public class SedPayload {
    private String sedId;

    public SedPayload(String sedId) {
        this.sedId = sedId;
    }

    public String getSedId() {
        return sedId;
    }

    public void setSedId(String sedId) {
        this.sedId = sedId;
    }
}
