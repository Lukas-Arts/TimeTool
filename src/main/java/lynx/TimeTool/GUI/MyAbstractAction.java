package lynx.TimeTool.GUI;

import javax.swing.*;
import java.util.HashMap;

public abstract class MyAbstractAction extends AbstractAction {
    public MyAbstractAction(String name){
        this.putValue(Action.NAME,name);
    }
    public MyAbstractAction(HashMap<String,Object> settings){
        for(String key:settings.keySet())
            this.putValue(key,settings.get(key));
    }
}
