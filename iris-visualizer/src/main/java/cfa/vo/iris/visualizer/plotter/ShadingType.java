package cfa.vo.iris.visualizer.plotter;

public enum ShadingType {
    auto("auto"),
    flat("flat"),
    translucent("translevel"),
    transparent("opaque");
    
    public String name;
    
    private ShadingType(String arg) {
        this.name = arg;
    }
}
