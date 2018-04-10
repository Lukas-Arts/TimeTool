package lynx.TimeTool.GUI;

import lynx.TimeTool.GUI.diagram.*;
import lynx.TimeTool.Setting;
import lynx.TimeTool.TimeTool;
import lynx.TimeTool.Util.Util;
import lynx.TimeTool.WorkTimeItem;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.File;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;

public class DiagramGUI extends JFrame {
    private HashMap<String,String> settings;
    private DiagramPanel dp;
    private JComboBox yearSelector;
    private JPanel yearSelectorPanel;
    private JComboBox monthSelector;
    private JPanel monthSelectorPanel;
    public DiagramGUI(HashMap<String,String> settings){
        super("Diagram");
        this.settings=settings;
        int width=400;
        int height=300;
        this.setSize(width,height);
        Toolkit t=Toolkit.getDefaultToolkit();
        this.setLocation(t.getScreenSize().width / 2 - width / 2, t.getScreenSize().height / 2 - height / 2);
        this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        TimeTool tt=new TimeTool(settings);
        tt.monthly();
        init();
        this.setVisible(true);
    }
    private void init(){
        this.setLayout(new BorderLayout());
        JPanel jp=new JPanel(new BorderLayout());
        //JPanel jp2=new JPanel(new GridLayout(1,2));
        yearSelectorPanel=new JPanel();
        yearSelectorPanel.setLayout(new BorderLayout());
        yearSelectorPanel.add(new JLabel(" Year: "),BorderLayout.CENTER);
        HashMap<Integer,ArrayList<Integer>> yearMonthMap=getAvailable();
        Object[] years=yearMonthMap.keySet().toArray();
        Arrays.sort(years);
        yearSelector=new JComboBox(years);
        yearSelector.setSelectedIndex(yearSelector.getItemCount()-1);
        yearSelectorPanel.add(yearSelector,BorderLayout.EAST);
        //jp2.add(yearSelectorPanel);
        monthSelectorPanel=new JPanel();
        monthSelectorPanel.setLayout(new BorderLayout());
        monthSelectorPanel.add(new JLabel(" Month: "),BorderLayout.CENTER);
        Object[] months=yearMonthMap.get(yearSelector.getSelectedItem()).toArray();
        Arrays.sort(months);
        monthSelector=new JComboBox(months);
        monthSelector.setSelectedIndex(monthSelector.getItemCount()-1);
        monthSelectorPanel.add(monthSelector,BorderLayout.EAST);
        //jp2.add(monthSelectorPanel);
        //jp.add(jp2,BorderLayout.NORTH);
        String loc=settings.get(Setting.LOCATION).replace(".csv",
                "_"+yearSelector.getSelectedItem()+"_"+ Util.getWithLeadingZero(Integer.parseInt(monthSelector.getSelectedItem().toString()))+".csv");
        ArrayList<String> projects=new ArrayList<>(TimeTool.getProjectsFromFile(loc));
        Collections.sort(projects);
        this.add(jp,BorderLayout.NORTH);
        yearSelector.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                if(e.getStateChange()==ItemEvent.SELECTED) {
                    monthSelector.removeAllItems();
                    for (int i : yearMonthMap.get(e.getItem())) {
                        monthSelector.addItem(i);
                    }
                    monthSelector.setSelectedIndex(monthSelector.getItemCount() - 1);
                }
            }
        });
        monthSelector.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                if(e.getStateChange()==ItemEvent.SELECTED){
                    String loc=settings.get(Setting.LOCATION).replace(".csv",
                            "_"+yearSelector.getSelectedItem()+"_"+ Util.getWithLeadingZero(Integer.parseInt(monthSelector.getSelectedItem().toString()))+".csv");
                    ArrayList<String> projects=new ArrayList<>(TimeTool.getProjectsFromFile(loc));
                    Collections.sort(projects);
                    changeSetup((int)yearSelector.getSelectedItem(),(int)e.getItem(),projects);
                    repaint();
                    revalidate();
                }
            }
        });
        changeSetup(-1,-1,new ArrayList<>(projects));
        this.add(dp,BorderLayout.CENTER);
    }
    private void changeSetup(int year,int month,ArrayList<String> projects){
        HashMap<String,Double> dayWorkMap=new HashMap<>();
        ZonedDateTime zdt=ZonedDateTime.now();
        if(year<0){
            year=zdt.getYear();
        }
        if(month<0){
            month=zdt.getMonthValue();
        }
        String location=settings.get(Setting.LOCATION).replace(".csv","_"+year+"_"+ Util.getWithLeadingZero(month)+".csv");
        ArrayList<WorkTimeItem> items=TimeTool.readWorkTimeFile(location,projects);
        Padding padding=new Padding(20,20,30,55);
        ArrayList<Value> xAxisItems=new ArrayList<>();
        Axis xAxis=new Axis(Axis.HORIZONTAL,xAxisItems,padding,"(Days)");
        xAxisItems.add(new Value(""));
        ArrayList<DataLine> lines=new ArrayList<>();
        for(WorkTimeItem item:items){
            String d=item.getStartDate().split("-")[2];
            dayWorkMap.put(d,dayWorkMap.getOrDefault(d, 0.0)+item.getDuration()/1000.0/60/60);
            DataLine line=null;
            for(DataLine l:lines){
                if(l.getName().equalsIgnoreCase(item.getProject())){
                    line=l;
                }
            }
            if(line==null){
                line=new DataLine(item.getProject());
                lines.add(line);
            }
            Value x=new Value(d);
            line.add(new DataItem(x,new Value(item.getDuration()/1000.0/60/60)));

            if(!xAxisItems.contains(x)){
                xAxisItems.add(x);
            }
        }
        double max=0;
        for(double l:dayWorkMap.values()){
            if(max<l){
                max=l;
            }
        }
        Axis yAxis=Axis.getAxis(Axis.VERTICAL,Math.ceil(max),1,padding,"(Hours)");
        if(dp==null){
            ArrayList<JPanel> otherSettings=new ArrayList<>();
            otherSettings.add(yearSelectorPanel);
            otherSettings.add(monthSelectorPanel);
            dp=new DiagramPanel(new Diagram(xAxis,yAxis,lines,Diagram.BAR_FILLED_STACKED),true,otherSettings);
        }else{
            dp.clear();
            dp.setXAxis(xAxis);
            dp.setYAxis(yAxis);
            dp.setLines(lines);
        }
    }
    private HashMap<Integer, ArrayList<Integer>> getAvailable(){
        HashMap<Integer,ArrayList<Integer>> yearMonthMap=new HashMap<>();
        File file=new File(settings.get(Setting.LOCATION));
        String name=file.getName().replace(".csv","");
        File dir=file.getParentFile();
        for(File f:dir.listFiles()){
            if(f.getName().matches(name+"_(\\d){4}_(\\d){2}\\.csv")){
                String s[]=f.getName().replace(".csv","").split("_");
                int year=Integer.parseInt(s[1]);
                int month=Integer.parseInt(s[2]);
                if(yearMonthMap.containsKey(year)){
                    yearMonthMap.get(year).add(month);
                }else{
                    ArrayList<Integer> months=new ArrayList<>();
                    months.add(month);
                    yearMonthMap.put(year,months);
                }
            }
        }
        return yearMonthMap;
    }
    public static void main(String args[]){
        HashMap<String,String> settings=new HashMap<>();
        settings.put(Setting.LOCATION,"./time.csv");
        new DiagramGUI(settings);
    }
}