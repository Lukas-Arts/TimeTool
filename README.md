# TimeTool
This is a simple Tool for logging working hours. Just a simple CLI and a small GUI. It utilizes simple .csv-Files 
to store and manage all Data and the `./time.props`-File for all persistent Settings.

![alt text](https://github.com/Lukas-Arts/TimeTool/blob/master/Screenshot-v.0.3.png "Screenshot v.0.3")

## Installation

Download the `timetool-x.zip`-Archive (where x is the Version-Number e.g. '0.1') and extract it in some temp-Folder. Then execute the install.sh. This will install all Files to `~/TimeTool` and setup a Shortcut in `/usr/bin` so you can simply type 'timetool' instead of 'java -jar TimeTool.jar'.

## Usage

### General

`timetool [OPTION] [SETTING]`

#### Options: 

| Option |  Description  | Possible Parameter |
| -------------- | ---------- | -------------------------- |
| -start         | Log start  | -p/-c/-l                   |
| -stop          | Log stop   | -p/-c/-l                   |
| -gui           | Starts the GUI | -p/-c/-l                   |
| -monthly       | Splits the .csv defined by -l into separateMonths (also splits working hours into separate Days, if necessary) | -l                   |
| -list          | Lists the current Month, or the Month defined by -m and -y (requires corresponding '-monthly'-Files). You may also use -p to specify a Project | -p/-l/-m/-y/-d             |
| -help          | Shows this Help-Message |                            |
| -version       | Shows the current Version |                            |
        
#### Settings: 

| Setting | Description  |
| --------- | --------- |
| -p/-P/-project/-Project | PROJECTNAME (Optional, if not present Setting is taken from `./time.props`) |
| -c/-C/-comment/-Comment | COMMENT (Optional, if not present Setting is taken from `./time.props`) |
| -l/-L/-location/-Location | LOCATION (the location for the Logging-.csv-File) (Optional, if not present Setting is taken from `./time.props` or set to `./time.csv`) |
| -m/-M/-month/-Month | MONTH (only valid for -list) |
| -y/-Y/-year/-Year | YEAR (only valid for -list) |
| -d/-D/-diagram/-Diagram | DIAGRAMTYPE ('bar' only, so far, only valid for -list) |

### Console 

To use timetool from the Command Line simply enter

`timetool start`

when you start your work. After finishing your work enter

`timetool stop -p TimeTool -c 'added Readme'`

In this case the Worktime would be logged for the Project 'TimeTool' with 'added Readme' as an Comment

To turn the logging output into monthly enter

`timetool monthly`

This will create a .csv file for each Month, and also split the logged Worktime into daily parts.
After that you may access a simple overview of your current Worktime by entering

`timetool list`

```
       Date    |   Start  |    End   |  Duration  |    Project    |         Comment         
   ------------+----------+----------+------------+---------------+-------------------------
    2018-04-01 | 00:00:00 | 04:05:17 |  04:05:17  |   TimeTool    | initial working version 
    2018-04-01 | 21:03:35 | 23:59:59 |  02:56:24  |   TimeTool7   | added -monthly and -... 
    2018-04-02 | 00:00:00 | 04:27:32 |  04:27:32  |   TimeTool7   | added -monthly and -... 
    2018-04-06 | 22:50:34 | 23:59:59 |  01:09:25  |   TimeTool3   | pushed to git, added... 
    2018-04-07 | 00:00:00 | 05:33:04 |  05:33:04  |   TimeTool3   | pushed to git, added... 
    2018-04-08 | 00:45:53 | 01:36:35 |  00:50:42  |   TimeTool2   | added WorkTimeItem a... 
    2018-04-08 | 16:47:28 | 18:29:05 |  01:41:36  |   TimeTool4   | rewrite list&monthly... 
    2018-04-08 | 19:13:34 | 19:58:35 |  00:45:01  |   TimeTool5   |     added diagrams      
    2018-04-08 | 22:26:09 | 23:59:59 |  01:33:50  |   TimeTool6   |     added diagrams      
    2018-04-09 | 00:00:00 | 01:57:33 |  01:57:33  |   TimeTool6   |     added diagrams      
   ------------+----------+----------+------------+---------------+-------------------------
               |          |   SUM=   |  25:00:25  |               |                         
```

`timetool list -d bar`
   
```
  WorkTime Bar-Diagram for 2018-04
  
  07h| ██                     
     | ██                     
     | ██                     
     | ██  ▄▄      ██         
     | ██  ██      ██  ██     
     | ██  ██      ██  ██     
     | ██  ██      ██  ██     
     | ██  ██      ██  ██     
     | ██  ██  ▄▄  ██  ██  ██ 
     | ██  ██  ██  ██  ██  ██ 
  ---+-----------------------------------------------------------
     ||01||02||06||07||08||09|
```

### GUI

To start the GUI simply enter

`timetool gui`

This will bring up a little Overlay at the top-center of your Screen. You may double-Click this Overlay to start/stop the Worktime-Logging. Rightclick the Overlay to access the Settings or Display a Diagram for your current statistics. Theres also an Icon in the System-Tray.
Note that the Global-Shortcuts-Feature is probably only available on Linux Systems   .