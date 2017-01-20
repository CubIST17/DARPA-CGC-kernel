/* edge.cpp */
#include "edge.h"

using namespace std;

/*set edge properties*/
void EDGE::set_s_block(string s){
	s_block = s;
}

void EDGE::set_d_block(string d){
	d_block = d;
}

void EDGE::set_edge_type(EDGE_TYPE type){
	edge_type = type;
}

/*get edge properties*/
string EDGE::get_s_block(){
	return s_block;
}

string EDGE::get_d_block(){
	return d_block;
}


EDGE_TYPE EDGE::get_edge_type(){
	return edge_type;
}

EDGE::EDGE(string source, string destination, string type){
	s_block = source;
	d_block = destination;

	if(type == "blue")
		edge_type = CALL;
	else if(type == "pink")
		edge_type = COND_TAKEN;
	else if(type == "hotpink")
		edge_type = COND_NOT_TAKEN;
	else if(type == "yellow")
		edge_type = INDIRECT;
	else if(type == "yellow4")
		edge_type = DIRECT;
	else if(type == "red")
		edge_type = FALLTHROUGH;
	else if(type == "orange")
		edge_type = CATCH;
	else if(type == "red4")
		edge_type = CALL_FT;
	else if(type == "green")
		edge_type = RET;
    else if(type == "modified")//for the use of graph shrinking
        edge_type = MODIFIED;
}

/*print edge information*/
void EDGE::print_edge(){
	cout<<"edge informaiton: "<<endl
		<<"edge_type = "<<edge_type<<endl;
}








