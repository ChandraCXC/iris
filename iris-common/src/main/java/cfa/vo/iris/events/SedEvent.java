/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package cfa.vo.iris.events;

import cfa.vo.iris.sed.ExtSed;

/**
 *
 * @author olaurino
 */
public class SedEvent extends GenericEvent<ExtSed, SedListener, SedCommand> {
    private static class Holder {
        private static final SedEvent INSTANCE = new SedEvent();
    }

    public static SedEvent getInstance() {
        return Holder.INSTANCE;
    }
}
