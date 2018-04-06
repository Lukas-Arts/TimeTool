package lynx.TimeTool.GUI;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.util.HashMap;

public class RightClickMenu extends JPopupMenu {
    public RightClickMenu(HashMap<String,String> settings){
        add(new JMenuItem(new MyAbstractAction("Settings"){

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
