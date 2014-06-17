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

testGreenBox:
	$(SBT) "run-main com.usyd.mrsim.greenbox.GreenBox $(SSIMFLAGS)"

#===============================================#
#-------------------- Life ---------------------#
#===============================================#

lifeTest:
	$(SBT) "run-main com.usyd.mrsim.example.life.LifeHardware $(SSIMFLAGS)"


lifeHardware:
	$(SBT) "run-main com.usyd.mrsim.greenbox.GreenBox $(HARDFLAGS)"

#===============================================#
#-------------------- Clean --------------------#
#===============================================#

clean:
	$(SBT) clean
	$(RM) *.class *.so *.h *.cpp *.o
	$(RM) -r project/*/
	$(RM) Mapper GreenBox
