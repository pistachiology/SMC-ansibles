
import os
import sys
os.system("rm cfind-max")
os.system("picco find-max.c smc-config cfind-max utilconf")
if not os.path.isfile("./cfind-max.cpp"):
    sys.exit(-1)
os.system("cp cfind-max.cpp ../compute/test-code.cpp; cd ../compute; make clean; make; cp test-code ../find-max/cfind-max;")