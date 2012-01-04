/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package cfa.vo.iris.events;

import cfa.vo.iris.sed.IPoint;

/**
 *
 * @author olaurino
 */
public class PointEvent extends GenericEvent<IPoint, IListener<IPoint, SedCommand>, SedCommand> {

    private static class Holder {
        private static final PointEvent INSTANCE = new PointEvent();
    }

    public static PointEvent getInstance() {
        return Holder.INSTANCE;
    }

}
