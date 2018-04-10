package lynx.TimeTool.GUI.diagram;

import java.awt.*;
import java.util.ArrayList;
import java.util.Collection;

public class DataLine {
    private Collection<DataItem> points=new ArrayList<>();
    private String name="";
    private Color color=null;   //null= points come with own color
    public DataLine(){
    }
    public DataLine(String name){
        this();
        this.name=name;
    }
    public DataLine(String name,Color c){
        this(name);
        this.color=c;
    }
    public DataLine(String name,Color c,Collection<DataItem> points){
        this(name,c);
        this.points=points;
    }
    public String getName(){
        return name;
    }
    public void add(DataItem item){
        if(color!=null){
            item.setColor(color);
        }
        DataItem di=null;
        for(DataItem i:points){
            if(item.getX().equals(i.getX())&&item.getY().getValue() instanceof Number){
                di=i;
                di.getY().setValue(((Number)i.getY().getValue()).doubleValue()+((Number)item.getY().getValue()).doubleValue());
            }
        }
        if(di==null)this.points.add(item);
    }
    public Collection<DataItem> getPoints(){
        return this.points;
    }
    public void setColor(Color c){
        for(DataItem di:points){
            di.setColor(c);
        }
        this.color=c;
    }
}