package lynx.TimeTool.GUI;

import lynx.TimeTool.Setting;
import lynx.TimeTool.TimeTool;
import lynx.TimeTool.Util.Util;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.HashMap;

public class GUI extends JDialog{
    private TimeTool tt;
    private long start=System.currentTimeMillis();
    private JLabel jl=new JLabel();
    private boolean running=false;
    private HashMap<String,String> settings;
    private GlobalHotkeyListener logoutListener;
    private GlobalHotkeyListener loginListener;
    private GUI self=this;
    public GUI(HashMap<String,String> settings){
        this.setTitle("TimeTool");
        this.settings=settings;
        int width=100;
        int height=25;
        this.setSize(width,height);
        this.setUndecorated(true);
        this.add(jl);
        jl.setHorizontalAlignment(SwingConstants.CENTER);
        jl.setVerticalAlignment(SwingConstants.CENTER);
        jl.setOpaque(true);
        this.tt=new TimeTool(settings);

        jl.setBackground(Setting.getColorFromString(settings.get(Setting.INACTIVE_COLOR)));
        Toolkit t=Toolkit.getDefaultToolkit();
        this.setLocation(t.getScreenSize().width / 2 - width / 2, 0);
        addMouseListener(new MouseAdapter(){
            @Override
            public void mouseClicked(MouseEvent e){
                if(e.getClickCount()==2){
                    if(running){
                        if(Boolean.parseBoolean(settings.get(Setting.ASK_ON_LOGOUT))){
                            new AskOnLogoutGUI(settings,self);
                        } else {
                            stop(settings);
                        }
                    }else{
                        start();
                    }
                }
            }
            public void mousePressed(MouseEvent e){
                if(e.isPopupTrigger()){
                    System.out.println("Popup triggered");
                    RightClickMenu menu=new RightClickMenu(settings);
                    menu.show(e.getComponent(),e.getX(),e.getY());
                }
            }
            public void mouseReleased(MouseEvent e){
                if(e.isPopupTrigger()){
                    System.out.println("Popup triggered");
                    RightClickMenu menu=new RightClickMenu(settings);
                    menu.show(e.getComponent(),e.getX(),e.getY());
                }
            }
        });
        setUpLoginHook(settings.get(Setting.GLOBAL_LOGIN_HOOK));
        setUpLogoutHook(settings.get(Setting.GLOBAL_LOGOUT_HOOK));
        //this.setType(Type.POPUP);
        boolean alwaysOnTop=Boolean.parseBoolean(settings.get(Setting.ALWAYS_ON_TOP));
        System.out.println("AlwaysOnTop: "+alwaysOnTop);
        this.setAlwaysOnTop(alwaysOnTop);
        this.setVisible(true);
        createTray();
    }
    private void createTray(){
        //Check the SystemTray is supported
        if (!SystemTray.isSupported()) {
            System.out.println("SystemTray is not supported");
            return;
        }
        try {
            BufferedImage img= ImageIO.read(new File("./tray.png"));
            PopupMenu popup = new PopupMenu();
            MenuItem mi=new MenuItem("Show");
            mi.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    setVisible(true);
                }
            });
            popup.add(mi);
            TrayIcon trayIcon = new TrayIcon(img);
            trayIcon.setPopupMenu(popup);
            SystemTray tray = SystemTray.getSystemTray();
            tray.add(trayIcon);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (AWTException e) {
            e.printStackTrace();
        }
        /*new BufferedImage(24,24,BufferedImage.TYPE_INT_ARGB);
        Graphics2D g=img.createGraphics();
        //reset composite
        g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER));
        g.setColor(new Color(9,152,200));
        g.drawRect(0,0,23,23);*/
    }
    private GlobalHotkeyListener setUpHook(String s,ActionListener al){
        String s2[]=s.split("\\+");
        GlobalHotkeyListener listener=new GlobalHotkeyListener(s2);
        listener.addActionListener(al);
        return listener;
    }
    public void setUpLoginHook(String s){
        if(loginListener!=null)
            loginListener.stopListener();
        loginListener=setUpHook(s,new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                start();
            }
        });
    }
    public void setUpLogoutHook(String s){
        if(logoutListener!=null)
            logoutListener.stopListener();
        logoutListener=setUpHook(s,new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                stop(settings);
            }
        });
    }
    public void stop(HashMap<String,String> settings){
        long stop=tt.stop(settings);
        System.out.println("Leaving Work at "+new Timestamp(stop).toString());
        jl.setBackground(Setting.getColorFromString(tt.getSettings().get(Setting.INACTIVE_COLOR)));
        running=false;
        this.repaint();
        this.revalidate();
    }
    public void start(){
        start=tt.start();
        System.out.println("Going to Work at "+new Timestamp(start).toString());
        jl.setBackground(Setting.getColorFromString(tt.getSettings().get(Setting.ACTIVE_COLOR)));
        running=true;
        Thread th=new Thread(new Runnable() {
            @Override
            public void run() {
                while (running){
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        //ignore
                    }
                    long diff=System.currentTimeMillis()-start;
                    jl.setText(Util.getTimeDiff(diff));
                    repaint();
                    revalidate();
                }
            }
        });
        th.start();
        this.repaint();
        this.revalidate();
    }
}
