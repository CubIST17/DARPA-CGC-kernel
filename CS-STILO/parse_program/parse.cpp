#include <hash_map>
#include <iostream>
#include <fstream>
#include <string>
#include <list>
#include <time.h>
#include <ctime>

/*parseAPI headers*/
//#include "/usr/include/dyninst/CFG.h"
//#include "/usr/include/dyninst/CodeObject.h"

#include "/usr/local/include/CFG.h"
#include "/usr/local/include/CodeObject.h"


using namespace std;
using namespace __gnu_cxx;
using namespace Dyninst;
using namespace Dyninst::ParseAPI;

list<string> all_sys_calls;
list<string> all_func_calls;
list<string> used_sys_calls;

int main(int argc, char* argv[]){

	clock_t startClock, finishClock;
	double timeCount;
	startClock = clock();	

	hash_map<Address, bool> seen;
	SymtabCodeSource *sts;
	CodeObject *co;
	/* read and parse a binary */
	sts = new SymtabCodeSource( argv[1] );
	co = new CodeObject( sts );
	co->parse();	
	CodeObject::funclist all_func = co->funcs();
	CodeObject::funclist::iterator fit;
	


	/* Print all found functions to file "all_functions" */
	ofstream all_func_file("all_functions");
	int func_index = 0;
    string func_call;
	for(fit = all_func.begin(); fit != all_func.end(); ++fit) {
		func_index ++;
		Function * f = *fit;
        	func_call = f->name();
		all_func_file<<"["<<func_index<<"]: "<<func_call<<endl;
        	all_func_calls.push_back(func_call);
	}
	all_func_file.close();


	//	cout<<"here"<<endl;		

	/* Print the control flow graph */
	ofstream dotfile("cfg.dot");
	dotfile<<"digraph G {\n";
	for(fit = all_func.begin(); fit != all_func.end(); ++fit) {
	
		Function * f = *fit;

		if(f->retstatus() == NORETURN){ //will not return
			dotfile<<"\t"<<"\""<< hex <<f->addr()<<"\""<<" [shape=box,color=red,label=\""<< f->name() <<"\"]"<<endl;
		}
		else if(f->retstatus() == UNKNOWN){ //cannot be determined statically
			dotfile<<"\t"<<"\""<< hex <<f->addr()<<"\""<<" [shape=box,color=yellow,label=\""<< f->name() <<"\"]"<<endl;	
		}
		else if(f->retstatus() == UNSET){ //unparsed function (default)
                        dotfile<<"\t"<<"\""<< hex <<f->addr()<<"\""<<" [shape=box,color=green,label=\""<< f->name() <<"\"]"<<endl;
                }
		else{
			dotfile<<"\t"<<"\""<< hex <<f->addr()<<"\""<<" [shape=box,color=black,label=\""<< f->name() <<"\"]"<<endl;
		}
		/* iterate all the blocks */
		Function::blocklist::iterator bit;
		for(bit=f->blocks().begin(); bit != f->blocks().end(); ++bit) {
			Block * b = *bit;

			// Don't revisit blocks in shared code
			if(seen.find(b->start()) != seen.end())
				continue;
			seen[b->start()] = true;
			
			/* iterate all outgoing edges */
			Block::edgelist::const_iterator it;

            		//block with no edge
			if( (int)b->targets().size() == 0 ){
                		
				dotfile<<"\t\t"<<"\""<<hex<<b->start()<<"\""
					<<" -> "
					<<"\"*\""<<" [color=black]"<<endl;
                		continue;
	                	cout<<"empty target"<<endl;
            		}

			for(it=b->targets().begin(); it != b->targets().end(); ++it) {
				string s = "";
				if((*it)->type() == CALL) {
//					cout<<"CALL"<<endl;
					s = " [color=blue]";
					
					std::vector<Function *> funcs;
					int num_contain_funcs = (*it)->trg()->containingFuncs();
					if (num_contain_funcs > 0 ){
//						cout<<"Number of containing functions: "<<num_contain_funcs<<endl;
						(*it)->trg()->getFuncs(funcs);
//						cout<<"containing function name: "<<(*funcs.begin())->name()<<endl;
					}
				
				}
				else if((*it)->type() == RET){  //return
//					cout<<"RET"<<endl;
					s = " [color=green]";
				}
				else if((*it)->type() == COND_TAKEN){  //conditional branch–taken
					s = " [color=pink]";
				}
				else if((*it)->type() == COND_NOT_TAKEN){  //conditional branch–not taken  ??
					s = " [color=hotpink]";
				}
				else if((*it)->type() == INDIRECT){  //branch indirect
					s = " [color=yellow]";
				}
				else if((*it)->type() == DIRECT){  //branch direct
					s = " [color=yellow4]";
				}
				else if((*it)->type() == FALLTHROUGH){   //direct fallthrough (no branch)
					s = " [color=red]";
				}
				else if((*it)->type() == CALL_FT){  //post-call fallthrough ??
					s = " [color=red4]";
				}
				else if((*it)->type() == CATCH){  //exception handler
					s = " [color=orange]";
				}

				dotfile<<"\t\t"<<"\""<<hex<<(*it)->src()->start()<<"\""
					<<" -> "
					<<"\""<<hex<<(*it)->trg()->start()<<"\""<<s<<endl;
				
			}
		}
	}
	dotfile<<"}\n";



	//delete co;
	//delete sts;
	
	finishClock = clock();
	timeCount = finishClock - startClock;
	cout<<"time used for parsing binary: "<<timeCount / (double) CLOCKS_PER_SEC<<endl;	
	
	return 0;
}

