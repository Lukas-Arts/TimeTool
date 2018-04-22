package lynx.TimeTool.GUI;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.HashMap;

public class RightClickMenu extends JPopupMenu {
    public RightClickMenu(HashMap<String,String> settings){
        add(new JMenuItem(new MyAbstractAction("Diagram"){
            @Override
            public void actionPerformed(ActionEvent e) {
                boolean found=false;
                Window[] windows = Window.getWindows();
                if( windows != null ) { // don't rely on current implementation, which at least returns [0].
                    for (Window w : windows) {
                        //System.out.println("Found window "+w.getName()+" "+w.toString());
                        if(w instanceof DiagramGUI && w.isVisible() && w.isShowing()){
                            //if(((JDialog) w).getTitle().equalsIgnoreCase("Diagram")){
                                System.out.println("Found dialog "+w.getName()+" "+((JFrame) w).getTitle()+" active: "+w.isActive()+" enabled: "+w.isEnabled()+" showing: "+w.isShowing()+" valid: "+w.isValid()+" visible: "+w.isVisible());
                                w.setVisible(true);

                                found=true;
                            //}
                        }
                    }
                }
                if(!found){
                    new DiagramGUI(settings);
                }
            }
        }));
        add(new JMenuItem(new MyAbstractAction("Setting"){
            @Override
            public void actionPerformed(ActionEvent e) {
                new SettingsGUI(settings);
            }
        }));
        add(new JMenuItem(new MyAbstractAction("Exit"){
            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        }));
    }
}
