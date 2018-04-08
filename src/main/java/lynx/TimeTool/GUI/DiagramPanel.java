package lynx.TimeTool.GUI;

import lynx.TimeTool.TimeTool;
import lynx.TimeTool.Util.Util;
import lynx.TimeTool.WorkTimeItem;

import javax.swing.*;
import java.awt.*;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class DiagramPanel extends JPanel {
    private String location;
    private List<WorkTimeItem> items=new ArrayList<>();
    private HashMap<String,Double> dayWorkMap;
    double max=100;
    public DiagramPanel(String location,int year,int month,ArrayList<String> projects){
        this.setDoubleBuffered(true);
        this.location=location;
        changeSetUp(year,month,projects);
    }
    public void changeSetUp(int year,int month,ArrayList<String> projects){
        dayWorkMap=new HashMap<>();
        ZonedDateTime zdt=ZonedDateTime.now();
        if(year<0){
            year=zdt.getYear();
        }
        if(month<0){
            month=zdt.getMonthValue();
        }
        String location=this.location.replace(".csv","_"+year+"_"+ Util.getWithLeadingZero(month)+".csv");
        items=TimeTool.readWorkTimeFile(location,projects);
        for(WorkTimeItem item:items){
            String d=item.getStartDate().split("-")[2];
            dayWorkMap.put(d,dayWorkMap.getOrDefault(d, 0.0)+item.getDuration()/1000.0/60/60);
        }
        max=0;
        for(double l:dayWorkMap.values()){
            if(max<l){
                max=l;
            }
        }
        this.repaint();
    }
    public void paint(Graphics g){
        ((Graphics2D)g).setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);
        int h=this.getHeight();
        int w=this.getWidth();
        g.setColor(Color.BLACK);
        g.drawLine(40,5,40,h-35);
        g.drawLine(35,h-40,w-5,h-40);
        for(int i=0;i<max+1;i++){
            int y=(int)(h-40-(i*(h-50)/(max+1)));
            g.drawLine(35,y,40,y);
            g.drawString(i+"",13,y+5);
        }
        int i=0;
        for(String d:dayWorkMap.keySet()){
            int x=40+(int)((i+1)*(w-50.0)/(dayWorkMap.keySet().size()+1));
            double val=dayWorkMap.get(d);
            g.setColor(Color.BLUE);
            int y=(int)(val*(h-45)/(max+1));

            g.fillRect(x-10,h-40-y,20,y);
            g.setColor(Color.BLACK);
            g.drawLine(x,h-35,x,h-40);
            g.drawString(d,x-7,h-20);
            i++;
        }
    }
}
