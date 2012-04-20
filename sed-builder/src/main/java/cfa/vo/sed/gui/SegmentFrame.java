/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package cfa.vo.sed.gui;

import cfa.vo.sedlib.Segment;

/**
 *
 * @author olaurino
 */
public interface SegmentFrame {
    public void update(Segment segment);
    public void setVisible(boolean visible);
}
