#!/bin/bash
#export MAVEN_OPTS="-Xmx4096M -XX:PermSize=256M -XX:MaxPermSize=256M "
export MAVEN_OPTS="-Xmx2048m -agentpath:/work/devtools/jprofiler8/bin/linux-x64/libjprofilerti.so=nowait,port=7777"
API_PROFILE=dev;[ -n "$2" ] && API_PROFILE=$2 
API_COMMAND="mvn -Dspring.profiles.active=$API_PROFILE -Dnextprot.api jetty:run"
PKILL=$(which pkill)
LOGFILE="npapi.log"

#
#
# simple function to start cmd
function startdaemon(){
  PID=''
  [ -f $2 ] && PID=$(cat $2);
  [ -n "$PID" ] && kill -0 $PID 2>/dev/null && {
    echo "this deamon ($1) is already started"
    return;
  }
  echo starting $1
  [ -n "$LOGFILE" ] && exec &> $LOGFILE  
  $1 &
  PID=$!
  echo $PID >$2
}
#
#
# simple function to stop cmd
function stopdaemon(){
  PID=''
  [ -f $1 ] && PID=$(cat $1) && [ -n "$PID" ] && kill -0 $PID 2>/dev/null && {
    kill $PID ; rm $1
    echo -n "killing $PID ($2) "
    while kill -0 $PID 2>/dev/null; do sleep 1;echo -n '.';done
    echo
  } || {
    echo "nothing to stop..."
    [ -n "$PKILL" ] && $PKILL -9 -f "$2" 
  }
}

#
# in case $0 start, jetty is always restarted
[  "$1" == "start" ] && {
  echo "STARTING...."
  [ -f "$LOGFILE" ] && cp $LOGFILE "${LOGFILE}.bck"
  stopdaemon  /tmp/np-api.pid  nextprot.api
  startdaemon "$API_COMMAND" /tmp/np-api.pid 
  
  exit
}

[  "$1" == "stop" ] && {
  echo "STOPING...."
  stopdaemon  /tmp/np-api.pid  nextprot.api
  exit
}  

[  "$1" == "status" ] && {
  [ -f /tmp/np-api.pid ] && PID=$(cat /tmp/np-api.pid) && echo "jetty api is running $PID" ||{
    echo "jetty api is not running"
  }
  exit
}

[  "$1" == "log" ] && {
  tail -f $LOGFILE
  exit
}

echo "run $0 [start|stop|status|log] [pro|dev*]"
exit 1
