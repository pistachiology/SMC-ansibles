
import os
import sys
os.system("rm cfixed-point-test.cpp")
os.system("picco fixed-point-test.c smc-config cfixed-point-test utilconf")
if not os.path.isfile("./cfixed-point-test.cpp"):
    sys.exit(-1)
os.system("cp cfixed-point-test.cpp ../compute/test-code.cpp; cd ../compute; make clean; make; cp test-code ../fixed-point/cfixed-point;")