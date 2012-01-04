/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package cfa.vo.iris.events;

import cfa.vo.iris.events.SegmentEvent.SegmentPayload;
import cfa.vo.iris.sed.SedlibSedManager.ExtSed;
import cfa.vo.sedlib.Segment;

/**
 *
 * @author olaurino
 */
public class SegmentEvent extends GenericEvent<Segment, SegmentListener, SegmentPayload> {
    private static class Holder {
        private static final SegmentEvent INSTANCE = new SegmentEvent();
    }

    public static SegmentEvent getInstance() {
        return Holder.INSTANCE;
    }

    public static class SegmentPayload {
        private ExtSed sed;
        private SedCommand cmd;

        public SegmentPayload(ExtSed sed, SedCommand cmd) {
            this.sed = sed;
            this.cmd = cmd;
        }

        public ExtSed getSed() {
            return sed;
        }

        public SedCommand getSedCommand() {
            return cmd;
        }
    }
}
