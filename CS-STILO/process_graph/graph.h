/* graph.h */
#include <list>
#include <string>
#include <iostream>
#include <fstream>

#include <map>
#include <queue>
#include <stack>

class FUNCTION;
class NODE;

class GRAPH {

private:
	std::list<FUNCTION*> functions;
	std::map<std::string, NODE*> nodes_map;
	std::map<std::string, FUNCTION*> functions_map;
    //loop map
    std::map<std::string, bool> loops_map;

	void add_function_list(FUNCTION* p_function);
	void add_function_map(std::string function_entry_node , FUNCTION* p_function);
	void add_node_map(std::string node_name, NODE* p_node);
//	void print_path(std::queue<std::string>& path, std::ofstream& f);	

public:
	
	std::list<FUNCTION*>& get_function_list();

	std::map<std::string, NODE*>& get_node_map();
	
	std::map<std::string, FUNCTION*>& get_function_map(); 


	void read_cfg(std::string cfgfile);

	void build_node_map();
	void build_function_map();
    void build_loop_map();
    
    //remove uninteresting nodes; print cfg after reduction
    void shrink_graph();
    void print_graph_cfg();

    
    //for printing info
	void print_graph();
	void print_function_map();

    //find paths and loops, not used
	void navigate();

    //extract call sequence pairs for each function
    void extract_graph();


	void get_uncalled();

	GRAPH(){};
	~GRAPH(){};
};
