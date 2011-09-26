#!/bin/bash
# Team management tasks

# `./manage.sh start` starts the team
if [ "$1" = "start" ]; then
  for (( i=1; i<=12; i++ )); do
    echo "Starting Player #$i..."
    java Client &
  done

# `./manage.sh stop` stops the team
elif [ "$1" = "stop" ]; then
  for PID in `ps -ef | grep "java Client" | grep -v grep | awk '{print $2}'`; do
    echo "Stopping player with pid $PID..."
    kill $PID
  done
fi
