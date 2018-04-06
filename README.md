## TimeTool
This is a simple Tool for logging working hours. Just a simple CLI and a small GUI. It utilizes a simple .csv-Files 
to store and manage all Data and the ./time.props-File for all persistent Settings.

### Installation

Download the timetool-x.7z-Archive (where x is the Version-Number) and extract it in some temp-Folder. Then execute the install.sh. This will install all Files to ~/TimeTool and setup a shortcut in /usr/bin so you can simply type 'timetool' instead of 'java -jar TimeTool.jar'.

### Usage

`TimeTool [MAIN-PARAMETER] [SETTING-PARAMETER]`

MAIN-PARAMETER: 

| Parameter | Funktion  |
| --------- | --------- |
| -start    | Log start |
| -stop     | Log stop  |
| -gui      | Starts the GUI |
| -monthly  | Splits the .csv defined by -l into separateMonths (also splits working hours into separate Days) |
| -list     | Lists the current Month, or the Month defined by -m and -y (requires -monthly to be called first) |
| -help     | Shows this Help-Message |
| -version  | Shows the current version |
        
SETTING-PARAMETER: 

| Parameter | Funktion  |
| --------- | --------- |
| -p/-P/-project/-Project | PROJECTNAME (Optional, if not present setting is taken from ./time.props) |
| -c/-C/-comment/-Comment | COMMENT (Optional, if not present setting is taken from ./time.props) |
| -l/-L/-location/-Location | LOCATION (the location for the Logging-.csv-File) (Optional, if not present setting is taken from ./time.props) |
| -m/-M/-month/-Month | MONTH (only valid for -list) |
| -y/-Y/-year/-Year | YEAR (only valid for -list) |
