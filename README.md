# TimeTool
This is a simple Tool for logging working hours. Just a simple CLI and a small GUI. It utilizes a simple .csv-Files 
to store and manage all Data and the `./time.props`-File for all persistent Settings.

## Installation

Download the `timetool-x.zip`-Archive (where x is the Version-Number e.g. '0.1') and extract it in some temp-Folder. Then execute the install.sh. This will install all Files to `~/TimeTool` and setup a Shortcut in `/usr/bin` so you can simply type 'timetool' instead of 'java -jar TimeTool.jar'.

## Usage

`timetool [OPTION] [SETTING]`

Options: 

| Option |  Description  | Possible Parameter |
| -------------- | ---------- | -------------------------- |
| -start         | Log start  | -p/-c/-l                   |
| -stop          | Log stop   | -p/-c/-l                   |
| -gui           | Starts the GUI | -p/-c/-l                   |
| -monthly       | Splits the .csv defined by -l into separateMonths (also splits working hours into separate Days, if necessary) | -l                   |
| -list          | Lists the current Month, or the Month defined by -m and -y (requires corresponding '-monthly'-Files). You may also use -p to specify a Project | -p/-l/-m/-y             |
| -help          | Shows this Help-Message |                            |
| -version       | Shows the current Version |                            |
        
Settings: 

| Setting | Description  |
| --------- | --------- |
| -p/-P/-project/-Project | PROJECTNAME (Optional, if not present Setting is taken from `./time.props`) |
| -c/-C/-comment/-Comment | COMMENT (Optional, if not present Setting is taken from `./time.props`) |
| -l/-L/-location/-Location | LOCATION (the location for the Logging-.csv-File) (Optional, if not present Setting is taken from `./time.props` or set to `./time.csv`) |
| -m/-M/-month/-Month | MONTH (only valid for -list) |
| -y/-Y/-year/-Year | YEAR (only valid for -list) |

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
       Date    |   Start  |    End   | Duration  |    Project    |         Comment         
   ------------+----------+----------+-----------+---------------+-------------------------
    2018-04-01 | 00:00:00 | 04:05:17 |  0d 04:05 |   TimeTool    |                           
    2018-04-01 | 21:03:35 | 00:00:00 |  0d 02:56 |   TimeTool    | added -monthly and -list  
    2018-04-02 | 00:00:00 | 04:27:32 |  0d 04:27 |   TimeTool    | added -monthly and -list  
   ------------+----------+----------+-----------+---------------+-------------------------
               |          |   SUM=   |  0d 11:29 |               |                         
```

### GUI

To start the GUI simply enter

`timetool gui`

This will bring up a little Overlay at the top-center of your Screen. You may double-Click this Overlay to start/stop the Worktime-Logging. Rightclick the Overlay to access the Settings. Theres also an Icon in the System-Tray.
Note that the Global-Shortcuts-Feature is probably only available on Linux Systems   .