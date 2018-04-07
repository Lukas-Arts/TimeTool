package lynx.TimeTool;

import lynx.TimeTool.GUI.GUI;
import lynx.TimeTool.Util.Util;

import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;

public class TimeTool {
    public static final String START="start";
    public static final String STOP="stop";
    public static final String GUI="gui";
    public static final String HELP="help";
    public static final String MONTHLY="monthly";
    public static final String LIST="list";
    public static final String VERSION="version";
    private HashMap<String,String> settings=new HashMap<>();
    private static String tsRegEx="(\\d){4}-(\\d){2}-(\\d){2} (\\d){2}:(\\d){2}:(\\d){2}\\.(\\d){1,3}";
    public TimeTool(String args[]){
        this(getSettings(args),(args.length>0?args[0]:null));
    }
    public TimeTool(HashMap<String,String> settings,String action){
        this.settings=settings;
        switch (action){
            case "-"+START:
            case START:{
                start();
                break;
            }
            case "-"+STOP:
            case STOP:{
                stop(settings);
                break;
            }
            case "-"+GUI:
            case GUI:{
                new GUI(settings);
                break;
            }
            case "-"+MONTHLY:
            case MONTHLY:{
                monthly();
                break;
            }
            case "-"+LIST:
            case LIST:{
                list();
                break;
            }
            case "-"+VERSION:
            case VERSION:{
                infoHeader();
                break;
            }
            case "-"+HELP:
            case HELP:
            default:{
                info();
                break;
            }
        }
    }
    public TimeTool(HashMap<String,String> settings){
        this.settings=settings;
    }
    public void setSettings(HashMap<String,String> settings){
        this.settings=settings;
    }
    public HashMap<String,String> getSettings(){
        return this.settings;
    }
    public long start(){
        long start=-1;
        List<String> lines=readFile(settings.get(Settings.LOCATION));
        if((lines.size()>0&& lines.get(lines.size()-1).matches(tsRegEx+";;.*"))){
            System.err.println("ERROR: Last Entry not Closed! : "+lines.size()+" "+lines.get(lines.size()-1));
        }else{
            try{
                BufferedWriter bw=new BufferedWriter(
                        new FileWriter(settings.getOrDefault(Settings.LOCATION,"time.csv"),true));
                start=System.currentTimeMillis();
                Timestamp ts=new Timestamp(start);
                bw.write(ts.toString()+";;"+settings.get(Settings.PROJECT)+";"+settings.get(Settings.COMMENT));
                bw.flush();
                bw.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return start;
    }
    public long stop(HashMap<String,String> settings){
        long stop=-1;
        List<String> lines=readFile(settings.get(Settings.LOCATION));
        if((lines.size()>0&&lines.get(lines.size()-1).equalsIgnoreCase(""))){
            System.err.println("ERROR: Last Entry already Closed!");
        } else if(lines.size()==0){
            System.err.println("ERROR: Can't close! The Time Table doesn't exist!");
        } else {
            stop=System.currentTimeMillis();
            Timestamp ts=new Timestamp(stop);
            String s=lines.get(lines.size()-1);
            String lastTS=s.split(";")[0];
            String withoutTS=s.replace(lastTS,"");
            String replacement;
            //don't overwrite existing comments, except they are the default ones
            if(withoutTS.equalsIgnoreCase(";;"+this.settings.get(Settings.PROJECT)+";"+this.settings.get(Settings.COMMENT))||
                    withoutTS.equalsIgnoreCase(";;;")){
                replacement=lastTS+";"+ts.toString()+";"+settings.get(Settings.PROJECT)+";"+settings.get(Settings.COMMENT);
            }else {
                replacement=s.replaceFirst(";;",";"+ts.toString()+";");
            }

            lines.set(lines.size()-1,replacement);
            try {
                Files.write(Paths.get(settings.get(Settings.LOCATION)), lines, StandardCharsets.UTF_8);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return stop;
    }
    private void list(){
        int year=Integer.parseInt(settings.getOrDefault(Settings.YEAR,"-1"));
        int month=Integer.parseInt(settings.getOrDefault(Settings.MONTH,"-1"));
        String project=settings.get(Settings.PROJECT);
        System.out.println(project);
        ZonedDateTime zdt=ZonedDateTime.now();
        if(year<0){
            year=zdt.getYear();
        }
        if(month<0){
            month=zdt.getMonthValue();
        }
        List<String> lines=readFile(settings.get(Settings.LOCATION).replace(".csv","_"+year+"_"+
                Util.getWithLeadingZero(month)+".csv"));
        System.out.println("    Date    |   Start  |    End   | Duration  |    Project    |         Comment         ");
        System.out.println("------------+----------+----------+-----------+---------------+-------------------------");
        SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
        long sum=0;
        for(String l:lines){
            String s[]=l.split(";");
            try {
                if((project!=null&&!"".equalsIgnoreCase(project)&&s[2].equalsIgnoreCase(project))||(project==null||"".equalsIgnoreCase(project))){
                    ZonedDateTime zdt2=ZonedDateTime.ofInstant(Instant.ofEpochMilli(sdf.parse(s[0]).getTime()),
                            ZoneId.systemDefault());
                    ZonedDateTime zdt3=ZonedDateTime.ofInstant(Instant.ofEpochMilli(sdf.parse(s[1]).getTime()),
                            ZoneId.systemDefault());
                    String date=zdt2.getYear()+"-"+ Util.getWithLeadingZero(zdt2.getMonthValue())+"-"+
                            Util.getWithLeadingZero(zdt2.getDayOfMonth());
                    String start=Util.getWithLeadingZero(zdt2.getHour())+":"+ Util.getWithLeadingZero(zdt2.getMinute())+
                            ":"+Util.getWithLeadingZero(zdt2.getSecond());
                    String end=Util.getWithLeadingZero(zdt3.getHour())+":"+ Util.getWithLeadingZero(zdt3.getMinute())+
                            ":"+Util.getWithLeadingZero(zdt3.getSecond());
                    sum+=ChronoUnit.MILLIS.between(zdt2,zdt3);
                    int d=(int) ChronoUnit.DAYS.between(zdt2,zdt3);
                    int h=(int) ChronoUnit.HOURS.between(zdt2,zdt3);
                    int m=(int) ChronoUnit.MINUTES.between(zdt2,zdt3);
                    String duration=Util.getBlankedString(d+"d "+Util.getWithLeadingZero(h%24)+":"+
                            Util.getWithLeadingZero(m%60),9,Util.ADD_UNEVEN_AT_START);
                    String p="";
                    String comment="";
                    if(s.length>2)p=s[2];
                    if(s.length>3)comment=s[3];
                    System.out.println(" "+date+" | "+start+" | "+end+" | "+duration+" | "+
                            Util.getBlankedString(p,13)+" | "+Util.getBlankedString(comment,25)+" ");

                }
            } catch (ParseException | IndexOutOfBoundsException e) {
                //e.printStackTrace();
            }
        }
        if(sum>0){
            Duration sum2=Duration.ofMillis(sum);
            String sum3=sum2.toDays()+"d "+Util.getWithLeadingZero(sum2.toHours()%24)+":"+Util.getWithLeadingZero(sum2.toMinutes()%60);
            System.out.println("------------+----------+----------+-----------+---------------+-------------------------");
            System.out.println("            |          |   SUM=   |"+Util.getBlankedString(sum3,11,
                    Util.ADD_UNEVEN_AT_START)+"|               |                         ");
        }
    }
    private void monthly(){
        List<String> lines=readFile(settings.get(Settings.LOCATION));
        SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
        int lastMonth=-1;
        int lastYear=-1;
        String location=settings.get(Settings.LOCATION).replace(".csv","");
        BufferedWriter bw=null;
        for(String l:lines){
            String s[]=l.split(";");
            if(s.length>=2){
                String project="";
                String comment="";
                if(s.length>2)
                    project=s[2];
                if(s.length>3)
                    comment=s[3];
                try {
                    for(String s2:splitIntoDays(s[0],s[1],project,comment)){
                        Instant i=Instant.ofEpochMilli(sdf.parse(s2.split(";")[0]).getTime());
                        ZonedDateTime zdt = ZonedDateTime.ofInstant(i,ZoneId.systemDefault());
                        int month=zdt.getMonthValue();
                        int year=zdt.getYear();
                        if(lastMonth!=month||lastYear!=year){
                            lastMonth=month;
                            lastYear=year;
                            if(bw!=null)
                                bw.close();
                            bw=new BufferedWriter(
                                    new FileWriter(location+"_"+year+"_"+(month<10?"0"+month:month)+".csv"));
                        }
                        if(bw!=null){
                            bw.write(s2);
                            bw.newLine();
                        }
                    }
                } catch (ParseException | IOException e) {
                    e.printStackTrace();
                }
            }
        }
        if(bw!=null) {
            try {
                bw.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    private ArrayList<String> splitIntoDays(String d1,String d2,String project,String comment){
        ArrayList<String> dailyLogs=new ArrayList<>();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
        Instant i1= null;
        ZonedDateTime zdt2=null;
        ZonedDateTime zdt3=null;
        try {
            i1 = Instant.ofEpochMilli(sdf.parse(d1).getTime());
            zdt2=ZonedDateTime.ofInstant(Instant.ofEpochMilli(sdf.parse(d2).getTime()),ZoneId.systemDefault());
            zdt3=ZonedDateTime.ofInstant(Instant.ofEpochMilli(sdf.parse(d1).getTime()),ZoneId.systemDefault());
            zdt3=zdt3.minusHours(zdt3.getHour()).minusMinutes(zdt3.getMinute()).minusSeconds(zdt3.getSecond())
                    .minusNanos(zdt3.getNano());
            ArrayList<String> timestamps=new ArrayList<>();
            timestamps.add(new Timestamp(i1.toEpochMilli()).toString());
            do{
                zdt3=zdt3.plusDays(1);
                if(zdt3.isBefore(zdt2)){
                    timestamps.add(new Timestamp(zdt3.toEpochSecond()*1000).toString());
                }else{
                    timestamps.add(new Timestamp(zdt2.toEpochSecond()*1000).toString());
                }
            }while(zdt3.isBefore(zdt2));
            for(int i=1;i<timestamps.size();i++){
                dailyLogs.add(timestamps.get(i-1)+";"+timestamps.get(i)+";"+project+";"+comment);
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return dailyLogs;
    }
    private List<String> readFile(String filePath){
        try {
            return Files.readAllLines(Paths.get(filePath), StandardCharsets.UTF_8);
        } catch (IOException e) {
            System.err.println("Failed to Read File: "+filePath);
            System.err.println("Cause: "+e.getMessage());
        }
        return new ArrayList<String>();
    }
    private static HashMap<String, String> getSettings(String args[]){
        HashMap<String,String> settings=readSettings();
        settings.putAll(getSettingsFromArgs(args));
        return settings;
    }
    private static HashMap<String,String> getSettingsFromArgs(String args[]){
        HashMap<String,String> settings=new HashMap<>();
        if(args.length>1){
            String next=null;
            for(int i=1;i<args.length;i++){
                if(next==null){
                    next=args[i];
                } else {
                    switch (next){
                        case "-p":
                        case "-P":
                        case "-project":
                        case "-Project":{
                            settings.put(Settings.PROJECT,args[i]);
                            break;
                        }
                        case "-c":
                        case "-C":
                        case "-comment":
                        case "-Comment":{
                            settings.put(Settings.COMMENT,args[i]);
                            break;
                        }
                        case "-l":
                        case "-L":
                        case "-location":
                        case "-Location":{
                            settings.put(Settings.LOCATION,args[i]);
                            break;
                        }
                        case "-m":
                        case "-M":
                        case "-month":
                        case "-Month":{
                            settings.put(Settings.MONTH,args[i]);
                            break;
                        }
                        case "-y":
                        case "-Y":
                        case "-year":
                        case "-Year":{
                            settings.put(Settings.YEAR,args[i]);
                            break;
                        }
                    }
                    next=null;
                }
            }
        }
        return settings;
    }
    private static HashMap<String,String> readSettings(){
        HashMap<String,String> settings=new HashMap<>();
        try {
            Properties p=new Properties();
            p.load(new FileReader("./time.props"));
            settings.put(Settings.PROJECT,p.getProperty(Settings.PROJECT));
            settings.put(Settings.COMMENT,p.getProperty(Settings.COMMENT));
            settings.put(Settings.LOCATION,p.getProperty(Settings.LOCATION,"./time.csv"));

            settings.put(Settings.ASK_ON_LOGOUT,p.getProperty(Settings.ASK_ON_LOGOUT,"false"));
            settings.put(Settings.ACTIVE_COLOR,p.getProperty(Settings.ACTIVE_COLOR));
            settings.put(Settings.INACTIVE_COLOR,p.getProperty(Settings.INACTIVE_COLOR));

            settings.put(Settings.ALWAYS_ON_TOP,p.getProperty(Settings.ALWAYS_ON_TOP,"true"));
            settings.put(Settings.GLOBAL_LOGOUT_HOOK,p.getProperty(Settings.GLOBAL_LOGOUT_HOOK));
            settings.put(Settings.GLOBAL_LOGIN_HOOK,p.getProperty(Settings.GLOBAL_LOGIN_HOOK));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return settings;
    }
    public void infoHeader(){
        System.out.println("");
        System.out.println("\t\t\t+---------------------------+");
        System.out.println("\t\t\t|                           |");
        System.out.println("\t\t\t|"+Util.getBlankedString("TimeTool "+Settings.VERSION+" © by 1ynx",28)+"|");
        System.out.println("\t\t\t|                           |");
        System.out.println("\t\t\t+---------------------------+");
        System.out.println("");
    }
    public void info(){
        infoHeader();
        System.out.println("This is a simple Tool for logging working hours. Just a simple CLI and a small");
        System.out.println("GUI. It utilizes simple .csv-Files to store and manage all Data and the ");
        System.out.println("./time.props-File for all persistent Settings.");
        System.out.println("");
        System.out.println("Usage: TimeTool [ MAIN-PARAMETER ] [ SETTING-PARAMETER ]");
        System.out.println("");
        System.out.println("MAIN-PARAMETER: ");
        System.out.println("-start \t\t\t\tLog start");
        System.out.println("-stop \t\t\t\tLog stop");
        System.out.println("-gui \t\t\t\tStarts the GUI");
        System.out.println("-monthly \t\t\tSplits the .csv defined by -l into separate ");
        System.out.println("\t\t\t\tMonths (also splits working hours into separate");
        System.out.println("\t\t\t\tDays)");
        System.out.println("-list \t\t\t\tLists the current Month, or the Month defined by -m");
        System.out.println("\t\t\t\tand -y (requires -monthly to be called first)");
        System.out.println("-help \t\t\t\tShows this Help-Message");
        System.out.println("-version \t\t\t\tShows the current version");
        System.out.println("");
        System.out.println("SETTING-PARAMETER: ");
        System.out.println("-p/-P/-project/-Project \tPROJECTNAME");
        System.out.println("-c/-C/-comment/-Comment \tCOMMENT");
        System.out.println("-l/-L/-location/-Location \tLOCATION (the location for the Logging-.csv-File)");
        System.out.println("-m/-M/-month/-Month \t\tMONTH (only valid for -list)");
        System.out.println("-y/-Y/-year/-Year \t\tYEAR (only valid for -list)");
        System.out.println("");
    }
    public static void main(String args[]){
        new TimeTool(args);
    }

}
