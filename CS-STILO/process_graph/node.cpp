/* node.cpp */
#include <iostream>
#include "node.h"

using namespace std;

/*set node properties*/

void NODE::set_node_name(string name){
	node_name = name;
}

void NODE::set_node_type(NODE_TYPE type){
	node_type = type;
}


/*get node properties*/

string NODE::get_node_name(){
	return node_name;
}

NODE_TYPE NODE::get_node_type(){
	return node_type;
}

list<EDGE*>& NODE::get_out_edge_list(){
	return out_edges;	
};


void NODE::add_out_edge(EDGE* p_edge){
	out_edges.push_back(p_edge);
}

void NODE::clear_out_edge(){
    while(!out_edges.empty()){
        out_edges.pop_back();
    }
}

/*print node information*/
void NODE::print_node(){
	cout<<"node information: "<<endl
		<<"node_name = "<<node_name<<endl
		<<"node_type = "<<node_type<<endl;
}
		
