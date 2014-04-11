#!/bin/bash

# locking example -- CORRECT
 # Bourne
 lockdir=/tmp/sim.lock
 endlockdir=/tmp/simRemove.lock
 if mv "$lockdir" "$endlockdir"
 then    # directory did not exist, but was created successfully
     echo >&2 "successfully removed lock: $lockdir, stopping simulation"
     pkill MrSimSimulation

 else
     echo >&2 "cannot acquire lock, giving up on $lockdir"
 fi

rm -rf "$endlockdir" /tmp/hw_in_pipe /tmp/hw_out_pipe /tmp/flagOut.lock
