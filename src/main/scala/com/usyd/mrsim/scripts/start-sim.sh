#!/bin/bash

# locking example -- CORRECT
 # Bourne
 lockdir=/tmp/sim.lock
 if mkdir "$lockdir" &>/dev/null
 then    # directory did not exist, but was created successfully
     echo >&2 "successfully acquired lock: $lockdir"
     /tmp/MrSimSimulation &
     exit 0
 else
     exit 0
 fi