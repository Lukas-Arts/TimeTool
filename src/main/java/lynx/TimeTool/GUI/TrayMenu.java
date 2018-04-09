package lynx.TimeTool.GUI;

import javax.swing.*;
import java.awt.event.ActionEvent;

public class TrayMenu extends JPopupMenu {
    public TrayMenu(GUI gui){
        add(new JMenuItem(new MyAbstractAction("Show"){
            @Override
            public void actionPerformed(ActionEvent e) {
                gui.setVisible(true);
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
