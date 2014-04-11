#!/bin/bash

# locking example -- CORRECT
 # Bourne
 lockdir=/tmp/sim.lock
 if mkdir "$lockdir" &>/dev/null
 then    # directory did not exist, but was created successfully
     echo >&2 "successfully acquired lock: $lockdir"
     mkfifo /tmp/hw_in_pipe
     mkfifo /tmp/hw_out_pipe
     chmod 666 /tmp/hw_in_pipe
     chmod 666 /tmp/hw_out_pipe
     rm -rf /tmp/flagOut.lock
     /tmp/MrSimSimulation &
     exit 0
 else
     exit 0
 fi
