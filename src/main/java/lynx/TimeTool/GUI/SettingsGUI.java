package lynx.TimeTool.GUI;

import lynx.TimeTool.Settings;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Properties;

public class SettingsGUI extends JFrame {
    private HashMap<String,JComponent> fields=new HashMap<>();
    private HashMap<String,String> settings;
    public SettingsGUI(HashMap<String,String> settings){
        super("Settings");
        this.settings=settings;
        int width=400;
        int height=300;
        this.setSize(width,height);
        Toolkit t=Toolkit.getDefaultToolkit();
        this.setLocation(t.getScreenSize().width / 2 - width / 2, t.getScreenSize().height / 2 - height / 2);
        this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        init();
        this.setVisible(true);
    }
    public void init(){
        JPanel jp=new JPanel(new BorderLayout());
        this.add(jp);

        JPanel jp2=new JPanel();
        jp2.setBorder(BorderFactory.createEmptyBorder(5,5,5,5));
        jp2.setLayout(new GridLayout(settings.size(),1));
        jp.add(jp2,BorderLayout.NORTH);
        ArrayList<String> settingKeys=new ArrayList<>(settings.keySet());
        Collections.sort(settingKeys);
        for(String key:settingKeys){
            JPanel jpi=new JPanel(new GridLayout(1,2));
            jpi.add(new JLabel(key.replace("_"," ")+": "));
            if(key.contains("Color")){
                JPanel jpc=new JPanel();
                Color c=Settings.getColorFromString(settings.get(key));
                jpc.setBackground(c);
                jpc.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseClicked(MouseEvent e) {
                        jpc.setBackground(JColorChooser.showDialog(jpc,"Color-Picker",c));
                    }
                });
                fields.put(key,jpc);
                jpi.add(jpc);
            } else if(isBoolean(settings.get(key))){
                JCheckBox jcb=new JCheckBox("",Boolean.parseBoolean(settings.get(key)));
                fields.put(key,jcb);
                jpi.add(jcb);
            } else {
                JTextField jtf=new JTextField(settings.get(key));
                fields.put(key,jtf);
                jpi.add(jtf);
            }
            jp2.add(jpi);
        }

        //Cancel/Apply-Buttons
        JPanel jpx=new JPanel(new GridLayout(1,3));
        JButton cancel=new JButton("Cancel");
        cancel.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });
        jpx.add(cancel);
        jpx.add(new JLabel());
        JButton apply=new JButton("Apply");
        apply.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                applyChanges();
                dispose();
            }
        });
        jpx.add(apply);
        jp.add(jpx,BorderLayout.SOUTH);
    }

    private boolean isBoolean(String s) {
        if(s==null)
            return false;
        return s.equalsIgnoreCase("true")||s.equalsIgnoreCase("false");
    }

    private void applyChanges(){
        for(String key:fields.keySet()){
            if(key.contains("Color")){
                Color c=((JPanel)fields.get(key)).getBackground();
                settings.put(key,"r="+c.getRed()+"|g="+c.getGreen()+"|b="+c.getBlue()+"|a="+c.getAlpha());
            }else{
                JComponent jc=fields.get(key);
                if(jc instanceof JCheckBox){
                    settings.put(key,((JCheckBox) jc).isSelected()+"");
                }else{
                    settings.put(key,((JTextField)fields.get(key)).getText());
                }
            }
        }
        Properties p=new Properties();
        for(String key:settings.keySet())
            p.setProperty(key,settings.get(key));
        try {
            p.store(new FileWriter("./time.props"),"");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
