package lynx.TimeTool.GUI;

import lynx.TimeTool.Setting;
import lynx.TimeTool.TimeTool;
import lynx.TimeTool.Util.Util;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;

public class DiagramGUI extends JFrame {
    private HashMap<String,String> settings;
    private DiagramPanel dp;
    private JComboBox yearSelector;
    private JComboBox monthSelector;
    private JPanel jp5;
    private ArrayList<JCheckBox> projectSelectors=new ArrayList<>();
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
        JPanel jp2=new JPanel(new GridLayout(1,2));
        JPanel jp3=new JPanel(new GridLayout(1,2));
        jp3.add(new JLabel(" Year:"));
        HashMap<Integer,ArrayList<Integer>> yearMonthMap=getAvailable();
        Object[] years=yearMonthMap.keySet().toArray();
        Arrays.sort(years);
        yearSelector=new JComboBox(years);
        yearSelector.setSelectedIndex(yearSelector.getItemCount()-1);
        jp3.add(yearSelector);
        jp2.add(jp3);
        JPanel jp4=new JPanel(new GridLayout(1,2));
        jp4.add(new JLabel(" Month:"));
        Object[] months=yearMonthMap.get(yearSelector.getSelectedItem()).toArray();
        Arrays.sort(months);
        monthSelector=new JComboBox(months);
        monthSelector.setSelectedIndex(monthSelector.getItemCount()-1);
        jp4.add(monthSelector);
        jp2.add(jp4);
        jp.add(jp2,BorderLayout.NORTH);
        String loc=settings.get(Setting.LOCATION).replace(".csv",
                "_"+yearSelector.getSelectedItem()+"_"+ Util.getWithLeadingZero(Integer.parseInt(monthSelector.getSelectedItem().toString()))+".csv");
        ArrayList<String> projects=new ArrayList<>(TimeTool.getProjectsFromFile(loc));
        Collections.sort(projects);
        jp5=new JPanel(new GridLayout((int)Math.ceil(projects.size()/4.0),Math.min(projects.size(),4)));
        projectSelectors.clear();
        for(String p:projects){
            JCheckBox jcb3=new JCheckBox(p,true);
            jp5.add(jcb3);
            projectSelectors.add(jcb3);
        }
        addListenerToProjects();
        JPanel jp6=new JPanel(new BorderLayout());
        jp6.add(new JLabel(" Projects: "),BorderLayout.WEST);
        jp6.add(jp5,BorderLayout.CENTER);
        jp.add(jp6,BorderLayout.CENTER);
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
                    jp5.removeAll();
                    projectSelectors.clear();
                    jp5.setLayout(new GridLayout((int)Math.ceil(projects.size()/4.0),Math.min(projects.size(),4)));
                    for(String p:projects){
                        JCheckBox jcb3=new JCheckBox(p,true);
                        jp5.add(jcb3);
                        projectSelectors.add(jcb3);
                    }
                    addListenerToProjects();
                    dp.changeSetUp((int)yearSelector.getSelectedItem(),(int)e.getItem(),projects);
                    repaint();
                    revalidate();
                }
            }
        });
        dp=new DiagramPanel(settings.get(Setting.LOCATION),-1,-1,new ArrayList<>(projects));
        this.add(dp,BorderLayout.CENTER);
    }
    private void addListenerToProjects(){
        for(JCheckBox jcb:projectSelectors){
            jcb.addItemListener(new ItemListener() {
                @Override
                public void itemStateChanged(ItemEvent e) {
                    ArrayList<String> selected=new ArrayList<>();
                    for(JCheckBox jcb:projectSelectors){
                        if(jcb.isSelected()){
                            selected.add(jcb.getText());
                        }
                    }
                    dp.changeSetUp((int)yearSelector.getSelectedItem(),(int)monthSelector.getSelectedItem(),selected);
                    repaint();
                }
            });
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