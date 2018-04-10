package lynx.TimeTool.GUI.diagram;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

public class Diagram extends AbstractDiagram {
    private Collection<DataLine> lines;
    private Axis xAxis;
    private Axis yAxis;
    private static final String STACKED="STACKED";
    public static final String BAR="BAR";
    public static final String BAR_FILLED="BAR_FILLED";
    public static final String BAR_STACKED="BAR_"+STACKED;
    public static final String BAR_FILLED_STACKED="BAR_FILLED_"+STACKED;
    public static final String LINE="LINE";
    private String type;
    public Diagram(Axis xAxis, Axis yAxis, Collection<DataLine> lines){
        this.xAxis=xAxis;
        this.yAxis=yAxis;
        this.lines = lines;
        this.type=BAR;
    }
    public Diagram(Axis xAxis, Axis yAxis, Collection<DataLine> lines, String type){
        this(xAxis,yAxis,lines);
        this.type=type;
        if(type.contains(STACKED)){
            try{
                Number n=(Number)lines.iterator().next().getPoints().iterator().next().getY().getValue();
            }catch (ClassCastException e){
                System.err.println("ERROR: Can't stack Non-Number Values!");
            }
        }
    }
    public void setXAxis(Axis x){
        this.xAxis=x;
    }
    public void setYAxis(Axis y){
        this.yAxis=y;
    }
    public void setLines(Collection<DataLine> lines){
        this.lines=lines;
    }
    public Collection<DataLine> getLines(){
        return this.lines;
    }
    public DataLine getLine(String name){
        DataLine line=null;
        for(DataLine l:lines){
            if(l.getName().equalsIgnoreCase(name))
                line=l;
        }
        return line;
    }
    public void addLine(DataLine line){
        this.lines.add(line);
    }
    public void removeLine(DataLine line){
        this.lines.remove(line);
    }
    public void removeLine(String name){
        this.removeLine(getLine(name));
    }
    public void clear(){
        this.lines.clear();
    }
    public void setType(String type){
        System.out.println(type);
        this.type=type;
    }
    public void paint(Graphics g){
        ((Graphics2D)g).setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);
        g.setColor(Color.WHITE);
        g.fillRect(0,0,getWidth(),getHeight());
        g.setColor(Color.BLACK);
        xAxis.paint(g,getWidth(),getHeight());
        yAxis.paint(g,getWidth(),getHeight());
        int offsetStep=10;
        int offset= -(lines.size()*offsetStep)/2;
        HashMap<String,Value> stacked=new HashMap<>();
        for(DataLine line:lines){
            Point lastPoint=null;
            for(DataItem point: line.getPoints()){
                int x=xAxis.getLocation(point.getX(),getWidth(),getHeight());
                int y=yAxis.getLocation(point.getY(),getWidth(),getHeight());
                g.setColor(point.getColor());
                switch (type){
                    case BAR:{
                        g.drawRect(x+offset,y,offsetStep,yAxis.getNullLocation(getWidth(),getHeight())-y);
                        break;
                    }
                    case BAR_FILLED:{
                        g.fillRect(x+offset,y,offsetStep,yAxis.getNullLocation(getWidth(),getHeight())-y);
                        break;
                    }
                    case BAR_STACKED:{
                        Value val=stacked.get(point.getX().toString());
                        double lastY;
                        double thisY=((Number)point.getY().getValue()).doubleValue();
                        if(val!=null){
                            lastY=((Number)val.getValue()).doubleValue();
                            val.setValue(lastY+thisY);
                        }else {
                            val=point.getY().copy();
                            stacked.put(point.getX().toString(),val);
                            lastY=0;
                        }
                        int top=yAxis.getLocation(val,getWidth(),getHeight());
                        int bottom=yAxis.getLocation(new Value(lastY),getWidth(),getHeight());
                        g.drawRect(x-5,top,10,bottom-top);
                        break;
                    }
                    case BAR_FILLED_STACKED:{
                        Value val=stacked.get(point.getX().toString());
                        double lastY;
                        double thisY=((Number)point.getY().getValue()).doubleValue();
                        if(val!=null){
                            lastY=((Number)val.getValue()).doubleValue();
                            val.setValue(lastY+thisY);
                        }else {
                            val=point.getY().copy();
                            stacked.put(point.getX().toString(),val);
                            lastY=0;
                        }
                        int top=yAxis.getLocation(val,getWidth(),getHeight());
                        int bottom=yAxis.getLocation(new Value(lastY),getWidth(),getHeight());
                        g.fillRect(x-5,top,10,bottom-top);
                        break;
                    }
                    case LINE:{
                        if(lastPoint!=null){
                            g.drawLine(x,y,lastPoint.x,lastPoint.y);
                        }
                        g.setColor(Color.BLACK);
                        g.drawLine(x-2,y-2,x+2,y+2);
                        g.drawLine(x+2,y-2,x-2,y+2);
                        lastPoint=new Point(x,y);
                        break;
                    }
                }
            }
            if(!type.contains(STACKED)){
                offset+=offsetStep;
            }
        }
    }
    public static void main(String args[]){
        JFrame jf=new JFrame();
        jf.setSize(500,500);
        Padding p=new Padding(20,20,60,60);
        ArrayList<Value> xItems=new ArrayList<>();
        xItems.add(new Value(""));
        xItems.add(new Value("Montag"));
        xItems.add(new Value("Dienstag"));
        xItems.add(new Value("Mittwoch"));
        xItems.add(new Value("Donnerstag"));
        xItems.add(new Value("Freitag"));
        Axis xAxis=new Axis(Axis.HORIZONTAL,xItems,p,"(Days)");
        Axis yAxis=Axis.getAxis(Axis.VERTICAL,15.0,1.0,p,"(Hours)");

        ArrayList<DataLine> lines=new ArrayList<>();
        DataLine line=new DataLine("test",Color.BLUE);
        line.add(new DataItem(new Value("Montag"),new Value(0.5)));
        line.add(new DataItem(new Value("Dienstag"),new Value(2.0)));
        line.add(new DataItem(new Value("Mittwoch"),new Value(3.0)));
        line.add(new DataItem(new Value("Donnerstag"),new Value(6.5)));
        line.add(new DataItem(new Value("Freitag"),new Value(5.0)));
        lines.add(line);
        DataLine line2=new DataLine("test2",Color.RED);
        line2.add(new DataItem(new Value("Montag"),new Value(2.0)));
        line2.add(new DataItem(new Value("Dienstag"),new Value(5.0)));
        line2.add(new DataItem(new Value("Mittwoch"),new Value(3.5)));
        line2.add(new DataItem(new Value("Donnerstag"),new Value(4.0)));
        line2.add(new DataItem(new Value("Freitag"),new Value(3.5)));
        lines.add(line2);
        jf.add(new Diagram(xAxis,yAxis,lines,BAR_FILLED));
        jf.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        jf.setVisible(true);
    }
}
