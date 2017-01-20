/* graph.cpp */
#include "graph.h"
#include "node.h"
#include "edge.h"
#include "function.h"

using namespace std;

list<FUNCTION*> & GRAPH::get_function_list(){
	return functions;
}

void GRAPH::add_function_list(FUNCTION* p_function){
	functions.push_back(p_function);
	return;
}

void GRAPH::read_cfg(string cfgfile){
	
	/* transform file name from string to char* */
	char f[100];
	for ( int i = 0; i < cfgfile.size(); i++){
		f[i] = cfgfile[i];
	}
	f[cfgfile.size()]='\0';

	/* open the file */
	ifstream f_cfg(f);
	string line;
	string begin;
	getline(f_cfg, begin);
	
	/* construct the functions */	
	string end = "}";
	getline(f_cfg, line);

	FUNCTION* p_function;
	string function_name;
	string function_entry_node;
	string function_return_type;
	string temp;

	while (line != end){
		
		p_function = new FUNCTION();

		/* get and set the function name*/
		temp = line.substr(line.find_last_of('=')+2);
		function_name = temp.erase(temp.find_last_of(']')-1);
		cout<<"function_name: "<<function_name<<endl;   //cl de-canceled//
		p_function->set_function_name(function_name);

		/*get and set the function entry node*/
		temp = line.substr(line.find_first_of('"')+1);
		function_entry_node = temp.erase(temp.find_first_of('[')-2);
		cout<<"function_entry_node: "<<function_entry_node<<endl;  //cl de-canceled//
		p_function->set_function_entry_node(function_entry_node);

		
		/*get and set function return type*/
		temp = line.substr(line.find_first_of(',')+7);
		function_return_type = temp.erase(temp.find_first_of(","));
		cout<<"function_return_type: "<<function_return_type<<endl;   //cl de-canceled//
		p_function->set_function_return_type(function_return_type);

	
		/* call FUNCTION instance to deal with node and edge information */
		p_function->read_cfg(f_cfg);

		add_function_list(p_function);
		
		getline(f_cfg,line);
	}

	f_cfg.close();
	return;
}


/* nodes_map functions*/
map<string, NODE*>& GRAPH::get_node_map(){
	return nodes_map;
}

/*build a mapping between node name and node pointer within the whole graph*/
void GRAPH::build_node_map(){

	list<FUNCTION*>::iterator f_it;

	for( f_it = functions.begin(); f_it != functions.end(); f_it++){
		list<NODE*>& n_list = (*f_it)->get_node_list();
		list<NODE*>::iterator n_it;
		for (n_it = n_list.begin(); n_it != n_list.end(); n_it++){
			string node_name = (*n_it)->get_node_name();
			NODE* p_node = (*n_it);
			add_node_map(node_name, p_node);
		}
    }

    for( f_it = functions.begin(); f_it != functions.end(); f_it++){

        list<NODE*>& des_n_list = (*f_it)->get_des_node_list();
        list<NODE*>::iterator des_n_it;
        for(des_n_it = des_n_list.begin(); des_n_it != des_n_list.end(); des_n_it++){
            string des_node_name = (*des_n_it)->get_node_name();
            NODE* p_des_node = (*des_n_it);
            add_node_map(des_node_name, p_des_node);
        }
	}
	return;
}

/*called by build_node_map*/
void GRAPH::add_node_map(string node_name, NODE* p_node){
    nodes_map.insert( pair<string,NODE*>(node_name, p_node) );
	return;
}

/*print graph information*/		
void GRAPH::print_graph(){
     

        list<FUNCTION*>& f_list = functions;
        list<FUNCTION*>::iterator f_it;

        for(f_it = f_list.begin(); f_it != f_list.end(); f_it++){
                cout<<"function: "<<(*f_it)->get_function_name()<<endl;
                
                list<NODE*>& n_list = (*f_it)->get_node_list();
                list<NODE*>::iterator n_it;
                for(n_it = n_list.begin(); n_it!=n_list.end(); n_it++){
                        cout<<"\tnode: "<<(*n_it)->get_node_name()<<endl;
                        
                        list<EDGE*>& e_list = (*n_it)->get_out_edge_list();
                        list<EDGE*>::iterator e_it;
                        for(e_it = e_list.begin(); e_it != e_list.end(); e_it++){
                                cout<<"\t\tedge: "<<(*e_it)->get_d_block()<<endl;
                        }               
                }               
        }
	return;
}

/*functions_map functions*/
void GRAPH::add_function_map(string function_entry_node ,FUNCTION* p_function){
	functions_map.insert(pair<string, FUNCTION*>(function_entry_node, p_function));
	return;
}

/*store the mapping between entry node name  and  function pointer*/
void GRAPH::build_function_map(){
	string function_entry_node;
	FUNCTION* p_function;
	list<FUNCTION*>::iterator f_it;
	for (f_it = functions.begin(); f_it != functions.end(); f_it++){
		function_entry_node = (*f_it)->get_function_entry_node();
		p_function = (*f_it);
		add_function_map(function_entry_node, p_function);	
	}
	return;
}		

map<string, FUNCTION*>& GRAPH::get_function_map(){
	return functions_map;	
}

void GRAPH::print_function_map(){
	map<string,FUNCTION*>::iterator f_map_it;
	for (f_map_it = functions_map.begin(); f_map_it!= functions_map.end(); f_map_it++){
		cout<<(f_map_it->second)->get_function_name()<<endl;
	}
}


/*build the loop map*/
void GRAPH::build_loop_map(){

    int index = 0;

    list<FUNCTION*>::iterator f_it;
    for(f_it = functions.begin(); f_it != functions.end(); f_it++){
        
        index++;

        if( (*f_it)->get_function_name() != "get_method" )continue;
//        if( (*f_it)->get_function_name() == "treat_file" )continue;

        cout<<"building loop map from function "<<index<<" : "<<(*f_it)->get_function_name()<<endl;
        (*f_it)->construct_loop_map(functions_map, nodes_map, loops_map);

    }
}

//find all paths and loops
void GRAPH::navigate(){
	
	/*
	let each function navigate its own paths.
	*/

	list<FUNCTION*>::iterator f_it;
	for(f_it = functions.begin(); f_it != functions.end(); f_it++){


       
		cout<<"*************analysing function: "<<(*f_it)->get_function_name()<<endl;
		(*f_it)->find_path(functions_map, nodes_map);
        (*f_it)->find_loop(functions_map, nodes_map);
		
		/*print all nodes within a function*/
//		list<NODE*> f_nodes = (*f_it)->get_node_list();
//		list<NODE*>::iterator n_it;
//		for( n_it = f_nodes.begin(); n_it != f_nodes.end(); n_it++){
//			cout<<(*n_it)->get_node_name()<<endl;
//		}

//		cout<<"printing the paths:"<<endl;
//		(*f_it)->print_path_to_screen();
//		cout<<"*************analysis complete"<<endl;
//		cout<<endl;
//        (*f_it)->print_node_path_to_screen();
	}
}

/*get all functions that are not called*/
void GRAPH::get_uncalled(){
	/*find all functions that have been called*/
	map<string,bool> function_called;
	list<FUNCTION*>::iterator f_it;
	for(f_it = functions.begin(); f_it!=functions.end(); f_it++){
		list<NODE*> nodes = (*f_it)->get_node_list();
		list<NODE*>::iterator n_it;
		for(n_it = nodes.begin(); n_it!=nodes.end(); n_it++){
			list<EDGE*> edges = (*n_it)->get_out_edge_list();
			list<EDGE*>::iterator e_it;
			for(e_it = edges.begin(); e_it!= edges.end(); e_it++){
				if( (*e_it)->get_edge_type() != CALL) continue;
				string d_block_name = (*e_it)->get_d_block();
				if(functions_map.find(d_block_name)!=functions_map.end()){
//					cout<<(functions_map.find(d_block_name)->second)->get_function_name()<<endl;
					string f_name = (functions_map.find(d_block_name)->second)->get_function_name();
					function_called.insert( pair<string,bool>(f_name, true) );
				}
			}
		}
	}		

	ofstream f;
	f.open("uncalled",ios::app);
    int called_num = 0, uncalled_num = 0;
	/*get the functions that have not been called */
	for(f_it = functions.begin(); f_it!=functions.end(); f_it++){
		string f_name = (*f_it)->get_function_name();
		if(function_called.find(f_name)!=function_called.end()){
            called_num++;
			continue;
		}
		else{
            uncalled_num++;
			f<<f_name<<endl;
		}
	}
	f.close();
    cout<<"called: "<<called_num<<endl;
    cout<<"uncalled: "<<uncalled_num<<endl;
}

void GRAPH::shrink_graph(){
    list<FUNCTION*>::iterator f_it;
    for(f_it = functions.begin(); f_it != functions.end(); f_it++){

        if( (*f_it)->get_function_name()!="get_method" )continue;
        cout<<"Shrinking function: "<<(*f_it)->get_function_name()<<endl;
        (*f_it)->shrink_function(functions_map, nodes_map);
    }
}

void GRAPH::print_graph_cfg(){
    list<FUNCTION*>::iterator f_it;
    for(f_it = functions.begin(); f_it != functions.end(); f_it++){
        
        if( (*f_it)->get_function_name()!="get_method" )continue;
        cout<<"Printing cfg of function: "<<(*f_it)->get_function_name()<<endl;
        (*f_it)->print_function_cfg();
    }
}

void GRAPH::extract_graph(){
    list<FUNCTION*>::iterator f_it;
    for(f_it = functions.begin(); f_it != functions.end(); f_it++){
        cout<<"extracting information from function: "<<(*f_it)->get_function_name()<<endl;
//        if( (*f_it)->get_function_name()!="free_slotinfo" )continue;
        (*f_it)->extract_function(functions_map);
    }
}


