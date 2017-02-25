import os
from sets import Set

dic = Set()
location = "../CS-STILO/HMM/flex_v5/trace/"
dirs = os.listdir(location);
for directory in dirs:
	print("doing "+directory)
	traces = open(location + directory, 'r').readlines()
	for call in traces:
		sign = call[0: call.find('(')]
		dic.add(sign)
outfile = open(location+"trace.dic", 'w')
counter = 0
for item in dic:
	outfile.write(item + " " + str(counter) + '\n')
	counter += 1