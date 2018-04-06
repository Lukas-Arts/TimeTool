package lynx.TimeTool.GUI;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by lukas on 17.04.15.
 */
public class GlobalHotkeyListener extends Thread{
    private int keyboard_id;
    private ArrayList<ActionListener> als=new ArrayList<>();
    private HashMap<String,Integer> keyCombination=new HashMap<>();
    private String combination="";
    private boolean running=true;
    public GlobalHotkeyListener(String[] keyCombination){
        //detect keyboard id
        try {
            String cmd[]={"xinput","list"};
            Process p=new ProcessBuilder(cmd).start();
            BufferedReader br= new BufferedReader(new InputStreamReader(p.getInputStream()));
            String s=null;
            while((s=br.readLine())!=null){
                s=s.replace("slave  keyboard","").replace("master keyboard","");
                if(s.contains("keyboard")&&(!s.contains("Virtual"))){
                    //System.out.println(s);
                    String s2[]=s.split("\t");
                    for(String s3:s2)if(s3.contains("id="))keyboard_id=Integer.parseInt(s3.replace("id=",""));
                }
            }
            br.close();
            p.destroy();
            //System.out.println(keyboard_id);
        } catch (IOException e) {
            e.printStackTrace();
        }
        //map keycodes for keyCombination
        try {
            String cmd[]={"xmodmap","-pk"};
            Process p=new ProcessBuilder(cmd).start();
            BufferedReader br= new BufferedReader(new InputStreamReader(p.getInputStream()));
            String s=null;
            while((s=br.readLine())!=null){
                for(String s2:keyCombination)
                    if(s.contains("("+s2+")")){
                        String s3[]=s.split("\t");
                        //System.out.println(Integer.parseInt(s3[0].replace(" ", "")));
                        if(this.keyCombination.get(s2)==null)
                        {
                            combination+=s2+"+";
                            this.keyCombination.put(s2,Integer.parseInt(s3[0].replace(" ", "")));
                        }
                        //for(String s4:s3)System.out.println(s4);
                    }
            }
            combination=combination.substring(0,combination.length()-1);
            br.close();
            p.destroy();
        } catch (IOException e) {
            e.printStackTrace();
        }
        //System.out.println(this.keyCombination.size());
        this.start();
    }
    public void stopListener(){
        running=false;
    }
    public void run()
    {
        try {
            String cmd[]={"xinput","test",keyboard_id+""};
            Process p=new ProcessBuilder(cmd).start();
            BufferedReader br= new BufferedReader(new InputStreamReader(p.getInputStream()));
            String s=null;
            int i=0;
            while((s=br.readLine())!=null&&running){
                String s2[]=s.split(" ");
                if(keyCombination.containsValue(Integer.parseInt(s2[s2.length-1])))
                {
                    if(s.contains("press"))i++;
                    else if(s.contains("release"))i--;
                }

                if(i==keyCombination.size()){
                    //System.out.println("All buttons pressed: "+combination);
                    for(ActionListener a:als)a.actionPerformed(new ActionEvent(this,0,combination+" pressed"));
                }
            }
            br.close();
            p.destroy();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void addActionListener(ActionListener a){
        this.als.add(a);
    }
}
