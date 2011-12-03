#!/bin/bash
# Team management tasks

function start_player() {
  DEBUG_TEXT=""
  PLAYER="one player"
  if [ $# -ge 3 ]; then
    PLAYER="Player #$3 on team $2"
  fi
  echo "Starting $PLAYER with arguments: $@..."
  java -cp bin/ futility.Main $@ & 
  sleep .1 	
}

function start_team() {
  for (( i=1; i<=10; i++ )); do
    start_player $@ $i
  done
  start_player $@ 11 --goalie
}

function start_three() {
  for (( i=1; i<=2; i++ )); do
    start_player $@ $i
  done
  start_player $@ 3 --goalie
}

function stop_players() {
  for PID in `ps -ef | grep "java -cp bin/ futility.Main" | grep -v grep | awk '{print $2}'`; do
    echo "Stopping player with pid $PID..."
    kill $PID
  done
}

case "$1" in
  compete)
    start_team "--team" "futility" "${@:2}"
    start_team "--team" "adversary" "${@:2}"
    ;;
  scrimmage)
    start_three "--team" "futility" "${@:2}"
    start_three "--team" "adversary" "${@:2}"
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
    echo $"Usage: $0 {compete|scrimmage|start|startone|stop}"
    exit 1
esac

exit 0
