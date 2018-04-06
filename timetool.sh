#!/bin/bash
args=""
for ((i=1;i<=$#;i++)); 
do
    args+=${!i}" "
done
cd ~/TimeTool/
java -jar ./TimeTool.jar $args
