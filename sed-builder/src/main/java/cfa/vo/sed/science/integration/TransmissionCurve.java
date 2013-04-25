/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cfa.vo.sed.science.integration;

/**
 *
 * @author olaurino
 */
public interface TransmissionCurve {
    public String getFileName();
    public void setFileName(String fileName);
    public String getId();
    public void setId(String id);
    public double getEffWave();
    public void setEffWave(double wave);
}
