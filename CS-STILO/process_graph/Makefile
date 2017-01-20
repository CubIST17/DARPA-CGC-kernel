all: parseGRAPH

parseGRAPH: node.o edge.o function.o graph.o parseG.o
	g++ node.o edge.o function.o graph.o parseG.o -o parseGRAPH

parseG.o: parseG.cpp graph.h function.h node.h edge.h
	g++ -c parseG.cpp

graph.o: graph.cpp graph.h function.h node.h edge.h
	g++ -c graph.cpp

function.o: function.cpp function.h node.h edge.h 
	g++ -c function.cpp

node.o: node.cpp node.h
	g++ -c node.cpp

edge.o: edge.cpp edge.h
	g++ -c edge.cpp

clean: 
	rm *.o parseGRAPH uncalled transi-prob 
