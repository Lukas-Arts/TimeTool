package lynx.TimeTool.GUI.diagram;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Collection;

public class DiagramPanel extends AbstractDiagram {
    private Diagram d;
    private Legend l;
    public DiagramPanel(Diagram d){
        this.setLayout(new BorderLayout());
        this.add(d,BorderLayout.CENTER);
        this.d=d;
    }
    public DiagramPanel(Diagram d,boolean legend){
        this(d);
        if(legend){
            this.l=new Legend(d,new ArrayList<>());
            this.add(l,BorderLayout.EAST);
        }
    }
    public DiagramPanel(Diagram d, boolean legend, ArrayList<JPanel> otherSettings){
        this(d);
        if(legend){
            this.l=new Legend(d,otherSettings);
            this.add(l,BorderLayout.EAST);
        }
    }
    @Override
    public void setXAxis(Axis x) {
        d.setXAxis(x);
    }
    @Override
    public void setYAxis(Axis y) {
        d.setYAxis(y);
    }
    @Override
    public void setLines(Collection<DataLine> lines) {
        d.setLines(lines);
        if(l!=null)l.setLines(lines);
    }
    @Override
    public Collection<DataLine> getLines() {
        return d.getLines();
    }
    @Override
    public DataLine getLine(String name) {
        return d.getLine(name);
    }
    @Override
    public void addLine(DataLine line) {
        d.addLine(line);
        if(l!=null)l.addLine(line);
    }
    @Override
    public void removeLine(DataLine line) {
        d.removeLine(line);
        if(l!=null)l.removeLine(line);
    }
    @Override
    public void removeLine(String name) {
        if(l!=null)l.removeLine(d.getLine(name));
        d.removeLine(name);
    }
    @Override
    public void clear() {
        d.clear();
        if(l!=null)l.clear();
    }
    @Override
    public void setType(String type) {
        d.setType(type);
    }

    @Override
    public void setMonth(int year, int month) {
        d.setMonth(year,month);
    }
}
