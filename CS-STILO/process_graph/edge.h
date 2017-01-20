/* edge.h */
#include <string>
#include <iostream>

enum EDGE_TYPE {CALL, COND_TAKEN, COND_NOT_TAKEN, INDIRECT, DIRECT, FALLTHROUGH, CATCH, CALL_FT, RET, MODIFIED};

class NODE;

class EDGE {
private:
	std::string s_block;
	std::string d_block;
	EDGE_TYPE edge_type;

public:
	void set_s_block(std::string s);
	void set_d_block(std::string d);
	void set_edge_type(EDGE_TYPE type);

	std::string get_s_block();
	std::string get_d_block();
	EDGE_TYPE get_edge_type();

	void print_edge();
	
	EDGE(std::string source, std::string destination, std::string type);
	EDGE(){}
	~EDGE(){}
};
