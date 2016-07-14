package cfa.vo.iris.visualizer;

import cfa.vo.iris.IWorkspace;
import cfa.vo.iris.visualizer.preferences.VisualizerComponentPreferences;

public class IrisVisualizer {
    
    private VisualizerComponentPreferences preferences;
    
    private IrisVisualizer() {};
    
    private static class Holder {
        private static final IrisVisualizer INSTANCE = new IrisVisualizer();
    }
    
    public static IrisVisualizer getInstance() {
        return Holder.INSTANCE;
    }
    
    public VisualizerComponentPreferences createPreferences(IWorkspace ws) {
        if (preferences == null) {
            preferences = new VisualizerComponentPreferences(ws);
        }
        
        return preferences;
    }
    
    public VisualizerComponentPreferences getActivePreferences() {
        return preferences;
    }
}
