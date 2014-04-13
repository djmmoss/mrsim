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

testHardware: setupIdentity testSim testCon testAvro

testCon: setupIdentity
	$(SBT) "run-main com.usyd.mrsim.controller.Controller $(SSIMFLAGS)"

testAvro: setupIdentity
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

testInterface: setupIdentity hardware
	/tmp/start-sim.sh
	$(SBT) "run-main com.usyd.mrsim.np_test"
	/tmp/stop-sim.sh



#===============================================#
#------------------ WordCount ------------------#
#===============================================#

wordcount: setupWordCount hardware
	/tmp/start-sim.sh
	$(SBT) $(JNILIB) "run-main com.usyd.mrsim.example.WordCount ./data/words_0.txt output"
	/tmp/stop-sim.sh

setupWordCount:
	cp examples/WordCount.scala ./src/main/scala/com/usyd/mrsim/.

testWordCountHardware: setupWordCount testSim

wordcountHardware: setupWordCount
	$(SBT) "run-main com.usyd.mrsim.sim.MrSimSimulation $(HARDFLAGS)"

#===============================================#
#-------------------- Life ---------------------#
#===============================================#

life: setupLife hardware
	/tmp/start-sim.sh
	$(SBT) "run-main com.usyd.mrsim.example.Life ./data/numbers_0.txt output"
	/tmp/stop-sim.sh

setupLife:
	cp examples/Life.scala ./src/main/scala/com/usyd/mrsim/.

testLifeHardware: setupLife testSim

lifeHardware: setupLife
	$(SBT) "run-main com.usyd.mrsim.sim.MrSimSimulation $(HARDFLAGS)"

#===============================================#
#------------------- KMeans --------------------#
#===============================================#

kmeans: setupKMeans hardware
	/tmp/start-sim.sh
	$(SBT) "run-main com.usyd.mrsim.example.KMeans ./data/points/points_0.txt output"
	/tmp/stop-sim.sh

setupKMeans:
	cp examples/KMeans.scala ./src/main/scala/com/usyd/mrsim/.

testKMeansHardware: setupLife testSim

kmeansHardware: setupKMeans
	$(SBT) "run-main com.usyd.mrsim.sim.MrSimSimulation $(HARDFLAGS)"

#===============================================#
#------------------ Identity -------------------#
#===============================================#

identity: setupIdentity hardware
	/tmp/start-sim.sh
	$(SBT) "run-main com.usyd.mrsim.example.Identity ./data/words_0.txt output"
	/tmp/stop-sim.sh

setupIdentity:
	cp examples/Identity.scala ./src/main/scala/com/usyd/mrsim/.

testIdentityHardware: setupIdentity testSim

identityHardware: setupIdentity
	$(SBT) "run-main com.usyd.mrsim.sim.MrSimSimulation $(HARDFLAGS)"

#===============================================#
#---------------- Scoobi Tests -----------------#
#===============================================#

localTest:
	cd cluster_test; cp loc_build build.sbt
	#cd cluster_test; $(SBT) "run-main WordCountTest ../data/words_0.txt outputWord"
	#cd cluster_test; $(SBT) "run-main LifeTest ../data/numbers/in.txt outputLife"
	cd cluster_test; $(SBT) "run-main KMeansTest ../data/points/points_0.txt"

awsTest:
	cd cluster_test; cp aws_build build.sbt
	cd cluster_test; sbt assembly
	#cd cluster_test; hadoop jar dd./target/scala-2.10/ScoobiTest-assembly-1.0.jar WordCountTest words output1 scoobi
	#cd cluster_test; hadoop jar ./target/scala-2.10/ScoobiTest-assembly-1.0.jar LifeTest numbers output1 scoobi
	cd cluster_test; hadoop jar ./target/scala-2.10/ScoobiTest-assembly-1.0.jar KMeansTest points output1 scoobi
	cd cluster_test; cp loc_build build.sbt

hkuTest:
	cd cluster_test; cp hku_build build.sbt
	cd cluster_test; sbt assembly
	cd cluster_test; hadoop jar ./target/scala-2.10/WordCountTest-assembly-1.0.jar words output1 scoobi
	cd cluster_test; cp loc_build build.sbt

#===============================================#
#------------------- Cluster -------------------#
#===============================================#

hkuIdentity: setupIdentity
	cp hku_build build.sbt
	sbt assembly
	#hadoop jar ./target/scala-2.10/Identity-assembly-1.0.jar com.usyd.mrsim.example.Identity words output1 scoobi
	cp loc_build build.sbt

awsIdentity: makeAwsIdentity
	cp aws_build build.sbt
	sbt assembly
	hadoop jar ./target/scala-2.10/Identity-assembly-1.0.jar com.usyd.mrsim.example.Identity words output1 scoobi
	cp loc_build build.sbt

makeAwsIdentity: setupIdentity hardware
	/tmp/start-sim.sh

awsLife: makeAwsLife
	cp aws_build build.sbt
	sbt assembly
	hadoop jar ./target/scala-2.10/Identity-assembly-1.0.jar com.usyd.mrsim.example.Life numbers output1 scoobi
	cp loc_build build.sbt

makeAwsLife: setupLife hardware
	/tmp/start-sim.sh

hdfsWords:
	hadoop dfs -mkdir words &>/dev/null
	hadoop dfs -copyFromLocal ./data/words/* words &>/dev/null

hdfsNumbers:
	hadoop dfs -mkdir numbers &>/dev/null
	hadoop dfs -copyFromLocal ./data/numbers/* numbers &>/dev/null

hdfsPoints:
	hadoop dfs -mkdir points &>/dev/null
	hadoop dfs -copyFromLocal ./data/points/* points &>/dev/null



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
	$(RM) ./src/main/scala/com/usyd/mrsim/WordCount.scala
	$(RM) ./src/main/scala/com/usyd/mrsim/Life.scala
	$(RM) ./src/main/scala/com/usyd/mrsim/Identity.scala
	$(RM) ./src/main/scala/com/usyd/mrsim/KMeans.scala
