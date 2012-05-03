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
public class MultipleSegmentEvent extends GenericEvent<List<Segment>, MultipleSegmentListener, SegmentPayload> {
    private static class Holder {
        private static final MultipleSegmentEvent INSTANCE = new MultipleSegmentEvent();
    }

    public static MultipleSegmentEvent getInstance() {
        return Holder.INSTANCE;
    }
}
