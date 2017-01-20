import glob

def get_call_in_pa(program, call_in_pa):
	f = open(program+"/initial-all/complete");
	f.readline()
	line = f.readline()
	calls = line.split(",")
	for i in range(1, len(calls)-1):
		call_in_pa.add(calls[i])


def get_call_info(program):
	call_in_pa = set()
	get_call_in_pa(program, call_in_pa)
	#print call_in_pa

	call_in_trace = set()
	for fname in glob.glob(program+"/strace-translated/*.trace"):
		with open(fname) as f:
			for line in f:
				if line.find("@") != -1:
					call = line[0:line.find("\n")]
					#print call
					if call in call_in_pa:
						call_in_trace.add( call )
					else:
						print call
	print program
	print "len of call_in_pa ", len(call_in_pa)
	print "len of call_in_trace ", len(call_in_trace)
	print call_in_trace, "\n"

def main():


#	get_call_info("flex_v5")
#	get_call_info("grep_v5")
#	get_call_info("gzip_v5")
#	get_call_info("sed_v7")
#	get_call_info("bash_v6")
#	get_call_info("vim_v7")

#	get_call_info("proftpd")

	get_call_info("nginx")



main()
