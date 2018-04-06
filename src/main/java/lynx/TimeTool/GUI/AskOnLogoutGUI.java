package lynx.TimeTool.GUI;

import lynx.TimeTool.Settings;

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
        jp.add(jp2,BorderLayout.NORTH);

        JPanel jp3=new JPanel(new GridLayout(1,2));
        jp3.add(new JLabel("Project:"));
        JTextField jtf=new JTextField(settings.get(Settings.PROJECT));
        jp3.add(jtf);
        jp2.add(jp3,BorderLayout.NORTH);

        JPanel jp4=new JPanel(new GridLayout(1,2));
        JLabel cLabel=new JLabel("Comment:");
        cLabel.setVerticalAlignment(SwingConstants.TOP);
        jp4.add(cLabel);
        JTextArea jta=new JTextArea(settings.get(Settings.COMMENT));
        jta.setRows(5);
        jp4.add(jta);
        jp2.add(jp4,BorderLayout.CENTER);


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
                settings.put(Settings.PROJECT,jtf.getText());
                settings.put(Settings.COMMENT,jta.getText());
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
