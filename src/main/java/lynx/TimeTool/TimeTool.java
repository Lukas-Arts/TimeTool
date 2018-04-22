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
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.*;

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
        List<String> lines=readFile(settings.get(Setting.LOCATION));
        if((lines.size()>0&& lines.get(lines.size()-1).matches(tsRegEx+";;.*"))){
            System.err.println("ERROR: Last Entry not Closed! : "+lines.size()+" "+lines.get(lines.size()-1));
        }else{
            try{
                BufferedWriter bw=new BufferedWriter(
                        new FileWriter(settings.getOrDefault(Setting.LOCATION,"time.csv"),true));
                start=System.currentTimeMillis();
                Timestamp ts=new Timestamp(start);
                bw.write(ts.toString()+";;"+settings.get(Setting.PROJECT)+";"+settings.get(Setting.COMMENT));
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
        List<String> lines=readFile(settings.get(Setting.LOCATION));
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
            if(withoutTS.equalsIgnoreCase(";;"+this.settings.get(Setting.PROJECT)+";"+this.settings.get(Setting.COMMENT))||
                    withoutTS.equalsIgnoreCase(";;;")){
                replacement=lastTS+";"+ts.toString()+";"+settings.get(Setting.PROJECT)+";"+settings.get(Setting.COMMENT);
            }else {
                replacement=s.replaceFirst(";;",";"+ts.toString()+";");
            }

            lines.set(lines.size()-1,replacement);
            try {
                Files.write(Paths.get(settings.get(Setting.LOCATION)), lines, StandardCharsets.UTF_8);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return stop;
    }
    private void list(){
        int year=Integer.parseInt(settings.getOrDefault(Setting.YEAR,"-1"));
        int month=Integer.parseInt(settings.getOrDefault(Setting.MONTH,"-1"));
        String project=settings.get(Setting.PROJECT);
        System.out.println(project);
        ZonedDateTime zdt=ZonedDateTime.now();
        if(year<0){
            year=zdt.getYear();
        }
        if(month<0){
            month=zdt.getMonthValue();
        }
        String d=settings.getOrDefault(Setting.DIAGRAM,"");
        switch (d){
            case "bar":{
                barDiagram(year,month,project);
                break;
            }
            default:{
                list(year,month,project);
            }
        }
    }
    private void barDiagram(int year, int month,String project){
        if("".equalsIgnoreCase(project)||project==null){
            System.out.println("WorkTime Bar-Diagram for "+year+"-"+Util.getWithLeadingZero(month));
        }else System.out.println("WorkTime Bar-Diagram for "+year+"-"+Util.getWithLeadingZero(month)+", Project: "+project);
        System.out.println();
        ArrayList<WorkTimeItem> items=readWorkTimeFile(settings.get(Setting.LOCATION).replace(".csv",
                "_"+year+"_"+Util.getWithLeadingZero(month)+".csv"),project);
        HashMap<String,Long> dayWorkMap=new HashMap<>();
        for(WorkTimeItem item:items){
            String d=item.getStartDate();
            dayWorkMap.put(d,dayWorkMap.getOrDefault(d, 0L)+item.getDuration());
        }
        long max=0;
        for(long l:dayWorkMap.values()){
            if(max<l){
                max=l;
            }
        }
        char[][] diagram=new char[10][dayWorkMap.size()*4];
        for(int i=0;i<diagram.length;i++) {
            for (int j = 0; j < diagram[i].length; j++) {
                diagram[i][j]=' ';
            }
        }
        ArrayList<String> keys=new ArrayList<>(dayWorkMap.keySet());
        Collections.sort(keys);
        for(int j=0;j<keys.size();j++){
            String d=keys.get(j);
            long w=dayWorkMap.get(d);
            if(w>0){
                double i=((w/(double)max)*10)-1;
                for(int i2=diagram.length-1;i2>=diagram.length-(int)i-1;i2--){
                    diagram[i2][j*4+1]='█';
                    diagram[i2][j*4+2]='█';
                }
                double i2=diagram.length-i-1;

                double diff=(10-i2)-(9-(int)i2);
                //System.out.println(diff+" "+i+" "+i2+" "+(int)i2);
                if(diff!=0){
                    if(diff<0.25){
                        diagram[(int)i2][j*4+1]='_';
                        diagram[(int)i2][j*4+2]='_';
                    }else if(diff<0.75){
                        diagram[(int)i2][j*4+1]='▄';
                        diagram[(int)i2][j*4+2]='▄';
                    }
                }
            }
        }
        for(int i=0;i<diagram.length;i++){
            if(i==0){
                System.out.print(Util.getWithLeadingZero((int)(max/1000.0/60.0/60.0))+"h|");
            }else {
                System.out.print("   |");
            }
            for(int j=0;j<diagram[i].length;j++){
                System.out.print(diagram[i][j]);
            }
            System.out.println();
        }
        System.out.println("---+-----------------------------------------------------------");
        System.out.print("   |");
        for (int i=0;i<keys.size();i++){
            String day=keys.get(i).split("-")[2];
            System.out.print("|"+day+"|");
        }
    }
    private void list(int year, int month,String project){
        List<WorkTimeItem> items=readWorkTimeFile(settings.get(Setting.LOCATION).replace(".csv","_"+year+"_"+
                Util.getWithLeadingZero(month)+".csv"),project);
        System.out.println("    Date    |   Start  |    End   |  Duration  |    Project    |         Comment         ");
        System.out.println("------------+----------+----------+------------+---------------+-------------------------");
        SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
        long sum=0;
        for(WorkTimeItem item:items){
            long d=item.getDuration();
            sum+=d;
            String date=item.getStartDate();
            String start=item.getStartTime();
            String end=item.getEndTime();
            String duration=Util.getBlankedString(WorkTimeItem.getShortDuration(d),10,Util.ADD_UNEVEN_AT_START);
            String p=Util.getBlankedString(item.getProject(),13);
            String comment=Util.getBlankedString(item.getComment(),23);
            System.out.println(" "+date+" | "+start+" | "+end+" | "+duration+" | "+p+" | "+comment+" ");
        }
        if(sum>0){
            String sum2=WorkTimeItem.getLongDuration(sum);
            System.out.println("------------+----------+----------+------------+---------------+-------------------------");
            System.out.println("            |          |   SUM=   |"+Util.getBlankedString(sum2,12,
                    Util.ADD_UNEVEN_AT_START)+"|               |                         ");
        }
    }
    public void monthly(){
        List<WorkTimeItem> items=readWorkTimeFile(settings.get(Setting.LOCATION));
        int lastMonth=-1;
        int lastYear=-1;
        String location=settings.get(Setting.LOCATION).replace(".csv","");
        BufferedWriter bw=null;
        for(WorkTimeItem item:items){
            String project=item.getProject();
            String comment=item.getComment();
            for(WorkTimeItem item2:item.splitIntoDays()){
                ZonedDateTime zdt = ZonedDateTime.ofInstant(Instant.ofEpochMilli(item2.getStart()),ZoneId.systemDefault());
                int month=zdt.getMonthValue();
                int year=zdt.getYear();
                try {
                    if(lastMonth!=month||lastYear!=year){
                        lastMonth=month;
                        lastYear=year;
                        if(bw!=null)
                            bw.close();
                        bw=new BufferedWriter(
                                new FileWriter(location+"_"+year+"_"+(month<10?"0"+month:month)+".csv"));
                    }
                    if(bw!=null){
                            bw.write(item2.toString(project,comment));
                        bw.newLine();
                    }
                } catch (IOException e) {
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
    public static List<String> readFile(String filePath){
        try {
            return Files.readAllLines(Paths.get(filePath), StandardCharsets.UTF_8);
        } catch (IOException e) {
            System.err.println("Failed to Read File: "+filePath);
            e.printStackTrace();
        }
        return new ArrayList<String>();
    }
    public static ArrayList<WorkTimeItem> readWorkTimeFile(String filePath){
        return readWorkTimeFile(filePath,"");
    }
    public static ArrayList<WorkTimeItem> readWorkTimeFile(String filePath,String project){
        ArrayList<String> projects=new ArrayList<>();
        projects.add(project);
        return readWorkTimeFile(filePath,projects);
    }
    public static ArrayList<WorkTimeItem> readWorkTimeFile(String filePath,List<String> projects){
        ArrayList<WorkTimeItem> items=new ArrayList<>();
        try {
            List<String> lines=Files.readAllLines(Paths.get(filePath), StandardCharsets.UTF_8);
            for(String s:lines){
                WorkTimeItem wti=WorkTimeItem.parse(s);
                if(projects.contains(wti.getProject())||(projects.size()==1&&projects.get(0).equalsIgnoreCase(""))){
                    items.add(wti);
                }
            }
        } catch (IOException | ParseException e) {
            System.err.println("Failed to Read File: "+filePath);
            e.printStackTrace();
        }
        return items;
    }
    public static Set<String> getProjectsFromFile(String filePath){
        ArrayList<WorkTimeItem> items=readWorkTimeFile(filePath);
        HashSet<String> projects=new HashSet<>();
        for(WorkTimeItem item:items){
            projects.add(item.getProject());
        }
        return projects;
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
                        case "-d":
                        case "-D":
                        case "-diagram":
                        case "-Diagram":{
                            settings.put(Setting.DIAGRAM,args[i]);
                            break;
                        }
                        case "-p":
                        case "-P":
                        case "-project":
                        case "-Project":{
                            settings.put(Setting.PROJECT,args[i]);
                            break;
                        }
                        case "-c":
                        case "-C":
                        case "-comment":
                        case "-Comment":{
                            settings.put(Setting.COMMENT,args[i]);
                            break;
                        }
                        case "-l":
                        case "-L":
                        case "-location":
                        case "-Location":{
                            settings.put(Setting.LOCATION,args[i]);
                            break;
                        }
                        case "-m":
                        case "-M":
                        case "-month":
                        case "-Month":{
                            settings.put(Setting.MONTH,args[i]);
                            break;
                        }
                        case "-y":
                        case "-Y":
                        case "-year":
                        case "-Year":{
                            settings.put(Setting.YEAR,args[i]);
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
            settings.put(Setting.PROJECT,p.getProperty(Setting.PROJECT));
            settings.put(Setting.COMMENT,p.getProperty(Setting.COMMENT));
            settings.put(Setting.LOCATION,p.getProperty(Setting.LOCATION,"./time.csv"));

            settings.put(Setting.ASK_ON_LOGOUT,p.getProperty(Setting.ASK_ON_LOGOUT,"false"));
            settings.put(Setting.ACTIVE_COLOR,p.getProperty(Setting.ACTIVE_COLOR));
            settings.put(Setting.INACTIVE_COLOR,p.getProperty(Setting.INACTIVE_COLOR));
            settings.put(Setting.TEXT_COLOR,p.getProperty(Setting.TEXT_COLOR));

            settings.put(Setting.ALWAYS_ON_TOP,p.getProperty(Setting.ALWAYS_ON_TOP,"true"));
            settings.put(Setting.GLOBAL_LOGOUT_HOOK,p.getProperty(Setting.GLOBAL_LOGOUT_HOOK));
            settings.put(Setting.GLOBAL_LOGIN_HOOK,p.getProperty(Setting.GLOBAL_LOGIN_HOOK));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return settings;
    }
    public void infoHeader(){
        System.out.println("");
        System.out.println("\t\t\t+---------------------------+");
        System.out.println("\t\t\t|                           |");
        System.out.println("\t\t\t|"+Util.getBlankedString("TimeTool "+ Setting.VERSION+" © by 1ynx",28)+"|");
        System.out.println("\t\t\t|                           |");
        System.out.println("\t\t\t+---------------------------+");
        System.out.println("");
    }
    public void info(){
        infoHeader();
        System.out.println("This is a simple Tool for logging working hours. Just a simple CLI and a small");
        System.out.println("GUI. It utilizes simple .csv-Files to store and manage all Data and the ");
        System.out.println("./time.props-File for all persistent Setting.");
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
