package lynx.TimeTool;

import java.awt.*;

public class Setting {
    public static final String VERSION="v0.2";
    public static final String PROJECT="Project";
    public static final String COMMENT="Comment";
    public static final String LOCATION="Location";
    public static final String ASK_ON_LOGOUT ="Ask_on_Logout";
    public static final String ACTIVE_COLOR="Color_Active";
    public static final String INACTIVE_COLOR="Color_Inactive";
    public static final String ALWAYS_ON_TOP="Always_on_Top";
    public static final String GLOBAL_LOGOUT_HOOK="Global_Logout-Shortcut";
    public static final String GLOBAL_LOGIN_HOOK="Global_Login-Shortcut";
    public static final String MONTH="Month";
    public static final String YEAR="Year";
    public static final String DIAGRAM="Diagram";
    public static final String TEXT_COLOR="Color_Text";

    public static Color getColorFromString(String s){
        Color color=Color.black;
        if(s!=null){
            String s1[]=s.split("\\|");
            if(!(s1.length==3||s1.length==4)){
                System.err.println("No Valid Color: "+s+"!");
            }else{
                if(s1.length==3){
                    color=new Color(getVal(s1[0]),getVal(s1[1]),getVal(s1[2]));
                }else {
                    color=new Color(getVal(s1[0]),getVal(s1[1]),getVal(s1[2]),getVal(s1[3]));
                }
            }
        }
        return color;
    }
    private static int getVal(String s){
        int i=-1;
        String s1[]=s.split("=");
        if(s1.length!=2){
            System.err.println("No valid Color-Value: "+s+"!");
        }else{
            i=Integer.parseInt(s1[1]);
        }
        return i;
    }
}
