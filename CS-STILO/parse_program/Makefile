all: parse

parse: parse.cpp
	#g++ -Wall -Wno-deprecated -c -o parse.o parse.cpp
	#g++ parse.o -Wl,-rpath=/usr/local/lib -L/usr/local/lib -lparseAPI 
	g++ -Wall -Wno-deprecated -o parse parse.cpp -lparseAPI 

clean: 
	rm parse
