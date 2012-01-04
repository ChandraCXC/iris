/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package cfa.vo.iris.events;

import cfa.vo.iris.sed.IXSegment;

/**
 *
 * @author olaurino
 */
public class XSegmentEvent extends GenericEvent<IXSegment, XSegmentListener, SedCommand> {
    private static class Holder {
        private static final XSegmentEvent INSTANCE = new XSegmentEvent();
    }

    public static XSegmentEvent getInstance() {
        return Holder.INSTANCE;
    }
}
