package lynx.TimeTool.GUI.diagram;

import lynx.TimeTool.Util.ColorPalette;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Random;

public class Legend extends JPanel {
    private JPanel otherSettings;
    private JPanel jpm;
    private Diagram d;
    private HashMap<DataLine,JPanel> map=new HashMap<>();
    public Legend(Diagram d, ArrayList<JPanel> otherSettings){
        this.setLayout(new BorderLayout());
        this.otherSettings=new JPanel();
        this.otherSettings.setLayout(new BoxLayout(this.otherSettings,BoxLayout.PAGE_AXIS));
        for(JPanel j:otherSettings){
            this.otherSettings.add(j);
        }
        String s[]={"Bar","bar stacked","Bar filled","Bar filled stacked","Line"};
        JComboBox jcb=new JComboBox(s);
        jcb.setSelectedIndex(3);
        jcb.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                if(e.getStateChange()==ItemEvent.SELECTED){
                    d.setType(e.getItem().toString().toUpperCase().replace(" ","_"));
                    d.revalidate();
                    d.repaint();
                }
            }
        });
        JPanel jp=new JPanel();
        jp.setLayout(new BorderLayout());
        jp.add(new JLabel(" Type: "),BorderLayout.CENTER);
        jp.add(jcb,BorderLayout.EAST);
        this.otherSettings.add(jp);
        this.otherSettings.setBorder(BorderFactory.createTitledBorder("Other Settings"));
        this.add(this.otherSettings,BorderLayout.NORTH);
        JPanel jp2=new JPanel();
        jp2.setLayout(new BorderLayout());
        jpm=new JPanel();
        jpm.setLayout(new BoxLayout(jpm,BoxLayout.PAGE_AXIS));
        jp2.add(jpm,BorderLayout.NORTH);
        this.d=d;
        this.setLines(d.getLines());

        JScrollPane jsp=new JScrollPane(jp2);
        jsp.setBorder(BorderFactory.createTitledBorder("Legend"));
        this.add(jsp,BorderLayout.CENTER);
    }
    public void setLines(Collection<DataLine> lines){
        for(DataLine l:lines){
            addLine(l);
        }
    }
    public void addLine(DataLine line){
        JPanel jp8=new JPanel();
        jp8.setBorder(BorderFactory.createEmptyBorder(5,5,5,5));
        jp8.setLayout(new BorderLayout());
        map.put(line,jp8);
        JCheckBox jcb3=new JCheckBox(line.getName(),true);
        jcb3.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                if(e.getStateChange()==ItemEvent.SELECTED){
                    d.addLine(line);
                }else if(e.getStateChange()==ItemEvent.DESELECTED){
                    d.removeLine(line);
                }
                d.revalidate();
                d.repaint();
            }
        });
        JPanel jp7=new JPanel();
        jp7.setBorder(BorderFactory.createLineBorder(Color.BLACK,1));
        Random r=new Random();
        Color c= ColorPalette.getColor(map.size()-1);
        line.setColor(c);
        jp7.setBackground(c);
        jp7.setPreferredSize(new Dimension(22,20));
        jp8.add(jcb3,BorderLayout.CENTER);
        jp8.add(jp7,BorderLayout.EAST);
        jp7.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                Color c=JColorChooser.showDialog(jp7,"Color-Picker",jp7.getBackground());
                jp7.setBackground(c);
                line.setColor(c);
                d.revalidate();
                d.repaint();
            }
        });
        jpm.add(jp8);
    }
    public void removeLine(DataLine line){
        this.remove(map.get(line));
        map.remove(line);
    }
    public void clear(){
        for(DataLine dl:map.keySet()){
            jpm.remove(map.get(dl));
        }
        map.clear();
    }
}