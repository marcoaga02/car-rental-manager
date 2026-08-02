#!/bin/bash
NEW_DISPLAY=42
DONE="no"
while [ "$DONE" == "no" ]
do
  out=$(xdpyinfo -display :${NEW_DISPLAY} 2>&1)
  if [[ "$out" == name* ]] || [[ "$out" == Invalid* ]]
  then
    (( NEW_DISPLAY+=1 ))
  else
    DONE="yes"
  fi
done
echo "Using first available display :${NEW_DISPLAY}"
OLD_DISPLAY=${DISPLAY}
vncserver ":${NEW_DISPLAY}" -localhost -geometry 1600x1200 -depth 16 -SecurityTypes None
export DISPLAY=:${NEW_DISPLAY}
"$@"
export DISPLAY=${OLD_DISPLAY}
vncserver -kill ":${NEW_DISPLAY}"