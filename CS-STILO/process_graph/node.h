/*node.h*/
#include <string>
#include <list>
enum NODE_TYPE {FUNC_ENTRY, NORMAL};

class EDGE;


class NODE {
private:
	std::string node_name;
	NODE_TYPE node_type;
	std::list<EDGE*> out_edges;	

public:	

    NODE* parent;

	void set_node_name(std::string name);
	void set_node_type(NODE_TYPE type);
	
	std::string get_node_name();
	NODE_TYPE get_node_type();

	std::list<EDGE *>& get_out_edge_list();
	
	
	void add_out_edge(EDGE* p_edge);

    void clear_out_edge();

	void print_node();
	void print_out_edge_list();

    int in_degree;
    int out_degree;
    bool of_interest;

    double p;
    bool is_call;
    std::string call;    

    int child_num;

	NODE(){
        p = 0;
        is_call = false;
    }
	~NODE(){}

};

