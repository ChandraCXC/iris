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
public interface Response {
    public List<SimplePhotometryPoint> getPoints();
    public void addPoint(SimplePhotometryPoint point);
}
