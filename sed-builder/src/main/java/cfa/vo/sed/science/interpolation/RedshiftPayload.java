/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cfa.vo.sed.science.interpolation;

/**
 *
 * @author olaurino
 */
public interface RedshiftPayload {

    public double[] getX();

    public void setX(double[] x);

    public double[] getY();

    public void setY(double[] y);

    public double getFromRedshift();

    public void setFromRedshift(double redshift);

    public double getToRedshift();

    public void setToRedshift(double redshift);
}
