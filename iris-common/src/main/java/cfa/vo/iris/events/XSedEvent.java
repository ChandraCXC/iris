/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package cfa.vo.iris.events;

import cfa.vo.iris.sed.XSed;

/**
 *
 * @author olaurino
 */
public class XSedEvent extends GenericEvent<XSed, XSedListener, SedCommand> {
    private static class Holder {
        private static final XSedEvent INSTANCE = new XSedEvent();
    }

    public static XSedEvent getInstance() {
        return Holder.INSTANCE;
    }
}
