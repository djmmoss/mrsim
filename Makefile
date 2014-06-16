UNAME_S := $(shell uname -s)

ifeq ($(UNAME_S), Linux)
endif
ifeq ($(UNAME_S), Darwin)
endif

# Target Compiler
SBT = sbt
CC = g++

PATHTO = $(shell pwd)/src/main/scala/
PATHROOT = com/usyd/mrsim

# Compiler Flags:
HARDFLAGS = --backend v --genHarness --targetDir ./verilog/
SSIMFLAGS = --backend c --genHarness --compile --test
CFLAGS = -Wall

#===============================================#
#------------------- Hardware ------------------#
#===============================================#

testHardware: setupLife testSim testCon testAvro

testCon: setupLife
	$(SBT) "run-main com.usyd.mrsim.controller.Controller $(SSIMFLAGS)"

testAvro: setupLife
	$(SBT) "run-main com.usyd.mrsim.test.AvroTest $(SSIMFLAGS)"

testSim:
	$(SBT) "run-main com.usyd.mrsim.sim.MrSimSimulation $(SSIMFLAGS)"

hardware: SimScript ScriptSetup
	$(CC) -o MrSimSimulation MrSimSimulation.cpp MrSimSimulation-emulator.cpp
	cp MrSimSimulation /tmp/.

SimScript: testSim
	$(PATHTO)com/usyd/mrsim/scripts/SimulationScript.py

ScriptSetup:
	cp $(PATHTO)com/usyd/mrsim/scripts/*-sim.sh /tmp/.

testInterface: setupLife hardware
	/tmp/start-sim.sh
	$(SBT) "run-main com.usyd.mrsim.np_test"
	/tmp/stop-sim.sh




#===============================================#
#-------------------- Life ---------------------#
#===============================================#

life: setupLife hardware
	/tmp/start-sim.sh
	$(SBT) "run-main com.usyd.mrsim.example.Life ./data/numbers/* output"
	/tmp/stop-sim.sh

setupLife:
	cp -r examples/life ./src/main/scala/com/usyd/mrsim/.

testLifeHardware: setupLife testSim

lifeHardware: setupLife
	$(SBT) "run-main com.usyd.mrsim.sim.MrSimSimulation $(HARDFLAGS)"


#===============================================#
#---------------- Scoobi Tests -----------------#
#===============================================#

localTest:
	cd cluster_test; cp loc_build build.sbt
	cd cluster_test; $(SBT) "run-main LifeTest ../data/numbers/in.txt outputLife"

hkuTest:
	cd cluster_test; cp hku_build build.sbt
	cd cluster_test; sbt assembly
	cd cluster_test; hadoop jar ./target/scala-2.10/WordCountTest-assembly-1.0.jar words output1 scoobi
	cd cluster_test; cp loc_build build.sbt


#===============================================#
#-------------------- Clean --------------------#
#===============================================#

clean:
	$(SBT) clean
	$(RM) *.class *.so *.h *.cpp *.o *-*-*-* scoobi.*
	cd /tmp; $(RM) *sim.sh MrSimSimulation hw_in_pipe hw_out_pipe
	cd cluster_test; $(RM) -r project/*/
	cd cluster_test; $(RM) -r output target
	$(RM) -r project/*/
	$(RM) -r output target
	$(RM) MrSimSimulation
	$(RM) -r $(PATHTO)com/usyd/mrsim/com
	$(RM) -rf ./src/main/scala/com/usyd/mrsim/life
