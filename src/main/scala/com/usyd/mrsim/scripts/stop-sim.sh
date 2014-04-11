#!/bin/bash

# locking example -- CORRECT
 # Bourne
 lockdir=/tmp/sim.lock
 endlockdir=/tmp/simRemove.lock
 simIn=/tmp/simIn.bin
 simOut=/tmp/simOut.bin
 if mv "$lockdir" "$endlockdir"
 then    # directory did not exist, but was created successfully
     echo >&2 "successfully removed lock: $lockdir, stopping simulation"
     pkill MrSimSimulation
     rm -rf "$endlockdir" "$simIn" "$simOut"
 else
     echo >&2 "cannot acquire lock, giving up on $lockdir"
     exit 0
 fi