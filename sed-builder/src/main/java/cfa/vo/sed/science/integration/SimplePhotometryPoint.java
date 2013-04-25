/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cfa.vo.sed.science.integration;

/**
 *
 * @author olaurino
 */
public interface SimplePhotometryPoint {
    public String getId();
    public void setId(String id);
    public Double getWavelength();
    public void setWavelength(Double wl);
    public Double getFlux();
    public void setFlux(Double flux);
}
