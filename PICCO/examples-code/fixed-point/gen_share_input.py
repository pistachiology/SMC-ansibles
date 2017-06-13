
import os



for i in range(1, 5):
	os.system("picco-utility -I {} input_{} utilconf share_{}_".format(i, i, i))
