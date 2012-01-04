/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package cfa.vo.iris.logging;

import cfa.vo.iris.events.GenericEvent;

/**
 *
 * @author olaurino
 */
public class LogEvent extends GenericEvent<Object, LogListener, LogEntry>{
    private static class Holder {
        private static final LogEvent INSTANCE = new LogEvent();
    }

    public static LogEvent getInstance() {
        return Holder.INSTANCE;
    }
}
