import sys
import os

if len(sys.argv) != 2:
	sys.exit(-1)

i = sys.argv[1]
os.system("./cfind-max {0} rtconf 4 1 share_1_{0} share_2_{0} share_3_{0} share_4_{0}  output_1_".format(i))
