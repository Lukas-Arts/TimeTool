package lynx.TimeTool.Util;

public class Util {
    public static final boolean ADD_UNEVEN_AT_END=true;
    public static final boolean ADD_UNEVEN_AT_START=false;
    public static String getTimeDiff(long diff){
        int s= (int) ((diff/1000)%60);
        int m= (int) ((diff/1000/60)%60);
        int h= (int) ((diff/1000/60/60)%24);
        return getWithLeadingZero(h)+":"+getWithLeadingZero(m)+":"+getWithLeadingZero(s);
    }
    public static String getWithLeadingZero(Number i,int digits){
        String s=""+i;
        int max=1;
        for(int j=0;j<digits-1;j++){
            max=max*10;
            if(i.intValue()<max){
                s="0"+s;
            }
        }
        return s;
    }
    public static String getWithLeadingZero(Number i){
        return getWithLeadingZero(i,2);
    }
    public static String getBlankedString(String s,int maxSize){
        return getBlankedString(s,maxSize,ADD_UNEVEN_AT_END);
    }
    public static String getBlankedString(String s,int maxSize,boolean addUnevenWhen){
        if(s.length()<maxSize){
            int d=(maxSize-s.length())/2;
            double d2=(maxSize-s.length())/2.0;
            if(d!=d2){
                if(addUnevenWhen==ADD_UNEVEN_AT_END) {
                    s = s + " ";
                }else {
                    s=" "+s;
                }
            }
            StringBuilder sBuilder = new StringBuilder(s);
            for(int i = 0; i<d; i++){
                sBuilder = new StringBuilder(" " + sBuilder + " ");
            }
            s = sBuilder.toString();
        }else if(s.length()>maxSize){
            s=s.substring(0,maxSize-3)+"...";
        }
        return s;
    }
}
