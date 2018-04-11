package lynx.TimeTool.GUI;

import lynx.TimeTool.Setting;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;

public class AskOnLogoutGUI extends JFrame {
    private HashMap<String,String> settings;
    private GUI gui;
    public AskOnLogoutGUI(HashMap<String,String> settings,GUI gui){
        super("Worktime Description");
        this.settings=settings;
        this.gui=gui;
        int width=350;
        int height=200;
        this.setSize(width,height);
        Toolkit t=Toolkit.getDefaultToolkit();
        this.setLocation(t.getScreenSize().width / 2 - width / 2, t.getScreenSize().height / 2 - height / 2);
        this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        init();
        this.setVisible(true);
    }
    private void init(){
        JPanel jp=new JPanel(new BorderLayout());

        JPanel jp2=new JPanel();
        jp2.setBorder(BorderFactory.createEmptyBorder(5,5,5,5));
        jp2.setLayout(new BorderLayout());
        jp.add(jp2,BorderLayout.WEST);
        JPanel jp3=new JPanel();
        jp3.setLayout(new BoxLayout(jp3,BoxLayout.PAGE_AXIS));
        jp3.add(new JLabel("Project:"));
        JLabel cLabel=new JLabel("Comment:");
        jp3.add(cLabel);
        jp2.add(jp3,BorderLayout.NORTH);

        JPanel jp4=new JPanel(new BorderLayout());
        jp4.setBorder(BorderFactory.createEmptyBorder(5,5,5,5));
        JTextField jtf=new JTextField(settings.get(Setting.PROJECT));
        jp4.add(jtf,BorderLayout.NORTH);
        JTextArea jta=new JTextArea(settings.get(Setting.COMMENT));
        jta.setLineWrap(true);
        jta.setRows(5);
        jp4.add(jta,BorderLayout.CENTER);
        jp.add(jp4,BorderLayout.CENTER);

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
        JButton apply=new JButton("Submit");
        AskOnLogoutGUI self=this;
        apply.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                HashMap<String,String> settings=new HashMap<>(self.settings);
                settings.put(Setting.PROJECT,jtf.getText());
                settings.put(Setting.COMMENT,jta.getText());
                gui.stop(settings);
                dispose();
            }
        });
        jpx.add(apply);
        jp.add(jpx,BorderLayout.SOUTH);
        this.add(jp);
        jpx.revalidate();
    }
}
