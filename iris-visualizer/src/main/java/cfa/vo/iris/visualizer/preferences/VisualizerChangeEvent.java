package cfa.vo.iris.visualizer.preferences;

import cfa.vo.iris.events.GenericEvent;
import cfa.vo.iris.sed.ExtSed;

public class VisualizerChangeEvent
        extends GenericEvent<ExtSed, VisualizerListener, VisualizerCommand> {

    private static VisualizerChangeEvent instance;

    VisualizerChangeEvent() {}

    public static VisualizerChangeEvent getInstance() {
        if (instance == null) {
             instance = new VisualizerChangeEvent();
        }
        
        return instance;
    }
}
