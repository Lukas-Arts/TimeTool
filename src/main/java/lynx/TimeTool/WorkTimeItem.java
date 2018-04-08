package lynx.TimeTool;

import lynx.TimeTool.Util.Util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;

public class WorkTimeItem {
    private long start_ms=0,end_ms=0;
    private String project="",comment="";
    public WorkTimeItem(long start_ms) {
        this.start_ms = start_ms;
    }
    public WorkTimeItem(long start_ms, long end_ms) {
        this(start_ms);
        this.end_ms = end_ms;
    }
    public WorkTimeItem(long start_ms, long end_ms, String project, String comment) {
        this(start_ms,end_ms);
        this.project = project;
        this.comment = comment;
    }
    private static String getDate(ZonedDateTime zdt){
        return zdt.getYear()+"-"+ Util.getWithLeadingZero(zdt.getMonthValue())+"-"+Util.getWithLeadingZero(zdt.getDayOfMonth());
    }
    private static String getTime(ZonedDateTime zdt){
        return Util.getWithLeadingZero(zdt.getHour())+":"+ Util.getWithLeadingZero(zdt.getMinute())+
                ":"+Util.getWithLeadingZero(zdt.getSecond());
    }
    private static String getTimeWithMillis(ZonedDateTime zdt){
        return Util.getWithLeadingZero(zdt.getHour())+":"+ Util.getWithLeadingZero(zdt.getMinute())+
                ":"+Util.getWithLeadingZero(zdt.getSecond())+"."+Util.getWithLeadingZero(zdt.getNano()/1000000,3);
    }
    public long getStart() {
        return start_ms;
    }
    public void setStart(long start_ms) {
        this.start_ms = start_ms;
    }
    public String getStartDate(){
        ZonedDateTime zdt=ZonedDateTime.ofInstant(Instant.ofEpochMilli(start_ms),ZoneId.systemDefault());
        return getDate(zdt);
    }
    public String getStartTime(){
        ZonedDateTime zdt=ZonedDateTime.ofInstant(Instant.ofEpochMilli(start_ms),ZoneId.systemDefault());
        return getTime(zdt);
    }
    public String getStartDateTime(){
        ZonedDateTime zdt=ZonedDateTime.ofInstant(Instant.ofEpochMilli(start_ms),ZoneId.systemDefault());
        return getDate(zdt)+" "+getTime(zdt);
    }
    public String getStartDateTimeWithMillis(){
        ZonedDateTime zdt=ZonedDateTime.ofInstant(Instant.ofEpochMilli(start_ms),ZoneId.systemDefault());
        return getDate(zdt)+" "+getTimeWithMillis(zdt);
    }
    public long getEnd() {
        return end_ms;
    }
    public void setEnd(long end_ms) {
        this.end_ms = end_ms;
    }
    public String getEndDate(){
        ZonedDateTime zdt=ZonedDateTime.ofInstant(Instant.ofEpochMilli(end_ms),ZoneId.systemDefault());
        return getDate(zdt);
    }
    public String getEndTime(){
        ZonedDateTime zdt=ZonedDateTime.ofInstant(Instant.ofEpochMilli(end_ms),ZoneId.systemDefault());
        return getTime(zdt);
    }
    public String getEndDateTime(){
        ZonedDateTime zdt=ZonedDateTime.ofInstant(Instant.ofEpochMilli(end_ms),ZoneId.systemDefault());
        return getDate(zdt)+" "+getTime(zdt);
    }
    public String getEndDateTimeWithMillis(){
        ZonedDateTime zdt=ZonedDateTime.ofInstant(Instant.ofEpochMilli(end_ms),ZoneId.systemDefault());
        return getDate(zdt)+" "+getTimeWithMillis(zdt);
    }
    public String getProject() {
        return project;
    }
    public void setProject(String project) {
        this.project = project;
    }
    public String getComment() {
        return comment;
    }
    public void setComment(String comment) {
        this.comment = comment;
    }
    public boolean isFinished(){
        return end_ms!=0;
    }

    /**
     * @return  true if the item started and ended on the same day.
     *          if the item isnt finished yet the result will be true, too.
     */
    public boolean isSingleDay(){
        if(!isFinished()){
            return true;
        }
        ZonedDateTime zdt2=ZonedDateTime.ofInstant(Instant.ofEpochMilli(start_ms),ZoneId.systemDefault());
        ZonedDateTime zdt3=ZonedDateTime.ofInstant(Instant.ofEpochMilli(end_ms),ZoneId.systemDefault());
        return zdt2.getDayOfMonth()==zdt3.getDayOfMonth();
    }
    public long getDuration(){
        if(!isFinished()){
            return 0;
        }
        ZonedDateTime zdt2=ZonedDateTime.ofInstant(Instant.ofEpochMilli(start_ms),ZoneId.systemDefault());
        ZonedDateTime zdt3=ZonedDateTime.ofInstant(Instant.ofEpochMilli(end_ms),ZoneId.systemDefault());
        return ChronoUnit.MILLIS.between(zdt2,zdt3);
    }
    public static String getLongDuration(long millis){
        int s=(int)(millis/1000);
        int m=((int)(s/60));
        int h=(int)(m/60);
        return h+":"+Util.getWithLeadingZero(m%60)+":"+Util.getWithLeadingZero(s%60);
    }
    public static String getShortDuration(long millis){
        int s=(int)(millis/1000);
        int m=((int)(s/60));
        int h=(int)(m/60);
        return Util.getWithLeadingZero(h%24)+":"+Util.getWithLeadingZero(m%60)+":"+Util.getWithLeadingZero(s%60);
    }
    public ArrayList<WorkTimeItem> splitIntoDays(){
        ArrayList<WorkTimeItem> dailyItems=new ArrayList<>();
        if(!isFinished())
            return dailyItems;
        ZonedDateTime zdt2=ZonedDateTime.ofInstant(Instant.ofEpochMilli(start_ms),ZoneId.systemDefault());
        ZonedDateTime zdt3=ZonedDateTime.ofInstant(Instant.ofEpochMilli(end_ms),ZoneId.systemDefault());
        if(isSingleDay()){
            dailyItems.add(this);
            return dailyItems;
        }else{
            ZonedDateTime last=ZonedDateTime.from(zdt2);
            ZonedDateTime next;
            do{
                next=last.minusHours(last.getHour()).minusMinutes(last.getMinute()).minusSeconds(last.getSecond())
                        .minusNanos(last.getNano()).plusDays(1);
                if(next.isBefore(zdt3)){
                    dailyItems.add(new WorkTimeItem(last.toEpochSecond()*1000,next.minusSeconds(1).toEpochSecond()*1000,project,comment));
                }else{
                    dailyItems.add(new WorkTimeItem(last.toEpochSecond()*1000,zdt3.toEpochSecond()*1000));
                }
                last=next;
            }while (next.isBefore(zdt3));
        }
        return dailyItems;
    }
    public static WorkTimeItem parse(String s) throws ParseException {
        SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
        String s2[]=s.split(";");
        switch (s2.length){
            case 1:{
                return new WorkTimeItem(sdf.parse(s2[0]).getTime());
            }
            case 2:{
                return new WorkTimeItem(sdf.parse(s2[0]).getTime(),sdf.parse(s2[1]).getTime());
            }
            case 3:{
                return new WorkTimeItem(sdf.parse(s2[0]).getTime(),sdf.parse(s2[1]).getTime(),s2[2],"");
            }
            case 4:{
                return new WorkTimeItem(sdf.parse(s2[0]).getTime(),sdf.parse(s2[1]).getTime(),s2[2],s2[3]);
            }
            default:{
                throw new ParseException("Failed to Parse WorkTimeItem: "+s,0);
            }
        }
    }
    public String toString(){
        return getStartDateTimeWithMillis()+";"+getEndDateTimeWithMillis()+";"+getProject()+";"+getComment();
    }
    public String toString(String project,String comment){
        return getStartDateTimeWithMillis()+";"+getEndDateTimeWithMillis()+";"+project+";"+comment;
    }
}