package cfa.vo.iris.visualizer.stil.preferences;

// http://www.star.bris.ac.uk/~mbt/stilts/sun256/layer-xyerror.html
public enum ErrorBarType {
    lines("lines"),
    capped_lines("capped_lines"),
    caps("caps"),
    arrows("arrows"),
    ellipse("ellipse"),
    crosshair_ellipse("crosshair_ellipse"),
    rectangle("rectangle"),
    crosshair_rectangle("crosshair_rectangle"),
    filled_ellipse("filled_ellipse"),
    filled_rectangle("filled_rectangle");
    
    private final String name;       

    private ErrorBarType(String s) {
        name = s;
    }

    public boolean equalsName(String otherName) {
        return (otherName == null) ? false : name.equals(otherName);
    }

    @Override
    public String toString() {
       return this.name;
    }
}
