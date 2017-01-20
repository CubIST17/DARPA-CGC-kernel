/* 
main function: parseG.cpp
*/


#include <iostream>
#include <fstream>
#include <string>
#include <ctime>
#include <time.h>

using namespace std;

#include "node.h"
#include "edge.h"
#include "function.h"
#include "graph.h"

/* get current time*/
void print_cur_time(){

	string strCurrentTime1 = "";
    struct tm *ptm1 = NULL;
    time_t tme1;
    tme1 = time(NULL);
    ptm1 = localtime(&tme1);
    char szTime1[256];
    sprintf(szTime1, "%d-%02d-%02d %02d:%02d:%02d", (ptm1->tm_year + 1900),ptm1->tm_mon, ptm1->tm_mday, ptm1->tm_hour, ptm1->tm_min, ptm1->tm_sec);
    strCurrentTime1 = szTime1;
    cout<<"now: "<<strCurrentTime1<<endl;
}

/*parse the original executable, find out the entry points: all functions that are uncalled*/
void parse_original(string original_cfgfile){
	
	GRAPH* g = new GRAPH();	

	g->read_cfg(original_cfgfile);	

	g->build_node_map();
	
	g->build_function_map();

    g->extract_graph();

    g->get_uncalled();

//shrink graph
//    g->shrink_graph();
//print out the cfg of GRAPH g
//    g->print_graph_cfg();
//    g->build_loop_map();
//    g->navigate();


}

/*parse the statically compiled executable*/
void parse_static(string static_cfgfile){
	
    GRAPH* g = new GRAPH();
    
    g->read_cfg(static_cfgfile);

    g->build_node_map();

    g->build_function_map();

    g->extract_graph();

    g->get_uncalled();


}

int main(){
	
	clock_t startClock, finishClock;
	double timeCount;
	startClock = clock();	
		

	string original_cfgfile = "original/cfg.dot";
	
    string static_cfgfile = "static/cfg.dot";

    print_cur_time();

	parse_original(original_cfgfile);
//	parse_static(static_cfgfile);

    print_cur_time();

	finishClock = clock();
	timeCount = finishClock - startClock;
	cout<<"time used for prob assignment and call transi matrix for function : "<<endl<<timeCount / (double) CLOCKS_PER_SEC<<endl;

	return 0;
}


