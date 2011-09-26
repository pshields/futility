#!/bin/bash
# Team management tasks

function start_team() {
  for (( i=1; i<=12; i++ )); do
    echo "Starting Player #$i on team $1..."
    cd build/
    java futility.Main $1 &
    cd ..;
  done
}

function stop_players() {
  for PID in `ps -ef | grep "java futility.Main" | grep -v grep | awk '{print $2}'`; do
    echo "Stopping player with pid $PID..."
    kill $PID
  done
}

case "$1" in
  compete)
    start_team "futility"
    start_team "adversary"
    ;;
  start)
    start_team "futility"
    ;;
  stop)
    stop_players
    ;;
  *)
    echo $"Usage: $0 {compete|start|stop}"
    exit 1
esac

exit 0
