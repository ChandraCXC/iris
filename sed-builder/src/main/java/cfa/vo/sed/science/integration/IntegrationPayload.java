/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cfa.vo.sed.science.integration;

import java.util.List;

/**
 *
 * @author olaurino
 */
public interface IntegrationPayload {
    public List<TransmissionCurve> getCurves();
    public void addCurve(TransmissionCurve curve);
    public List<Window> getWindows();
    public void addWindow(Window window);
    public double[] getX();
    public void setX(double[] x);
    public double[] getY();
    public void setY(double[] y);
}
