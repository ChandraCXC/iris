/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package spv.components;

import cfa.vo.sedlib.Segment;
import java.util.List;
import javax.swing.JInternalFrame;

/**
 *
 * @author olaurino
 */
public interface Plotter {

    public void addSegment(Segment segment);

    public void addSegments(List<Segment> segments);

    public void removeSegment(Segment segment);

    public void removeSegments(List<Segment> segments);

    public void setTitle(String title);

    public void reset();

    public void display();

    public JInternalFrame getFrame();
    
}
