/* function.cpp */
#include "function.h"
#include "node.h"
#include "edge.h"

using namespace std;

void FUNCTION::set_function_name(string name){
	function_name = name;
}

void FUNCTION::set_function_entry_node(string entry_node){
	function_entry_node = entry_node;
}

string FUNCTION::get_function_name(){
	return function_name;
}

string FUNCTION::get_function_entry_node(){
	return function_entry_node;
}


void FUNCTION::set_function_return_type(string ret_type){
	if (ret_type == "green")
		function_return_type = UNSET;
	else if (ret_type == "red")
		function_return_type = NORETURN;
	else if (ret_type == "yellow")
		function_return_type = UNKNOWN;
	else if (ret_type == "black")
		function_return_type = RETURN;
}

RETURN_TYPE FUNCTION::get_function_return_type(){
	return function_return_type;
}



list<NODE*>& FUNCTION::get_node_list(){
	return nodes;
}

list<NODE*>& FUNCTION::get_des_node_list(){
    return des_nodes;
}

void FUNCTION::read_cfg(ifstream & f_cfg) {
	
	string line;
	streampos pos = f_cfg.tellg();
	getline(f_cfg, line);

    //bool print_info = true;   //cl de-canceled//
//    if(function_name =="parse_lsda_header")print_info = true;

	/* while file pointer still within current function */
	while(line.find_first_of("->")!= -1){

		/* read the node and edge information */
		/* TO DO */
		string temp;

		/* get the source block, since those belong to the current function*/
		int s_start, s_end;
		string source;
		temp = line;
		s_start = temp.find_first_of('"')+1;
		temp = temp.substr(s_start);
		s_end = temp.find_first_of('-')-2;
		source = temp.erase(s_end);

        /*store the source node*/
		NODE* p_node = get_node_by_name(source);
		/*if this node is not in current function's nodes list yet*/
		if(p_node == NULL){
			p_node = new NODE();
			p_node->set_node_name(source);
			nodes.push_back(p_node);
		}
	
		/* get the destination block */
		int d_start, d_end; 
		string destination;
		temp = line;
		d_start = temp.find_first_of('>')+3;
		temp = temp.substr(d_start);
		d_end = temp.find_first_of('[')-2;
		destination = temp.erase(d_end);
		
        if(destination!="*"){//if it's not empty block

		/* get the edge type */
		string type;
		int t_start, t_end;
		temp = line;
		t_start = temp.find_first_of('=')+1;
		temp = temp.substr(t_start);
		t_end = temp.find_first_of(']');
		type = temp.erase(t_end);
		
		//cout<<"now line is: "<<destination<<endl;  //cl de-canceled//

	
		/* create and add new edge to the source node */
		EDGE* p_edge = new EDGE(source, destination, type);		
		p_node->add_out_edge(p_edge);

		//if(print_info) cout<<source<<" "<<destination<<" added"<<endl;  //cl de-canceled//

        /*store the destination node*/
        NODE* p_des_node = get_des_node_by_name(destination);
        if(p_des_node == NULL){
            p_des_node = new NODE();
            p_des_node->set_node_name(destination);
            des_nodes.push_back(p_des_node);
        }

        }


		pos = f_cfg.tellg();
		getline(f_cfg, line);		
	}
	
	f_cfg.seekg(pos);
	
	//if(print_info)print_node_list(); //cl de-canceled//

	return;
}	

/* test if a node already exists in function's node list */
NODE* FUNCTION::get_node_by_name(string name){
	list<NODE*>::iterator it;

	for(it = nodes.begin(); it != nodes.end(); it++){
		if( (*it)->get_node_name() == name ){
			return (*it);
		}
	}
	return NULL;	
}

/* test if a node already exists in function's destination node list */
NODE* FUNCTION::get_des_node_by_name(string name){
	list<NODE*>::iterator it;

	for(it = des_nodes.begin(); it != des_nodes.end(); it++){
		if( (*it)->get_node_name() == name ){
			return (*it);
		}
	}
	return NULL;	
}

/* print all the node information of a function's node list */
void FUNCTION::print_node_list(){
	list<NODE*>::iterator it;

	cout<<"the node list in function "<<function_name<<" is:"<<endl;
	for(it = nodes.begin(); it != nodes.end(); it++){
		cout<<"Node: "<<(*it)->get_node_name()<<" To: ";

        list<EDGE*> edge_list = (*it)->get_out_edge_list();
        list<EDGE*>::iterator e_it;

        for(e_it = edge_list.begin(); e_it!=edge_list.end(); e_it++){
            string d_block = (*e_it)->get_d_block();
            cout<<d_block<<", ";
        }
        cout<<endl;
	}
}

//not used here now, node and loop sequence are stored directly
list<string> FUNCTION::translate(list<string>& node_seq, map<string, FUNCTION*> &functions_map, map<string, NODE*> &nodes_map){
    /*get call sequence from node sequence*/
    list<string>::iterator node_it;
    list<string> call_seq;
    for(node_it = node_seq.begin(); node_it!=node_seq.end(); node_it++){
        string node_name = (*node_it);
/*TO DO: function entry*/

        NODE* p_node = nodes_map.find(node_name)->second;
        list<EDGE*>& out_edge_list = p_node->get_out_edge_list();
        list<EDGE*>::iterator e_it;
        for(e_it = out_edge_list.begin(); e_it != out_edge_list.end(); e_it++){
            EDGE* p_edge = (*e_it);
            if( p_edge->get_edge_type()==CALL ){
                string d_block_name = p_edge->get_d_block();
                if(d_block_name == "ffffffffffffffff"){
//cout<<"call ffffffffffffffff? seriously?"<<endl;
                }
                else{
                    string f_name = (functions_map.find(d_block_name)->second)->get_function_name();
                    call_seq.push_back(f_name);
//cout<<"function call found: "<<f_name<<endl;
                }
            }
        }
    }
    return call_seq;
}


/*get all possible paths within a function*/
void FUNCTION::find_path(map<string, FUNCTION*>& functions_map, map<string, NODE*>& nodes_map ){

//    cout<<"before find path:"<<endl;
//    print_node_list();

	stack<NODE*> waiting_nodes;//pointers of nodes to be explored
	stack< list<string> > stored_paths;//path associated with those waiting_nodes, before

	if(nodes.empty()){
		cout<<"function with empty nodes list"<<endl;
		return;
	}

	/*get the first node of function*/
	NODE* p_first_node = *nodes.begin();

	/*define variables*/
	NODE* p_curr_node;
	string curr_node_name;
	list<EDGE*> out_edge_list;
	list<EDGE*>::iterator e_it;
	EDGE* p_edge;
	EDGE_TYPE e_type;
	string d_block_name;
	list<string> curr_path;

	NODE* p_c_node;//child node pointer
	map<string, bool> nodes_visited;

	waiting_nodes.push(p_first_node);
	stored_paths.push(curr_path);

//cout<<"1"<<endl;

	while(!waiting_nodes.empty()){

//cout<<"new while loop"<<endl;
//cout<<"2"<<endl;
		p_curr_node = waiting_nodes.top();
		waiting_nodes.pop();
		curr_node_name = p_curr_node->get_node_name();
//cout<<"current node: "<<curr_node_name<<endl;
		curr_path = stored_paths.top();
		stored_paths.pop();

		/*reach visited node*/
		if(nodes_visited.find(curr_node_name)!=nodes_visited.end()){
//cout<<"Visited: ";
            
            //see if it's already on current path
			list<string>::iterator it;
            for(it=curr_path.begin(); it!=curr_path.end(); it++){
                if((*it)==curr_node_name){
                    break;
                }
            }

            /*if this visited node IS on current path, no need to explore, leave it for find_loop*/
            if(it!=curr_path.end()){
//cout<<"ON"<<endl;
//cout<<"one path found "<<curr_path.size()<<" :loop"<<endl; 
                continue;
            }
            /*if not on current path: the node was visited by other, so still need to explore by current path*/
            else{
/*WHAT IS WRONG ? */
//cout<<"NOT ON"<<endl;
                curr_path.push_back(curr_node_name);
            }
        }
        else{ //unvisited node
//cout<<"NOT Visited"<<endl;
			nodes_visited.insert( pair<string,bool>(curr_node_name,true) );
            curr_path.push_back(curr_node_name);
        }
        
//cout<<"3"<<endl;

        /*get the out edges list of the current node*/

        out_edge_list = p_curr_node->get_out_edge_list();
		/*if there is no outgoing edges, the end, store path */
		if(out_edge_list.empty()){
//cout<<"one path found "<<curr_path.size()<<" :empty out edges "<<p_curr_node->get_node_name()<<" size: "<<out_edge_list.size()<<endl;            
            print_seq_to_file(curr_path);
            continue;
		}
		
//cout<<"4"<<endl;

        bool has_edge = false;//for the checking if all edges point out of function
		/*iterate all the outgoing edges*/
		for (e_it = out_edge_list.begin(); e_it != out_edge_list.end(); e_it++){
            
		    p_edge = (*e_it);
		    d_block_name = p_edge->get_d_block();
		
		    if(d_block_name=="ffffffffffffffff"){
                has_edge = true;
//cout<<"one path found "<<curr_path.size()<<" :out to ffffff"<<endl; 
                print_seq_to_file(curr_path);
			    continue;
		    }

            /*point out of function , not in the source node list*/
    		if(get_node_by_name(d_block_name)==NULL){                
                continue;
			}
		    
    	    p_c_node = (nodes_map.find(d_block_name))->second;
	        /*store unvisited node pointers and their paths*/
  	        waiting_nodes.push(p_c_node);
            stored_paths.push(curr_path);
            has_edge = true;
        }
        if(has_edge==false){
            /*all out going edges*/
//cout<<"one path found "<<curr_path.size()<<" :all out edge"<<endl;            
            print_seq_to_file(curr_path);
        }
    }//end while
}

void FUNCTION::find_loop(map<string, FUNCTION*>& functions_map, map<string, NODE*>& nodes_map){
    stack<NODE*> waiting_nodes;
    stack< list<string> > stored_paths;
    if(nodes.empty()){
        cout<<"function with empty nodes list"<<endl;
        return;
    }

    NODE* p_first_node = *nodes.begin();
    NODE* p_curr_node;
    string curr_node_name;
    list<EDGE*> out_edge_list;
    list<EDGE*>::iterator e_it;
    EDGE* p_edge;
    EDGE_TYPE e_type;
    string d_block_name;
    list<string> curr_path;
    list<string> node_loop;

    NODE* p_c_node;
    map<string,bool> nodes_visited;

    waiting_nodes.push(p_first_node);
    stored_paths.push(curr_path);

    while(!waiting_nodes.empty()){
        /*get the current node and the path before it*/
        p_curr_node = waiting_nodes.top();
        waiting_nodes.pop();
        curr_node_name = p_curr_node->get_node_name();
        curr_path = stored_paths.top();
        stored_paths.pop();

        /* reach visited node */
        if(nodes_visited.find(curr_node_name)!=nodes_visited.end()){
            /* see if this visited node is on current path */
            list<string>::iterator it;
            for(it=curr_path.begin(); it!=curr_path.end(); it++){
                if((*it)==curr_node_name){
                    break;
                }
            }

            /*if this visited node IS on current path, we find a loop!*/
            if(it!=curr_path.end()){
                while(it!=curr_path.end()){
                    node_loop.push_back((*it));
                    it++;
                }

                print_loop_to_file(node_loop);

                continue;
            }
            else{//not on current path
                curr_path.push_back(curr_node_name);
            }

        }
        else{
            nodes_visited.insert( pair<string,bool>(curr_node_name,true) );
            /*store current node*/
            curr_path.push_back(curr_node_name);
        }

        out_edge_list = p_curr_node->get_out_edge_list();
        if(out_edge_list.empty()){
            continue;
        }

        for(e_it = out_edge_list.begin(); e_it != out_edge_list.end(); e_it++){
            p_edge = (*e_it);
            d_block_name = p_edge->get_d_block();

            if(get_node_by_name(d_block_name)==NULL){
                continue;
            }
            p_c_node = (nodes_map.find(d_block_name))->second;

            waiting_nodes.push(p_c_node);
            stored_paths.push(curr_path);
        }
    }
   
}



void FUNCTION::construct_loop_map(map<string, FUNCTION*>& functions_map, map<string, NODE*>& nodes_map, map<string, bool>& loops_map){
    
    stack<NODE*> waiting_nodes;
    stack< list<string> > stored_paths;
    if(nodes.empty()){
        cout<<"function with empty nodes list"<<endl;
        return;
    }

    NODE* p_first_node = *nodes.begin();
    NODE* p_curr_node;
    string curr_node_name;
    list<EDGE*> out_edge_list;
    list<EDGE*>::iterator e_it;
    EDGE* p_edge;
    EDGE_TYPE e_type;
    string d_block_name;
    list<string> curr_path;
    list<string> node_loop;

    NODE* p_c_node;
    map<string,bool> nodes_visited;

    waiting_nodes.push(p_first_node);
    stored_paths.push(curr_path);

    while(!waiting_nodes.empty()){
        /*get the current node and the path before it*/
        p_curr_node = waiting_nodes.top();
        waiting_nodes.pop();
        curr_node_name = p_curr_node->get_node_name();
        curr_path = stored_paths.top();
        stored_paths.pop();

        /* reach visited node */
        if(nodes_visited.find(curr_node_name)!=nodes_visited.end()){

            //continue;

            /* see if this visited node is on current path */
            list<string>::iterator it;
            for(it=curr_path.begin(); it!=curr_path.end(); it++){
                if((*it)==curr_node_name){
                    break;
                }
            }

            /*if this visited node IS on current path, we find a loop!*/
            if(it!=curr_path.end()){

                /*print or record detected loop*/
                /*
                while(it!=curr_path.end()){
                //    node_loop.push_back((*it));

                    cout<<(*it)<<" ";
                    it++;
                }
                cout<<endl;
                

                /*store the loop entry node to loops_map*/
                /*
                if(loops_map.find(curr_node_name)==loops_map.end() ){
                    cout<<"store loop entry: "<<curr_node_name<<endl;
                    loops_map.insert( pair<string,bool>(curr_node_name,true) );
                }
                */

                continue;
            }
            else{//this node not on current path
                curr_path.push_back(curr_node_name);
            }

        }
        else{
            nodes_visited.insert( pair<string,bool>(curr_node_name,true) );
            /*store current node*/
            curr_path.push_back(curr_node_name);
        }

        out_edge_list = p_curr_node->get_out_edge_list();
        if(out_edge_list.empty()){
            continue;
        }

        for(e_it = out_edge_list.begin(); e_it != out_edge_list.end(); e_it++){
            p_edge = (*e_it);
            d_block_name = p_edge->get_d_block();


            if(get_node_by_name(d_block_name)==NULL){
                continue;
            }
            p_c_node = (nodes_map.find(d_block_name))->second;

            waiting_nodes.push(p_c_node);
            stored_paths.push(curr_path);
        }


        //print current node that has been visited 
//        cout<<curr_node_name<<" "<<waiting_nodes.size()<<endl;
    }

}


void FUNCTION::print_seq_to_file(list<string>& seq){
	ofstream f;
	f.open("data/paths",ios::app);

    list<string>::iterator p_it;
	for(p_it = seq.begin(); p_it!= seq.end(); p_it++){
		f<<"->"<<(*p_it);
	}
	f<<endl;
    f.close();
}

void FUNCTION::print_loop_to_file(list<string>& seq){
	ofstream f;
	f.open("data/loops",ios::app);

    list<string>::iterator p_it;
	for(p_it = seq.begin(); p_it!= seq.end(); p_it++){
		f<<"->"<<(*p_it);
	}
	f<<endl;
    f.close();
}

void FUNCTION::shrink_function(map<string, FUNCTION*>& functions_map, map<string, NODE*>& nodes_map){
    //count the in and out degree of each node
    if(nodes.empty()){
        cout<<"function with empty nodes list"<<endl;
        return;
    }

    //start from here

    string d_block_name;
    NODE* p_c_node;
    EDGE* p_edge;
    EDGE_TYPE e_type;

    list<EDGE*> out_edge_list;
    list<EDGE*>::iterator e_it;
    list<NODE*>::iterator n_it;

    for(n_it = nodes.begin(); n_it != nodes.end(); n_it++ ){//initialize all degrees to 0
        (*n_it)->in_degree = 0;
        (*n_it)->out_degree = 0;
        (*n_it)->of_interest = true;
    }


    for(n_it = nodes.begin(); n_it != nodes.end(); n_it++ ){//assign destination node's incoming degree
        
        out_edge_list = (*n_it)->get_out_edge_list();
        
        for( e_it = out_edge_list.begin(); e_it != out_edge_list.end(); e_it++){
            p_edge = (*e_it);
            d_block_name = p_edge->get_d_block();

            if(get_node_by_name(d_block_name)==NULL){
                //out going edge
                //cout<<"outgoing edge"<<endl;
                continue;
            }
            p_c_node = (nodes_map.find(d_block_name))->second;
            p_c_node->in_degree++;
        }

    }

    for(n_it = nodes.begin(); n_it != nodes.end(); n_it++){//assign current node's out going degree

        out_edge_list = (*n_it)->get_out_edge_list();
        (*n_it)->out_degree = (int)out_edge_list.size();
        
    }


    //seperate reducable nodes
    //reducible_nodes are all except those with call or return 
    list<NODE*> reducible_nodes;
    bool reducible = true;

    for(n_it = nodes.begin(); n_it != nodes.end(); n_it++ ){
        
        reducible = true;

        out_edge_list = (*n_it)->get_out_edge_list();

        for( e_it = out_edge_list.begin(); e_it != out_edge_list.end(); e_it++){
            p_edge = (*e_it);
            e_type = p_edge->get_edge_type();
            if(e_type == CALL || e_type == RET){//if it is an unreducible node
                reducible = false;
                (*n_it)->of_interest = false;
                break;
            }
        }

        if(reducible == true){//add to reducible nodes' list
            reducible_nodes.push_back( (*n_it) );
        }
        
    }

    //assign parent pointer, single only
    
    for(n_it = nodes.begin(); n_it != nodes.end(); n_it++){

        out_edge_list = (*n_it)->get_out_edge_list();

        for( e_it = out_edge_list.begin(); e_it != out_edge_list.end(); e_it++){
            p_edge = (*e_it);
            d_block_name = p_edge->get_d_block();

            if(get_node_by_name(d_block_name)==NULL){
                //out going
                continue;
            }

            p_c_node = (nodes_map.find(d_block_name))->second;

            if(p_c_node->in_degree == 1){//if this child node has only one parent, then assign the parent pointer
                p_c_node->parent = (*n_it);
                
            }
            else{
                p_c_node->parent = NULL;
            }

        }

    }
  
/*
    //print in and out degree of each node

    for(n_it = nodes.begin(); n_it != nodes.end(); n_it++){
        if((*n_it)->in_degree!=10){
            cout<<(*n_it)->in_degree<<" "<<(*n_it)->out_degree<<" "<<(*n_it)->parent<<endl;
        }
    }
*/ 

    //To Do: shrink the graph of a function

    int reducible_nodes_size = reducible_nodes.size();

//    cout<<"reducible nodes: "<<reducible_nodes_size<<endl;

    bool need_reduction = true;

    while(need_reduction){

        need_reduction = false;

        cout<<"one round************************************************************************"<<endl;

        for(n_it = reducible_nodes.begin(); n_it != reducible_nodes.end(); n_it++){//check each node in the reducible list

            //p is the pointer to the current node 
            NODE* p = (*n_it);
            if(p->in_degree == 1 && p->of_interest == true){//if single parent (and does not call or return)
                
                out_edge_list = p->get_out_edge_list();
                
                //update children's parent pointer
                for(e_it = out_edge_list.begin(); e_it != out_edge_list.end(); e_it++){
                    p_edge = (*e_it);
                    d_block_name = p_edge->get_d_block();
                    
                    if(get_node_by_name(d_block_name)==NULL){
                        continue;
                    }

                    p_c_node = (nodes_map.find(d_block_name))->second;

                    p_c_node->parent = p->parent;
                }
                
                //cout<<"OK1"<<endl;
                //update parent's children, out_edge_list
                NODE* parent_node = p->parent;
                //To Do:
                //parent should not clear its edge, it just needs to add new edges
                
                list<EDGE*> original_p_edges = parent_node->get_out_edge_list();
                parent_node->clear_out_edge();

                //add p's children to parent
                for(e_it = out_edge_list.begin(); e_it != out_edge_list.end(); e_it++){
                    p_edge = (*e_it);
                    EDGE* p_new_edge = new EDGE(parent_node->get_node_name(), p_edge->get_d_block(), "MODIFIED");
                    parent_node->add_out_edge(p_new_edge);
                }

                //add parent's own edges, except one that pointing to current node
                for(e_it = original_p_edges.begin(); e_it != original_p_edges.end(); e_it++){
                    p_edge = (*e_it);
                    d_block_name = p_edge->get_d_block();
                    if( d_block_name != p->get_node_name() ){
                        parent_node->add_out_edge( p_edge );
                    }
                }

    
                //cout<<"OK2"<<endl;
                //To Do: remove node and update degrees
                cout<<"one node removed, and parent degree added: "<<(p->out_degree -1)<<endl;
                parent_node->out_degree += (p->out_degree - 1);

                //give removed nodes minus degree
                p->in_degree = -1;
                p->out_degree = -1;

                p->of_interest = false;


                if(parent_node->out_degree == 1){//single child
                    //do nothing?
                }
                else{//multiple children, needs possible merging of edges after reduction
                    list<EDGE*> before_edges = parent_node->get_out_edge_list();
                    list<EDGE*> after_edges;
                    list<EDGE*>::iterator b_e_it, a_e_it;
                    //get only edges with unique destinations 
                    for(b_e_it = before_edges.begin(); b_e_it != before_edges.end(); b_e_it++){
                        bool already_in = false;
                        for(a_e_it = after_edges.begin(); a_e_it != after_edges.end(); a_e_it++){
                            if( (*b_e_it)->get_d_block() == (*a_e_it)->get_d_block() ){
                                //find duplicate edge
                                already_in = true;
                                //need to update destination node's indegree
                                NODE* p_duplicate = get_node_by_name( (*b_e_it)->get_d_block() );
                                p_duplicate->in_degree = p_duplicate->in_degree - 1;
                            }
                        }
                        if(!already_in){
                            after_edges.push_back( (*b_e_it) );
                        }
                        
                    }

                    parent_node->clear_out_edge();
                    for( a_e_it = after_edges.begin(); a_e_it != after_edges.end(); a_e_it++){
                        parent_node->add_out_edge( (*a_e_it) );
                    }

                    cout<<before_edges.size()<<" "<<after_edges.size()<<endl;
                    
                }

                //check if pointing to one self

                if(parent_node == parent_node->parent){
                    cout<<"POINTING TO ONE SELF"<<endl;
                }

                //some node is reduced, need another round
                need_reduction = true;
            }
        }
    }
    
}


void FUNCTION::print_function_cfg(){
    cout<<function_name<<endl;
    
    ofstream f;

    cout<<"opening cfg file"<<endl;
    f.open("example",ios::app);

    f<<"digraph G {"<<endl;
    f<<"\t"<<"\""<<function_entry_node<<"\""<<"[shape=box, color=black, label=\""<<function_name<<"\"]"<<endl;

    list<EDGE*> out_edge_list;
    list<EDGE*>::iterator e_it;
    list<NODE*>::iterator n_it;

    for(n_it = nodes.begin(); n_it!= nodes.end(); n_it++){
        NODE* p = (*n_it);
        if(p->in_degree<0)continue;
        list<EDGE*> out_edge_list = p->get_out_edge_list();
        for(e_it = out_edge_list.begin(); e_it != out_edge_list.end(); e_it++){
            EDGE* p_edge = (*e_it);
            EDGE_TYPE e_type = p_edge->get_edge_type();
            f<<"\t\t"<<"\""<<p_edge->get_s_block()<<"\" -> \""<<p_edge->get_d_block()<<"\"";
            if(e_type == CALL){
                f<<"[color=blue]"<<endl;
            }
            else{
                f<<endl;
            }
        }
    }


    f<<"}"<<endl;
    f.flush();
    f.close();

}


void FUNCTION::mark_call(){
    list<NODE*>::iterator n_it;
    list<EDGE*> out_edge_list;
    list<EDGE*>::iterator e_it;
    EDGE* p_edge;
    EDGE_TYPE e_type;
    NODE* p_node;
    
    for(n_it = nodes.begin(); n_it != nodes.end(); n_it++){
        //mark if a node makes call, (exclude call to ffffffffffffffff)
        p_node = (*n_it);
        out_edge_list = p_node->get_out_edge_list();
        for(e_it = out_edge_list.begin(); e_it != out_edge_list.end(); e_it++){
            p_edge = (*e_it);
            e_type = p_edge->get_edge_type();
            string child_name = p_edge->get_d_block();
            //call ffffffffffffffff does not count
            if(e_type == CALL && child_name != "ffffffffffffffff"){
                p_node->is_call = true;
	        	p_node->call = p_edge->get_d_block();
                break;
            }
        }


        //count child_num: 
    	p_node->child_num = out_edge_list.size();
        for(e_it = out_edge_list.begin(); e_it != out_edge_list.end(); e_it++){
            p_edge = (*e_it);
            e_type = p_edge->get_edge_type();
    	    string child_name = p_edge->get_d_block();
            //exclude calls and returns and meaningless child
            if(e_type == CALL || e_type == RET){
                p_node->child_num--;
            }
            else if(child_name == "ffffffffffffffff"){
                p_node->child_num--;
            }
            else if(get_node_by_name(child_name) == NULL){
                p_node->child_num--;
            }
        }
    }
}


void FUNCTION::extract_function_visit(NODE* n){

    list<EDGE*> out_edge_list;
    list<EDGE*>::iterator e_it;
    string curr_node_name = n->get_node_name();
//    cout<<"at: "<<curr_node_name<<endl;
    //if node n has not been visited, i.e. not in the map
    if(top_nodes_visited.find(curr_node_name) == top_nodes_visited.end()) {
//        cout<<"mark as visited: "<<curr_node_name<<endl;
        top_nodes_visited.insert( pair<string,bool>(curr_node_name,true));
        out_edge_list = n->get_out_edge_list();

        for(e_it = out_edge_list.begin(); e_it != out_edge_list.end(); e_it++){
            EDGE* p_edge = (*e_it);
            EDGE_TYPE e_type = p_edge->get_edge_type();
            if(e_type == CALL || e_type == RET)continue;
            string child_name = p_edge->get_d_block();
            if(child_name == "ffffffffffffffff")continue;
            NODE* p_child = get_node_by_name(child_name);
            //if child_name not in get_node_by_name, then it is not in nodes (should be in des_nodes).
            if(p_child == NULL){
                continue;
                p_child = get_des_node_by_name(child_name);
            }

            //cout<<"about to visit: "<<child_name<<endl;   //cl de-canceled
            extract_function_visit(p_child);
        }

        //cout<<"put in top order: "<<curr_node_name<<endl;  //cl de-canceled
        top_stack.push(n);
        top_queue.push(n);
    }
}


void FUNCTION::extract_function( map<string, FUNCTION*> &functions_map ){
    mark_call();
    cout<<"mark call done!"<<endl;

    NODE* p_first_node;
    list<EDGE*> out_edge_list;
    list<EDGE*>::iterator e_it;
    EDGE* p_edge;
    EDGE_TYPE e_type;

    //step 1: toplogical sort, ignoring loop, OK?
    //first node is the only one which has no incoming edges.
    
    //ONE NODE CASE
    if(nodes.empty()){   //cl, in cfg.dot, there is no empty nodes.
        //out matrix to file and return, no need to go through the analysis

    	cout<<"in FUNCTION::extract_function, nodes.empty(): "<<endl;  //cl add

        ofstream f;
        f.open("transi-prob",ios::app);
        f<<"NULL extracting information from function: "<<function_name<<endl;
        f<<"NULL"<<endl;
        f<<1<<endl;
        f.close();
        return;
    }

    //p_first_node = *nodes.begin();
    p_first_node = get_node_by_name(function_entry_node);   //cl add, the entry node of this function.
    //set in GRAPH::read_cfg, p_function->set_function_entry_node(function_entry_node)
    extract_function_visit(p_first_node);

    cout<<"step 1 done! : toplogical sorting"<<endl;   
    stack<NODE*> tmp_top_stack = top_stack;
    queue<NODE*> tmp_top_queue = top_queue;

    /*
    while(!tmp_top_stack.empty()){
        cout<<(tmp_top_stack.top())->get_node_name()<<endl;
        tmp_top_stack.pop();
    }
    tmp_top_stack = top_stack;
    */

    //step 2: assign p: the probability to reach each node from function entry
    map<string, bool> nodes_expanded;

    double total_p = 1.0;
    p_first_node = tmp_top_stack.top();
    p_first_node->p = total_p;
    while(!tmp_top_stack.empty()){
        NODE* p_curr_node = tmp_top_stack.top();
        tmp_top_stack.pop();
        //mark node expanded
        string curr_node_name = p_curr_node->get_node_name();
        nodes_expanded.insert( pair<string,bool>(curr_node_name,true) );

        out_edge_list = p_curr_node->get_out_edge_list();
        int e_num = out_edge_list.size();
        //if no outgoing edges
        if(e_num == 0)continue;

    	//child edge num(already filtered during mark_call, but may still contain back edge(loop)
	    int c_num = p_curr_node->child_num;
//        cout<<"c_num is: "<<c_num<<endl;

        for(e_it = out_edge_list.begin(); e_it != out_edge_list.end(); e_it++){
            p_edge = (*e_it);
            string child_name = p_edge->get_d_block();
            e_type = p_edge->get_edge_type();
            //exclude child already expanded
            if(nodes_expanded.find(child_name)!=nodes_expanded.end()&&e_type!=CALL&&e_type!=RET&&child_name!="ffffffffffffffff"&&get_node_by_name(child_name)!=NULL){
                c_num--;
            }
        }

        //if no meaningful child lefe after excluding back edge
        if(c_num == 0)continue;

        //divide the probability then pass down to children
        double c_p = p_curr_node->p/c_num;

        //cout<<"function: "<< function_name <<", curr_node: "<<curr_node_name<<", divisions: "<<c_num <<endl;  //cl add


        for(e_it = out_edge_list.begin(); e_it != out_edge_list.end(); e_it++){
            p_edge = (*e_it);
            e_type = p_edge->get_edge_type();
            if(e_type == CALL || e_type == RET)continue;
            string child_name = p_edge->get_d_block();
            if(child_name == "ffffffffffffffff")continue;
            if(nodes_expanded.find(child_name)!=nodes_expanded.end())continue;
            NODE* p_child = get_node_by_name(child_name);
            if(p_child == NULL){
                continue;
                p_child = get_des_node_by_name(child_name);
            }
            p_child->p += c_p;   //cl question, here should be "times"? not "plus"???
            //cout<<"function: "<< function_name <<", child: "<<child_name <<", probability "<< p_child->p <<endl;  //cl add
        }
    }
    tmp_top_stack = top_stack;


    /*
    while(!tmp_top_stack.empty()){
        NODE* p_curr_node = tmp_top_stack.top();
        tmp_top_stack.pop();
        cout<<p_curr_node->get_node_name()<<": "<<p_curr_node->p<<endl;
    }
    tmp_top_stack = top_stack;
    */

    cout<<"step 2 done! : assign probability"<<endl;


    //step 3: build immediate call sets (ICSs)

    /*
    while(!tmp_top_stack.empty()){
        NODE* p_curr_node = tmp_top_stack.top();
        tmp_top_stack.pop();
        cout<<p_curr_node->is_call<<endl;
    }
    tmp_top_stack = top_stack;
    */
    
    //for re-use
    nodes_expanded.clear();
    map< string, list<CALL_PROB> > ICS_map;

    //Our algorithm finds the immediate call set (ICS) for each node within
    //a CFG. The immediate call set (ICS) consists of calls that are immediately following the
    //owner node with no other calls in between. Each node n has an ICS nICS which is a set,
    //and each element within the set is represented as a pair < cj, Pnj t >, where cj is one of the
    //immediate calls of node n and P t
    //nj is the corresponding transition probability from node n
    //to the immediate call cj.



    while(!tmp_top_queue.empty()){
        NODE* p_curr_node = tmp_top_queue.front();
        tmp_top_queue.pop();
        string curr_node_name = p_curr_node->get_node_name();
	    cout<<"build ICS for node: "<<curr_node_name<<endl;  //cl de-cancel
        
        out_edge_list = p_curr_node->get_out_edge_list();
        int e_num = out_edge_list.size();
        int c_num = p_curr_node->child_num;
	    cout<<"initial e_num and c_num: "<<e_num<<"\t"<<c_num<<endl; //cl de-cancel
		
        for(e_it = out_edge_list.begin(); e_it != out_edge_list.end(); e_it++){
    	    p_edge = (*e_it);
	        e_type = p_edge->get_edge_type();
            string child_name = p_edge->get_d_block();

            //back edge do not count for branch prob computation
            if(nodes_expanded.find(child_name) == nodes_expanded.end()&&e_type!=CALL&&e_type!=RET&&child_name!="ffffffffffffffff"&&get_node_by_name(child_name)!=NULL){
		    	cout<<"child not expanded yet: "<<child_name<<endl; //cl de-cancel
	        	c_num--;
            }
        }
	    cout<<"node: "<<curr_node_name<<endl;  //cl de-cancel
	    cout<<"after c_num: "<<c_num<<endl;  //cl de-cancel
	
	    //e_num=0: has no out edge;	c_num=0: besides call and ret and back edge, has no out edge
        if(e_num == 0 || c_num == 0){
    	    //construct an NULL CALL_PROB
	        CALL_PROB* p_call_prob = new CALL_PROB();
	        p_call_prob->call_name = "NULL";
	        p_call_prob->prob = p_curr_node->p;
	        list<CALL_PROB> *p_call_prob_list = new list<CALL_PROB>;
	        p_call_prob_list->push_back(*p_call_prob);  //cl: why use a p_call_prob_list, the size always one????
	        ICS_map.insert( pair< string, list<CALL_PROB> >(curr_node_name, *p_call_prob_list) );
            nodes_expanded.insert( pair<string,bool>(curr_node_name,true) );
    		cout<<"e_num or c_num is 0"<<endl;  //cl de-cancel
	        continue;
        }
	
        //check and merge each child
        double branch_p = 1.0/c_num;

    	cout<<"check and merge each child... "<<endl;   //cl de-cancel

        list<CALL_PROB> *p_call_prob_list = new list<CALL_PROB>;
	    list<CALL_PROB>::iterator ics_it, c_ics_it;

	    for(e_it = out_edge_list.begin(); e_it != out_edge_list.end(); e_it++){
	        p_edge = (*e_it);
    	    e_type = p_edge->get_edge_type();
	        if(e_type == CALL || e_type == RET){
		        cout<<"call or ret"<<endl;  //cl de-cancel
		        continue;
            }
	        string child_name = p_edge->get_d_block();
            if(child_name == "ffffffffffffffff"){
                cout<<"ffffffffffffffff"<<endl;  //cl de-cancel
                continue;
            }
	        if(nodes_expanded.find(child_name) == nodes_expanded.end()){
                cout<<"not expanded"<<endl;  //cl de-cancel
                continue;
            }
    
	        NODE* p_child_node = get_node_by_name(child_name);
            if(p_child_node == NULL){
    		cout<<"child node NULL"<<endl;   //cl de-cancel
                continue;
                p_child_node = get_des_node_by_name(child_name);
            }
            //child makes call/ is call
            if(p_child_node->is_call){
  			cout<<"ONE CHILD IS cALL!!"<<endl;  //cl de-cancel
		        //prepare the weighted call set
		        CALL_PROB* p_call_prob = new CALL_PROB();
		        p_call_prob->call_name = p_child_node->call;
		        p_call_prob->prob = ( p_child_node->p * branch_p * p_curr_node->p ) / p_child_node->p;
		        bool found = false;
		        for(ics_it = (*p_call_prob_list).begin(); ics_it != (*p_call_prob_list).end(); ics_it++){
		            //if the same call already exists
		            if( (*ics_it).call_name == p_call_prob->call_name ){
			        (*ics_it).prob += p_call_prob->prob;
			        found = true;
			        break;
                    }
                }
                if(!found){
		            p_call_prob_list->push_back(*p_call_prob);
                }
            }
            //child is not a call. merge its call set (a list)
	        else{
			cout<<"child NOT CALL"<<endl;  //cl de-cancel
		        if(ICS_map.find(child_name)==ICS_map.end())cout<<"!!!!!!child not found in ICS map!!!!!! : "<<child_name<<endl;
                //get child's ICS
		        list<CALL_PROB> c = ICS_map.find(child_name)->second;

                //iterate through all call in child's ICS
		        for(c_ics_it = c.begin(); c_ics_it != c.end(); c_ics_it++){
		            bool found = false;
		            for(ics_it = (*p_call_prob_list).begin(); ics_it != (*p_call_prob_list).end(); ics_it++){
                        //if parent has this call
        			    if( (*ics_it).call_name == (*c_ics_it).call_name ){
		        	        (*ics_it).prob += ( (*c_ics_it).prob*branch_p*p_curr_node->p/p_child_node->p );
			                found = true;
        			        break;
                        }	
                    }
                    if(!found){//if parent does not have this call
        	    		CALL_PROB tmp_call_prob;
		            	tmp_call_prob.call_name = (*c_ics_it).call_name;
        			    tmp_call_prob.prob = ((*c_ics_it).prob*branch_p*p_curr_node->p)/p_child_node->p;
		            	p_call_prob_list->push_back(tmp_call_prob);
                    }	
                }
            }
        }
        ICS_map.insert( pair< string, list<CALL_PROB> >(curr_node_name, *p_call_prob_list) );
        nodes_expanded.insert( pair<string,bool>(curr_node_name,true) );
    }
    tmp_top_queue = top_queue;

    cout<<"step 3 done! : construct ICS"<<endl;

    /*
    //print ICSs
    while(!tmp_top_stack.empty()){
        NODE* p_curr_node = tmp_top_stack.top();
        tmp_top_stack.pop();
        string curr_node_name = p_curr_node->get_node_name();
	    cout<<"curr_node_name: "<<curr_node_name<<"\t child_num: "<<p_curr_node->child_num<<"\t is_call: "<<p_curr_node->is_call<<"p: "<<p_curr_node->p<<endl;
//	    cout<<p_curr_node->is_call<<endl;
        cout<<"ICS: ";
	    list<CALL_PROB> call_prob_list = ICS_map.find(curr_node_name)->second;
	    list<CALL_PROB>::iterator ics_it;
	    for(ics_it = call_prob_list.begin(); ics_it != call_prob_list.end(); ics_it++){
	        cout<<(*ics_it).call_name<<"\t"<<(*ics_it).prob<<"\t";
	    }
	    cout<<endl;
    }
    tmp_top_stack = top_stack;
    */


    //step 4: construct transition matrix

    cout<<"start step 4"<<endl; //cl de-cancel
    call_num = 0;
    int call_index = 0;
    map<string,int> call_index_map;
    call_index_map.insert( pair<string,int>("NULL",0) );
    call_set[0] = "NULL";
    //get call_set[]
    while(!tmp_top_stack.empty()){
    	NODE* p_curr_node = tmp_top_stack.top();
	    tmp_top_stack.pop();
    	if(!p_curr_node->is_call)continue;
	    string curr_node_name = p_curr_node->get_node_name();
    	string call_name = p_curr_node->call;
	    if(call_index_map.find(call_name) == call_index_map.end()){
	        //save index 0 for environment
    	    call_index++;
	        call_index_map.insert( pair<string,int>(call_name, call_index) );
	        call_set[call_index] = call_name;
	    }
    }
    tmp_top_stack = top_stack;

    cout<<"num of calls: "<<call_index<<endl; //cl de-cancel

    //initialize matrix
    m = new double*[call_index+1];
    for(int i = 0; i <= call_index; i++){
	    m[i] = new double[call_index+1];
	    for(int j = 0; j <= call_index; j++){
	        m[i][j] = 0;
	    }
    }
    cout<<"empty matrix initialization created!"<<endl;  //cl de-cancel

    //put the transition prob into matrix
    while(!tmp_top_stack.empty()){
    	NODE* p_curr_node = tmp_top_stack.top();
        tmp_top_stack.pop();
        string curr_node_name = p_curr_node->get_node_name();
	    if(!p_curr_node->is_call)continue;
	    cout<<"find a call node name: "<< curr_node_name <<endl;  //cl de-cancel
	    string from_call = p_curr_node->call;
	    int index_from = call_index_map.find(from_call)->second;

    	list<CALL_PROB> call_prob_list = ICS_map.find(curr_node_name)->second;
    	list<CALL_PROB>::iterator ics_it;

    	for(ics_it = call_prob_list.begin(); ics_it != call_prob_list.end(); ics_it++){
	        string to_call = (*ics_it).call_name;
	        int index_to = call_index_map.find(to_call)->second;
    	    m[index_from][index_to] += (*ics_it).prob;
	    }
    }
    tmp_top_stack = top_stack;


    cout<<"before first row"<<endl; // cl de-cancel
    //first row
    string from_call = "NULL";
    if(p_first_node->is_call){//if first node makes call
	cout<<"1"<<endl;
        string to_call = p_first_node->call;
        int index_to = call_index_map.find(to_call)->second;
        m[0][index_to] += 1;
    }
    else{// if first node does not make call
	//cout<<"2"<<endl;
	cout<<function_entry_node<<endl;
        list<CALL_PROB> call_prob_list = ICS_map.find(function_entry_node)->second;
        list<CALL_PROB>::iterator ics_it;
	//cout<<"2.1"<<endl;
        for(ics_it = call_prob_list.begin(); ics_it != call_prob_list.end(); ics_it++){
	        string to_call = (*ics_it).call_name;
        	int index_to = call_index_map.find(to_call)->second;
        	m[0][index_to] += (*ics_it).prob;
        }
    }
    cout<<"step 4 done! : construct transition matrix"<<endl;

    //out matrix to file
    //cout<<"start output matrix to file"<<endl;
    ofstream f;
    f.open("transi-prob",ios::app);
    f<<"extracting information from function: "<<function_name<<endl;
    for(int i = 0; i <= call_index; i++){
        if(i == 0){
            f<<call_set[i]<<"\t";
        }
        else{
            f<<functions_map.find(call_set[i])->second->get_function_name()<<"\t";
        }
    }
    f<<endl;

    for(int i =0; i <= call_index; i++){
    	for(int j = 0; j <= call_index; j++){
	        f<<m[i][j]<<"\t";
	    }
	    f<<endl;
    }
    f.close();
	
    cout<<"all done!"<<endl<<endl;
}
