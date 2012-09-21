/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cfa.vo.iris.events;

import cfa.vo.iris.sdk.PluginJar;

/**
 *
 * @author olaurino
 */
public class PluginJarEvent extends GenericEvent<PluginJar, PluginListener, SedCommand> {
    private static class Holder {
        private static final PluginJarEvent INSTANCE = new PluginJarEvent();
    }

    public static PluginJarEvent getInstance() {
        return Holder.INSTANCE;
    }
    
}
