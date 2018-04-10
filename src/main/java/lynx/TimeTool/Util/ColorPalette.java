package lynx.TimeTool.Util;

import java.awt.*;

public class ColorPalette {
    private static Color colors[][]={
            {new Color(0,48,88),new Color(5,82,138),new Color(0,159,215),new Color(70,216,213),new Color(242,207,61)},
            {new Color(143,48,46),new Color(48,51,60),new Color(128,131,120),new Color(228,205,164),new Color(200,122,100)},
            {new Color(195,71,7),new Color(215,204,60),new Color(253,235,125),new Color(157,224,171),new Color(2,173,165)},
            {new Color(246,246,146),new Color(51,54,69),new Color(120,198,211),new Color(220,238,226),new Color(234,45,73)},
    };
    public static Color getColor(int i){
        return colors[(i/5)%4][i%5];
    }
}
