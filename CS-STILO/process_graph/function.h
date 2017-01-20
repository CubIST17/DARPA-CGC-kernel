/* function.h */
#include <list>
#include <string>
#include <iostream>
#include <fstream>
#include <queue>
#include <stack>
#include <map>

class NODE;

enum RETURN_TYPE {UNSET, NORETURN, UNKNOWN, RETURN};

struct CALL_PROB {
    std::string call_name;
    double prob;
};

class FUNCTION {
private:
	std::string function_name;
	std::list<NODE*> nodes;
    std::list<NODE*> des_nodes;
	std::string function_entry_node;
	RETURN_TYPE function_return_type;
    //toplogical order of nodes
    std::stack<NODE*> top_stack;
    std::queue<NODE*> top_queue;
    std::map<std::string, bool> top_nodes_visited;

public:

    int call_num;
    /*over 300 system calls in total*/
    double **m;
    std::string call_set[400]; 
    


	void set_function_name(std::string name);

	void set_function_entry_node(std::string entry_node);

	void set_function_return_type(std::string ret_type);

	std::string get_function_name();

	std::string get_function_entry_node();

	RETURN_TYPE get_function_return_type();
	
	/*find all possible paths within this function*/
	void find_path(std::map<std::string, FUNCTION*> &functions_map, std::map<std::string, NODE*> &nodes_map );

	void find_loop(std::map<std::string, FUNCTION*> &functions_map, std::map<std::string, NODE*> &nodes_map );

	/*find loop entries*/
	void construct_loop_map(std::map<std::string, FUNCTION*> &functions_map, std::map<std::string, NODE*> &nodes_map, std::map<std::string, bool> &loops_map);

	std::list<NODE*> & get_node_list();

	std::list<NODE*> & get_des_node_list();

	NODE* get_node_by_name(std::string name);

	NODE* get_des_node_by_name(std::string name);

	void read_cfg(std::ifstream & f_cfg);

	void print_node_list();

	void print_seq_to_file(std::list<std::string>&);

	void print_loop_to_file(std::list<std::string>&);

	std::list<std::string> translate(std::list<std::string>&, std::map<std::string, FUNCTION*> &functions_map, std::map<std::string, NODE*> &nodes_map);

    void shrink_function( std::map<std::string, FUNCTION*> &functions_map, std::map<std::string, NODE*> &nodes_map );

    void print_function_cfg();

    void extract_function( std::map<std::string, FUNCTION*> &functions_map );

    void extract_function_visit(NODE*);

    void mark_call();

	FUNCTION(){};
	~FUNCTION(){};
};
