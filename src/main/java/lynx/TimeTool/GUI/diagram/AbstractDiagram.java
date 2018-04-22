package lynx.TimeTool.GUI.diagram;

import javax.swing.*;
import java.util.Collection;

public abstract class AbstractDiagram extends JPanel {
    public abstract void setXAxis(Axis x);
    public abstract void setYAxis(Axis y);
    public abstract void setLines(Collection<DataLine> lines);
    public abstract Collection<DataLine> getLines();
    public abstract DataLine getLine(String name);
    public abstract void addLine(DataLine line);
    public abstract void removeLine(DataLine line);
    public abstract void removeLine(String name);
    public abstract void clear();
    public abstract void setType(String type);
    public abstract void setMonth(int year,int month);
}
