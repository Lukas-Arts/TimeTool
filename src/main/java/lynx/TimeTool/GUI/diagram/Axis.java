package lynx.TimeTool.GUI.diagram;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class Axis {
    public static final boolean HORIZONTAL=true;
    public static final boolean VERTICAL=false;
    private boolean orientation;
    private List<Value> axisItems;
    private Padding p;
    private String text="";
    public Axis(boolean orientation,List<Value> axisItems,Padding p){
        this.orientation=orientation;
        this.axisItems=axisItems;
        this.p=p;
    }
    public Axis(boolean orientation,List<Value> axisItems,Padding p,String text){
        this(orientation,axisItems,p);
        this.text=text;
    }
    public static Axis getAxis(boolean orientation,double max,double steps,Padding p,String text){
        ArrayList<Value> items=new ArrayList<>();
        for(double i=0;i<=max;i+=steps){
            items.add(new Value(i));
        }
        return new Axis(orientation,items,p,text);
    }
    /**
     *  @return the Location of the Value on the DiagramPanel. (The Location is set to 0-Location, if the Value is not
     *  contained in axisItems and the axis is not of Type java.lang.Number
     */
    public int getLocation(Value val,int width,int height){
        if(axisItems.contains(val)){
            if(orientation==HORIZONTAL){
                int size2=width-p.getLeft()-p.getRight();
                int x=(int)(size2*(((double)axisItems.indexOf(val))/(axisItems.size())));
                return p.getLeft()+x;
            }else{
                int size2=height-p.getTop()-p.getBottom();
                int y=(int)(size2*(((double)axisItems.indexOf(val))/(axisItems.size())));
                return (height-p.getBottom())-y;
            }
        }else{
            try{
                Value v=axisItems.get(axisItems.size()-1);
                double max=(double)(v.getValue());
                if(orientation==HORIZONTAL){
                    int size2=width-p.getLeft()-p.getRight();
                    int maxX=(int)(size2*(((double)axisItems.indexOf(v))/(axisItems.size())));
                    int x=(int)(maxX*((double)val.getValue())/max);
                    return p.getLeft()+x;
                }else{
                    int size2=height-p.getTop()-p.getBottom();
                    int maxY=(int)(size2*(((double)axisItems.indexOf(v))/(axisItems.size())));
                    int y=(int)(maxY*(((double)val.getValue())/max));
                    return (height-p.getBottom())-y;
                }
            }catch (ClassCastException e){
                return getNullLocation(width,height);
            }
        }
    }
    public int getNullLocation(int width,int height){
        if(orientation==HORIZONTAL){
            return p.getLeft();
        }else{
            return height-p.getBottom();
        }
    }

    public void paint(Graphics g,int width,int height){
        if(orientation==HORIZONTAL){
            g.drawLine(p.getLeft()-5,height-p.getBottom(),width-p.getRight(),height-p.getBottom());
            for(int i=0;i<axisItems.size();i++){
                int size2=width-p.getLeft()-p.getRight();
                int x=p.getLeft()+i*size2/axisItems.size();
                g.drawLine(x,height-p.getBottom(),x,height-p.getBottom()+5);
                String s=axisItems.get(i).toString();
                g.drawString(s,x-(7*s.length()/2),height-p.getBottom()+18);
            }
            g.drawString(text,width-p.getRight()-(9*text.length()/2),height-p.getBottom()+18);
        }else{
            g.drawLine(p.getLeft(),p.getTop(),p.getLeft(),height-p.getBottom()+5);
            for(int i=0;i<axisItems.size();i++){
                int size2=height-p.getTop()-p.getBottom();
                int y=height-p.getBottom()-(i*size2/axisItems.size());
                g.drawLine(p.getLeft(),y,p.getLeft()-5,y);
                String s=axisItems.get(i).toString();
                g.drawString(s,p.getLeft()-20-(7*s.length()/2),y+5);
            }
            g.drawString(text,p.getLeft()-20-(9*text.length()/2),p.getTop()+5);
        }
    }
}
