/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package cfa.vo.iris.events;

import cfa.vo.iris.events.SegmentEvent.SegmentPayload;
import cfa.vo.sedlib.Segment;
import java.util.List;

/**
 *
 * @author olaurino
 */
public interface MultipleSegmentListener extends IListener<List<Segment>, SegmentPayload>{

}
