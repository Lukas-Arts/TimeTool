package lynx.TimeTool.Util;

public class Util {
    public static String getTimeDiff(long diff){
        int s= (int) ((diff/  1000)%60);
        int m= (int) ((diff/ 60000)%60);
        int h= (int) ((diff/360000)%24);
        return getWithLeadingZero(h)+":"+getWithLeadingZero(m)+":"+getWithLeadingZero(s);
    }
    public static String getWithLeadingZero(int i){
        return (i<10?"0"+i:i+"");
    }
    public static String getWithLeadingZero(long i){
        return getWithLeadingZero((int)i);
    }
    public static String getBlankedString(String s,int maxSize){
        if(s.length()<maxSize){
            int d=(maxSize-s.length())/2;
            double d2=(maxSize-s.length())/2.0;
            if(d!=d2){
                s=s+" ";
            }
            for(int i=0;i<d;i++){
                s=" "+s+" ";
            }
        }
        if(s.length()>maxSize){
            s=s.substring(0,maxSize-3)+"...";
        }
        return s;
    }
}
