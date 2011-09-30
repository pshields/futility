#!/bin/bash
# Team management tasks

function start_player() {
  # $0 is team name
  # $1 is debug switch
  # $2 is player # in series
  DEBUG_TEXT=""
  PLAYER="one player"
  if [ $# -ge 3 ]; then
    PLAYER="Player #$3 on team $2"
  fi
  echo "Starting $PLAYER with arguments: $@..."
  java -cp build/ futility.Main $@ &
}

function start_team() {
  for (( i=1; i<=12; i++ )); do
    start_player $@ $i
  done
}

function stop_players() {
  for PID in `ps -ef | grep "java -cp build/ futility.Main" | grep -v grep | awk '{print $2}'`; do
    echo "Stopping player with pid $PID..."
    kill $PID
  done
}

case "$1" in
  compete)
    start_team "--team" "futility" "${@:2}"
    start_team "--team" "adversary" "${@:2}"
    ;;
  start)
    start_team "${@:2}"
    ;;
  startone)
    start_player "${@:2}"
    ;;
  stop)
    stop_players
    ;;
  *)
    echo $"Usage: $0 {compete|start|startone|stop}"
    exit 1
esac

exit 0
