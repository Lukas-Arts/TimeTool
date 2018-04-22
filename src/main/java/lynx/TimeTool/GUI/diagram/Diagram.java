package lynx.TimeTool.GUI.diagram;

import javax.swing.*;
import java.awt.*;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.temporal.WeekFields;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Locale;

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
    public static final String CALENDAR="CALENDAR";

    public static final int RIGHT=0b0001;
    public static final int LEFT=0b0010;
    public static final int HORIZONTAL_CENTER=0b0011;
    public static final int CENTER=0b1111;
    public static final int TOP=0b0100;
    public static final int BOTTOM=0b1000;
    public static final int VERTICAL_CENTER=0b1100;

    private String type;
    private int days=31;
    private int dayOne=0;
    private int weekOfYear=0;
    private DecimalFormat df=new DecimalFormat("0.0#");
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
        this.type=type;
    }
    public void setMonth(int year,int month){
        LocalDate ld=LocalDate.of(year,month,1);
        days=ld.lengthOfMonth();
        dayOne=ld.getDayOfWeek().getValue();
        weekOfYear=ld.get(WeekFields.of(Locale.getDefault()).weekOfWeekBasedYear());
    }
    public void paint(Graphics g){
        ((Graphics2D)g).setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);
        g.setColor(Color.WHITE);
        g.fillRect(0,0,getWidth(),getHeight());

        if(type.equalsIgnoreCase(CALENDAR)){
            //System.out.println("drawing calendar");
            int paddingH=50;
            int paddingW=70;
            int widthPerDay=(int)((getWidth()-paddingW)/7.0);
            int heightPerDay=(int)((getHeight()-paddingH)/Math.ceil((days+(dayOne-1))/7.0));
            HashMap<String,Value> stacked=new HashMap<>();
            for(DataLine line:lines) {
                for (DataItem point : line.getPoints()) {
                    g.setColor(point.getColor());
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
                    try{
                        int day=Integer.parseInt(point.getX().toString().split("\n")[0])+(dayOne-1)-1;
                        int week=(int)Math.floor(day/7);
                        int top=(int)(paddingH/2+week*heightPerDay+heightPerDay-(heightPerDay*((double)val.getValue()/24.0)));
                        int bottom=(int)(paddingH/2+week*heightPerDay+heightPerDay-(heightPerDay*(lastY/24.0)));
                        g.fillRect((paddingW/2-5+(day%7)*widthPerDay),top,widthPerDay,bottom-top);
                        //System.out.println("drawing rect "+(int)((day/7)*widthPerDay)+" "+top+" "+bottom+" : "+val);
                    }catch (ArrayIndexOutOfBoundsException e){
                        System.err.println(e.getMessage());
                    }
                }
            }
            g.setColor(Color.BLACK);
            drawString(g,"Mon",(paddingW/2-5+0*widthPerDay+widthPerDay/2),paddingH/2/2,CENTER);
            drawString(g,"Tue",(paddingW/2-5+1*widthPerDay+widthPerDay/2),paddingH/2/2,CENTER);
            drawString(g,"Wen",(paddingW/2-5+2*widthPerDay+widthPerDay/2),paddingH/2/2,CENTER);
            drawString(g,"Thu",(paddingW/2-5+3*widthPerDay+widthPerDay/2),paddingH/2/2,CENTER);
            drawString(g,"Fri",(paddingW/2-5+4*widthPerDay+widthPerDay/2),paddingH/2/2,CENTER);
            drawString(g,"Sat",(paddingW/2-5+5*widthPerDay+widthPerDay/2),paddingH/2/2,CENTER);
            drawString(g,"Sun",(paddingW/2-5+6*widthPerDay+widthPerDay/2),paddingH/2/2,CENTER);
            double sum=0;
            HashMap<Integer,Double> weekSums=new HashMap<>();
            for(String s:stacked.keySet()){
                int day=Integer.parseInt(s.split("\n")[0])+(dayOne-1)-1;
                int week=(int)Math.floor(day/7);
                double val2=(double)stacked.get(s).getValue();
                double sum3=weekSums.getOrDefault(week,0.0);
                weekSums.put(week,sum3+val2);
                sum+=val2;
                String s2=df.format(val2);
                drawString(g,s2,(paddingW/2-5+(day%7)*widthPerDay+widthPerDay/2),
                                (paddingH/2+week*heightPerDay+heightPerDay/2),CENTER);
            }
            drawString(g,"Sum="+df.format(sum),getWidth()/2-5,getHeight()-(paddingH/2)/2,CENTER);
            int weeks=(int)Math.ceil((dayOne+days)/7.0);
            for(int j=0;j<weeks;j++){
                double weekSum=weekSums.getOrDefault(j,0.0);
                drawString(g,"="+df.format(weekSum),getWidth()-(paddingW/2)/2,paddingH/2+j*heightPerDay+heightPerDay/2,CENTER);
                for(int i=0;i<7;i++){
                    drawString(g,weekOfYear+j+"",(paddingW/2-5)/2,paddingH/2+j*heightPerDay+heightPerDay/2,CENTER);
                    if(i+j*7>=dayOne-1&&i+j*7<days+dayOne-1){
                        g.setFont(new Font(getFont().getName(),Font.PLAIN,8));
                        drawString(g,i+j*7-(dayOne-1)+1+"",paddingW/2-2+i*widthPerDay,paddingH/2+j*heightPerDay,BOTTOM|LEFT);
                        g.setFont(new Font(getFont().getName(),Font.PLAIN,12));
                    }else {
                        g.setColor(Color.lightGray);
                        g.fillRect(paddingW/2+i*widthPerDay-5,paddingH/2+j*heightPerDay,widthPerDay,heightPerDay);
                    }
                    g.setColor(Color.BLACK);
                    //g.drawRect(paddingW/2+(int)(i*widthPerDay)-5,paddingH/2+(int)(j*heightPerDay),(int)(widthPerDay),(int)(heightPerDay));
                }
                g.drawLine(paddingW/2-5,paddingH/2+j*heightPerDay,paddingW/2-5+7*widthPerDay,paddingH/2+j*heightPerDay);
            }
            g.drawLine(paddingW/2-5,paddingH/2+weeks*heightPerDay,paddingW/2-5+7*widthPerDay,paddingH/2+weeks*heightPerDay);


            g.drawLine(paddingW/2-5+0*widthPerDay,paddingH/2,paddingW/2-5+0*widthPerDay,paddingH/2+weeks*heightPerDay);
            g.drawLine(paddingW/2-5+1*widthPerDay,paddingH/2,paddingW/2-5+1*widthPerDay,paddingH/2+weeks*heightPerDay);
            g.drawLine(paddingW/2-5+2*widthPerDay,paddingH/2,paddingW/2-5+2*widthPerDay,paddingH/2+weeks*heightPerDay);
            g.drawLine(paddingW/2-5+3*widthPerDay,paddingH/2,paddingW/2-5+3*widthPerDay,paddingH/2+weeks*heightPerDay);
            g.drawLine(paddingW/2-5+4*widthPerDay,paddingH/2,paddingW/2-5+4*widthPerDay,paddingH/2+weeks*heightPerDay);
            g.drawLine(paddingW/2-5+5*widthPerDay,paddingH/2,paddingW/2-5+5*widthPerDay,paddingH/2+weeks*heightPerDay);
            g.drawLine(paddingW/2-5+6*widthPerDay,paddingH/2,paddingW/2-5+6*widthPerDay,paddingH/2+weeks*heightPerDay);
            g.drawLine(paddingW/2-5+7*widthPerDay,paddingH/2,paddingW/2-5+7*widthPerDay,paddingH/2+weeks*heightPerDay);
        }else{
            //int one=(xAxis.getLocation(xAxis.getAxisItems().get(1),getWidth(),getHeight())-xAxis.getPadding().getLeft());
            int offsetStep=(xAxis.getLocation(xAxis.getAxisItems().get(1),getWidth(),getHeight())-xAxis.getPadding().getLeft());
            int offset= -(int)Math.ceil((offsetStep)/2);
            HashMap<String,Value> stacked=new HashMap<>();
            for(DataLine line:lines){
                Point lastPoint=null;
                for(DataItem point: line.getPoints()){
                    int x=xAxis.getLocation(point.getX(),getWidth(),getHeight());
                    int y=yAxis.getLocation(point.getY(),getWidth(),getHeight());
                    g.setColor(point.getColor());
                    switch (type){
                        case BAR:{
                            g.drawRect(x+offset,y,(int)Math.ceil(offsetStep/(double)lines.size()),yAxis.getNullLocation(getWidth(),getHeight())-y);
                            break;
                        }
                        case BAR_FILLED:{
                            g.fillRect(x+offset,y,(int)Math.ceil(offsetStep/(double)lines.size()),yAxis.getNullLocation(getWidth(),getHeight())-y);
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
                            g.drawRect(x-(offsetStep-10)/2,top,offsetStep-10,bottom-top);
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
                            g.fillRect(x-(offsetStep-10)/2,top,offsetStep-10,bottom-top);
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
                    offset+=(int)Math.ceil(offsetStep/(double)lines.size());
                }
            }
            g.setColor(Color.BLACK);
            xAxis.paint(g,getWidth(),getHeight());
            yAxis.paint(g,getWidth(),getHeight());
        }
    }
    private static void drawString(Graphics g,String s,int x,int y,int orientation){
        drawString(g,s,x,y,orientation,false);
    }
    private static void drawString(Graphics g,String s,int x,int y,int orientation,boolean debug){
        if(debug){
            Color c=g.getColor();
            g.setColor(Color.RED);
            g.drawLine(x-1,y-1,x+1,y+1);
            g.drawLine(x+1,y-1,x-1,y+1);
            g.setColor(c);
        }
        g.drawString(s,x-getHorizontalOffset(g,s,orientation,debug),y-getVerticalOffset(g,s,orientation,debug));
    }
    private static int getHorizontalOffset(Graphics g,String s,int orientation,boolean debug){
        orientation=orientation&HORIZONTAL_CENTER;
        int offset;
        switch (orientation){
            case RIGHT:{
                offset=g.getFontMetrics().stringWidth(s);
                break;
            }
            case HORIZONTAL_CENTER:{
                offset=g.getFontMetrics().stringWidth(s)/2;
                break;
            }
            case LEFT:
            default: {
                offset=0;
                break;
            }
        }
        if(debug)System.out.println("HOffset: "+offset);
        return offset;
    }
    private static int getVerticalOffset(Graphics g,String s,int orientation,boolean debug){
        orientation=orientation&VERTICAL_CENTER;
        int offset;
        switch (orientation){
            case TOP:{
                offset= 2;
                break;
            }
            case VERTICAL_CENTER:{
                offset= -g.getFontMetrics().getHeight()/2+2;
                break;
            }
            case BOTTOM:
            default: {
                offset= -g.getFontMetrics().getHeight();
                break;
            }
        }
        if(debug)System.out.println("VOffset: "+offset);
        return offset;
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
