import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.Set;


public class TransitionMatrixProcessor {

	BufferedReader br;
	String program; 
	Map<String,String> name_map = new HashMap<String, String>();
	Map<String,Boolean> uncalled_map = new HashMap<String,Boolean>();
	Map<String,matrix> matrix_map = new HashMap<String,matrix>();
	ArrayList<String> func_list =new ArrayList<String>();
	
	public void build_transition_matrix(String app) throws IOException {
		
		long start_time = System.currentTimeMillis();
		
		program = app;
		

		
		//step 1: read in name_map, which maps system call function names to system call
		System.out.println("start read_name_map()");
		
		read_name_map();
		
		System.out.println("start read_uncalled_function()");
		
		//step 2: get all uncalled functions
		read_uncalled_function();
		
		System.out.println("start read_matrix()");
		//step 3: read in matrix for each function
		read_matrix();
		
		test_after_read_in();

		//step 4: pre-processing for recursive function:  remove the call, redistribute its prob
		pre_process_matrix();

		test_after_pre_process();

		//step 5: starting from each uncalled function, get the topological order, and then merge, print matrix to file
		process_matrix();
		
		
		//step 6:
		generate_initial_parameter();
		
		long end_time = System.currentTimeMillis();
		
		System.out.println("aggregation time: "+(end_time - start_time));
	}


	

	private void generate_initial_parameter() throws IOException {

		double[][] final_m;
		String[] final_call_set;
		
		String path= program+"/initial";
		File file=new File(path);
		File[] tempList = file.listFiles();
		
		//(1) define the weight for each uncalled function matrix
		double[] w = new double[tempList.length];
		for(int i = 0; i < tempList.length; i++){
			if(tempList[i].getName().equals("main"))w[i] = 0.95;
			else w[i] = 0.05/(tempList.length-1);
		}
		
		//(2) get final call set
		String[] curr_call_set;
		br = new BufferedReader(new FileReader(tempList[0].getPath()));
		br.readLine();
		curr_call_set = br.readLine().split(",");
		br.close();
		
		for(int i = 1; i < tempList.length; i++){
			//System.out.println(tempList[i]);
			br = new BufferedReader(new FileReader(tempList[i].getPath()));
			//System.out.println(br.readLine());
			br.readLine();
			curr_call_set = helper.call_set_union(curr_call_set, br.readLine().split(",")); 
			br.close();
		}
		
		final_call_set = curr_call_set;
		//for(int i = 0; i < curr_call_set.length; i++){
			//System.out.println(final_call_set[i]);
		//}
		
		
		final_m = new double[final_call_set.length][final_call_set.length];
		//(3)make index map for final call set
		Map<String, Integer> f_index_map = null;
		f_index_map = new HashMap<String,Integer>();
		
		for(int i = 0; i < final_call_set.length; i++){
			f_index_map.put(final_call_set[i], i);
		}
		int index_x, index_y;
		
		
		//(4)consider all matrices with weight
		for(int i = 0; i < tempList.length; i++){
			//System.out.println(tempList[i]);
			br = new BufferedReader(new FileReader(tempList[i].getPath()));
			br.readLine();
			String[] calls = br.readLine().split(",");
			for(int m = 0; m < calls.length; m++){
				String m_line = br.readLine();
				for(int n = 0; n < calls.length; n++){
					index_x = f_index_map.get(calls[m]);
					index_y = f_index_map.get(calls[n]);
					final_m[index_x][index_y] += Double.valueOf(m_line.split("\t")[n]) * w[i];
					//System.out.println(Double.valueOf(m_line.split("\t")[n]));
				}
			}
			br.close();
		}
		
		
		//(5)output complete initial parameters with all uncalled functions included
		FileWriter fstream = new FileWriter(program+"/initial-all/complete");
		BufferedWriter com = new BufferedWriter(fstream);
		
		//write the dimention
		com.append(final_call_set.length+"\n");

		String call;
		int i,j;
		//write all system calls, INcluding NULL
		for(i = 0; i < final_call_set.length; i ++){
			call = final_call_set[i];
			if(call.equals("null"))call="env";
			com.append(call+",");
		}
		com.append("\n");

		
		//write the normalized transition times
		for(i = 0; i < final_call_set.length; i++){
			double sum = 0;
			for(j = 0; j < final_call_set.length; j++){
				sum += final_m[i][j];
			}
			
			for(j = 0; j < final_call_set.length; j++){
				if(sum!=0){
					com.append(final_m[i][j]/sum + "\t");
				}
				else{
					com.append(final_m[i][j] + "\t");
				}
			}
			com.append("\n");
		}
		
		//write the initial distribution pi
		double[] pi = new double[final_call_set.length];
		for( i = 0; i < final_call_set.length; i++){
			double sum = 0;
			for( j = 0; j < final_call_set.length; j++)sum+=final_m[j][i];
			pi[i] = sum;
		}
		double total_pi = 0;
		for( i = 0; i < final_call_set.length; i++)total_pi+=pi[i];
		for( i = 0; i < final_call_set.length; i++){
			com.append((pi[i]/total_pi) + "\t");
		}
		com.append("\n");
		com.flush();
		com.close();
		
		//System.out.print(final_call_set.length);
		//for(i = 0; i < final_call_set.length; i++){
			//System.out.println(i+"\t"+final_call_set[i]+"$");
			
			//System.out.print("\n");
			
		//}
		
	}


	private void process_matrix() throws IOException {

		HashMap<String,Boolean> visited = new HashMap<String,Boolean>();
		Queue<String> que = new LinkedList<String>();
		HashMap<String, Boolean> global_processed = new HashMap<String,Boolean>();

		
		for(int i = 0; i < func_list.size(); i++){
			//merge the matrix for each uncalled function
			if(!uncalled_map.containsKey(func_list.get(i)))continue;
			//let's first only looks at main
			//if(!func_list.get(i).equals("tdelete"))continue;
			visited.clear();
			que.clear();

			matrix start_m = matrix_map.get(func_list.get(i));

			topoVisit(start_m, visited, que, matrix_map);
			
			//System.out.println(que.size());
			//System.out.println(que.toString());

			//print_matrix("tdelete",matrix_map);

			System.out.println("START MERGING MATRIX for "+func_list.get(i));
			//HashMap<String, Boolean> processed = new HashMap<String,Boolean>();
			while (!que.isEmpty()){
				String curr_func_name = que.poll();
				//System.out.println("process: "+curr_func_name);
				
				if(global_processed.containsKey(curr_func_name)){
					//this function already processed at other queue
					continue;
				}
				
				matrix curr_m = matrix_map.get(curr_func_name);
				
				if((name_map.containsKey(curr_func_name) && curr_m.make_call && !helper.child_make_syscall(curr_func_name, matrix_map, name_map))){
					//if curr is already a syscall
					//even if it makes calls, no need to merge
					global_processed.put(curr_func_name, true);
					continue;
				}
				
				boolean finished = false;
				//			int test = 0;
				while(!finished){			
					
					finished = true;
					String[] curr_call_set = curr_m.call_set;
					//skip j = 0, NULL
					int j = 1;
					for (; j < curr_call_set.length; j++){
						//System.out.println("des_call: "+des_call);
						//if(curr_call_set[j].equals("__sigaction")){
							//System.out.println("__sigaction is callee");
						//}
						String des_call = helper.strip_at(curr_call_set[j]);
						matrix des_m = matrix_map.get(des_call);
						//if (des_m.function_name.equals("__sigaction"))System.out.println("**************found\n");
						//if((!des_m.make_call)||name_map.containsKey(des_m.function_name)){
						if((!des_m.make_call)||(name_map.containsKey(des_call)&&des_m.make_call&&!helper.child_make_syscall(des_call, matrix_map, name_map))){
							//if it does not make call, then nothing to be merged, this includes the obvious system calls
							//if (1) there is a map des_call -> syscall, (2)it still makes calls  (3)but none of the calls is syscall, then itself is a syscall, no need to further merge

						}
						else{
							//need to merge this call to current matrix
							if(!global_processed.containsKey(des_m.function_name))continue;
							//if (des_m.function_name.equals("__sigaction"))System.out.println("**************?\n");

							//System.out.println("Merge: "+curr_func_name+" and "+des_call);
							//print_matrix_direct(curr_m, matrix_map);
							//print_matrix_direct(des_m, matrix_map);
							curr_m = merge(curr_m, des_m, matrix_map);
							//print_matrix_direct(curr_m, matrix_map);
							curr_m.normalize();
							matrix_map.put(curr_func_name, curr_m);

							//print_matrix(curr_m.function_name, matrix_map);
							//System.exit(0);
							/*
							if(test<4){
								print_matrix(curr_m.function_name, matrix_map);
								test++;
							}
							else{
								print_matrix(curr_m.function_name, matrix_map);
								System.exit(0);
							}
							 */
							finished = false;
							break;
						}
					}
				}
				//processed.put(curr_func_name, true);
				global_processed.put(curr_func_name, true);
				//print_matrix(curr_func_name,matrix_map);
			}


			/*
			//check matrix property after merge
			for ( int i = 0; i < func_list.size(); i++){
				String curr_func_name = func_list.get(i);
				matrix curr_m = matrix_map.get(curr_func_name);
				check_matrix(curr_m);
				normalize(curr_m, matrix_map);
			}
			System.out.println("after merge, matrix checked");
			System.exit(0);
			 */

			//print_matrix("tdelete",matrix_map);
			to_syscall_matrix(func_list.get(i), matrix_map, name_map);

		}		
	}

	private void test_after_pre_process() {
		/*
		//check matrix property after pre-processing
		for ( int i = 0; i < func_list.size(); i++){
			String curr_func_name = func_list.get(i);
			matrix curr_m = matrix_map.get(curr_func_name);
			check_matrix(curr_m);
			normalize(curr_m, matrix_map);
		}
		System.out.println("after pre-processing, matrix checked");
		System.exit(0);
		 */

		//String test_m = "_nl_find_msg";
		//print_matrix(test_m, matrix_map);		
	}

	private void pre_process_matrix() {
		for(int i = 0; i < func_list.size(); i++){
			String curr_func_name = func_list.get(i);
			matrix curr_m = matrix_map.get(curr_func_name);
			int j = 0;
			for(; j < curr_m.call_set.length; j++){
				if(curr_m.call_set[j].equals(curr_m.function_name)){
					//calling itself
					break;
				}
			}
			if(j!=curr_m.call_set.length){
				//recursive func found, need to process, remove it from matrix

				int tag = 0;
				for(int k = 0; k < curr_m.call_set.length; k++){
					if(curr_m.call_set[k].equals(curr_m.function_name)){
						tag = k;
						break;
					}
				}

				curr_m.remove(tag);
				curr_m.normalize();
				matrix_map.put(curr_func_name, curr_m);
			}
		}
		
	}

	private void test_after_read_in() {
		/*
		//how many system call are there in this program executable? print out...
		for (int i = 0; i < func_list.size(); i++){
			String curr_func_name = (String) func_list.toArray()[i];
			if(name_map.containsKey(curr_func_name)){
			//if(matrix_map.get(curr_func_name).call_set.length==1){
				System.out.print(name_map.get(curr_func_name)+",");
			}
		}
		 */


		/*
		//check matrix property after read-in
		for ( int i = 0; i < func_list.size(); i++){
			String curr_func_name = func_list.get(i);
			matrix curr_m = matrix_map.get(curr_func_name);
			check_matrix(curr_m);
			normalize(curr_m, matrix_map);
		}
		System.out.println("after reading in matrix, matrix checked");
		System.exit(0);
		 */		
	}
	
	
	private void read_matrix() throws IOException {
		br = new BufferedReader(new FileReader(program+"/transi-prob"));
		System.out.println("in read_matrix"); //cl de-canceled//
		
		while (br.ready()){

			matrix p_m = new matrix();
			String line = br.readLine();
			String func_name = line.substring(line.indexOf(':')+2);
			func_list.add(func_name);
			
			line = br.readLine();
			//System.out.println("cl test !!!!!!"); //cl de-canceled//
			System.out.println(func_name+":\t"+line); //cl de-canceled//
			
			
			p_m.call_set = line.split("\t");
			
			int num_of_calls = p_m.call_set.length;
			if(num_of_calls == 1){
				p_m.make_call = false;
			}
			else{
				p_m.make_call = true;
			}
			System.out.println(num_of_calls+" calls: "); //cl de-canceled//
			for(int i = 0; i < num_of_calls; i++){
				System.out.print(p_m.call_set[i]+"\t"); //cl de-canceled//
			}
			System.out.print("\n"); //cl de-canceled//

			p_m.m = new double[num_of_calls][num_of_calls];
			for(int i = 0; i < num_of_calls; i++){
				line = br.readLine();
				for(int j = 0; j < num_of_calls; j++){
					p_m.m[i][j] = Double.parseDouble(line.split("\t")[j]);
					System.out.print(p_m.m[i][j]+"\t"); //cl de-canceled//
				}
				System.out.print("\n");
			}
			p_m.function_name = func_name;
			matrix_map.put(func_name, p_m);
		}
		
		//add context here: __open_nocancel --> open@__open_nocancel (different from libc case, here add callee, libc add caller)
		
		int count = 0; 
		Set<String> distinct_syscall = new HashSet<String>();
		Set<String> keys = matrix_map.keySet();
		for(int i = 0 ; i < keys.size(); i++){
			String func_name = keys.toArray()[i].toString();
			matrix p_m = matrix_map.get(keys.toArray()[i]);
			for(int j = 0; j < p_m.call_set.length; j++){
				String callee = p_m.call_set[j];
				if (name_map.containsKey(callee)){// if there is a name mapping callee -> syscall, could be syscall, to be decided
					if(matrix_map.containsKey(callee)){//has matrix
						if(!matrix_map.get(callee).make_call){// does not make call
							p_m.call_set[j] = helper.add_at(name_map.get(callee), callee); //add the context
							System.out.println(p_m.call_set[j]);  //cl de-canceled//
							distinct_syscall.add(p_m.call_set[j]);
							count++;
						}
						else if(!helper.child_make_syscall(callee, matrix_map, name_map)){// yes it makes calls, but no of them is syscall, so itself is syscall
							p_m.call_set[j] = helper.add_at(name_map.get(callee), callee); //add the context
							distinct_syscall.add(p_m.call_set[j]);
							count++;
						}
						else{//makes call, and contains syscall, which means itself is layer of wrapper
							//not system call, do nothing
						}
					}
					else{//has no matrix, must makes no call
						p_m.call_set[j] = helper.add_at(name_map.get(callee), callee); //add the context
						System.out.println(p_m.call_set[j]);  //cl de-canceled//
						distinct_syscall.add(p_m.call_set[j]);
						count++;
					}
				}
				else{// callee is not syscall
					//not system call, do nothing
				}
			}
			matrix_map.put(func_name, p_m);
		}
		System.out.println(count);  //cl de-canceled//
		System.out.println(distinct_syscall);   //cl de-canceled//
		//System.exit(0);  //cl de-canceled//
	}

	private void read_uncalled_function() throws IOException {
		// TODO Auto-generated method stub
		br = new BufferedReader(new FileReader(program+"/uncalled"));
		
		while (br.ready()){
			String line = br.readLine();
			uncalled_map.put(line, true);
		}
	}

	private void read_name_map() throws IOException {
		br = new BufferedReader(new FileReader("names"));
		while(br.ready()){
			String line = br.readLine();
			String name = line.split(",")[0];
			String syscall = line.split(",")[1]; 
			if(name_map.containsKey(name)){
				System.out.println("key already in the map?");
			}
			else{
				name_map.put(name, syscall);
			}
		}
		br.close();
	}

	private matrix merge(matrix curr_m_o, matrix des_m_o, Map<String, matrix> matrix_map) {
		//System.out.println("merging "+curr_m_o.function_name+" "+des_m_o.function_name);
		//copy over original curr and des matrix
		matrix curr_m = (matrix) curr_m_o.clone();
		matrix des_m = (matrix) des_m_o.clone();
		matrix final_m = new matrix();
		matrix e_curr_m = new matrix();

		//(1) find number of unique sys call in des_m
		int des_unique = 0;
		String[] tmp_des = new String[des_m.call_set.length];
		//for every call in des_m
		for (int  j = 0; j < des_m.call_set.length; j++){
			boolean curr_have_it = false;
			boolean is_curr = false;
			//if curr_m has it
			for ( int i = 0; i < curr_m.call_set.length; i++){
				if(curr_m.call_set[i].equals(des_m.call_set[j])){
					curr_have_it = true;
				}

			}
			//if curr_m is it, des_m calling back
			if(curr_m.function_name.equals(des_m.call_set[j])){
				is_curr = true;
			}

			if (curr_have_it == false && is_curr == false){
				tmp_des[des_unique] = des_m.call_set[j];
				//System.out.println("added unique: "+ des_m.call_set[j]);
				des_unique ++;
			}
		}
		//System.out.println("unique call in des function: "+des_unique);

		//(2.1) copy over new call set from curr_m and des_m
		final_m.call_set = new String[curr_m.call_set.length+des_unique-1];
		int i = 0, j = 0;
		for(; i < curr_m.call_set.length; i++){
			if (curr_m.call_set[i].equals(des_m.function_name))continue;
			final_m.call_set[j] = curr_m.call_set[i];
			j++;
		}
		i = 0;
		for(; j < curr_m.call_set.length+des_unique-1; j++){
			final_m.call_set[j] = tmp_des[i];
			i++;
		}

		//(2.2) copy over e_curr_m
		e_curr_m.call_set = new String[curr_m.call_set.length+des_unique];
		i = 0; j = 0;
		for (; i < curr_m.call_set.length; i++){
			e_curr_m.call_set[j] = curr_m.call_set[i];
			j++;
		}
		i = 0;
		for(; j < curr_m.call_set.length+des_unique; j++){
			e_curr_m.call_set[j] = tmp_des[i];
			i++;
		}

		final_m.function_name = curr_m.function_name;
		if(final_m.call_set.length>1)final_m.make_call = true;
		else final_m.make_call = false;

		e_curr_m.function_name = curr_m.function_name;
		if(e_curr_m.call_set.length>1)e_curr_m.make_call = true;
		else e_curr_m.make_call = false;

		/*
		//print the merged string[]
		if (curr_m.function_name.equals("__sigaction")){
		System.out.print(curr_m.function_name+":\t");
		for(i = 0; i < curr_m.call_set.length; i++){
			System.out.print(curr_m.call_set[i]+"\t");
		}
		System.out.print("\n");

		System.out.print(des_m.function_name+":\t");
		for(i = 0; i < des_m.call_set.length; i++){
			System.out.print(des_m.call_set[i]+"\t");
		}
		System.out.print("\n");

		System.out.print(final_m.function_name+":\t");
		for(i = 0; i < final_m.call_set.length; i++){
			System.out.print(final_m.call_set[i]+"\t");
		}
		System.out.print("\n\n");
		}
		*/

		//(3)merge the pairwise call sequence probability

		final_m.m = new double[final_m.call_set.length][final_m.call_set.length];
		e_curr_m.m = new double[e_curr_m.call_set.length][e_curr_m.call_set.length];
		double[][] e_m_tmp = new double[e_curr_m.call_set.length][e_curr_m.call_set.length];

		Map<String, Integer> curr_index_map = null, des_index_map = null, f_index_map = null, e_curr_index_map;
		curr_index_map = new HashMap<String,Integer>();
		des_index_map = new HashMap<String,Integer>();
		f_index_map = new HashMap<String,Integer>();
		e_curr_index_map = new HashMap<String,Integer>();

		for(i = 0; i < curr_m.call_set.length; i++){
			curr_index_map.put(curr_m.call_set[i], i);
		}
		for(i = 0; i < des_m.call_set.length; i++){
			des_index_map.put(des_m.call_set[i], i);
		}
		for(i = 0; i < final_m.call_set.length; i++){
			f_index_map.put(final_m.call_set[i], i);
		}
		for(i = 0; i < e_curr_m.call_set.length; i++){
			e_curr_index_map.put(e_curr_m.call_set[i], i);
		}

		int index_x, index_y;
		double ratio;

		// (3.0) pairwise call sequence WITHIN des function, this is done at the beginning, only once
		//compute the times des_m being called within curr_m
		double des_p = 0, des_p_row = 0, des_p_col = 0; 
		for(i = 0; i < curr_m.call_set.length; i++){
			des_p_col+=curr_m.m[i][curr_index_map.get(des_m.function_name)];
			des_p_row += curr_m.m[curr_index_map.get(des_m.function_name)][i];
		}
		if(des_p_col==0||des_p_row==0){
			if(des_p_col==0){
				System.out.println("des_p_col is 0! What happened?");
				System.out.println("des_p_row is: "+ des_p_row);
			}
			else if(des_p_row==0){
				System.out.println("des_p_row is 0! What happened?");
				System.out.println("des_p_col is: "+ des_p_col);
			}
			//System.out.println(curr_m.call_set.length);
			//System.exit(0);
		}
		des_p = (des_p_row + des_p_col)/2;
		//if des_m call back curr_m, need to skip that 
		int des_skip = 0;
		if(des_index_map.containsKey(curr_m.function_name)){
			des_skip = des_index_map.get(curr_m.function_name);
		}
		else{
			des_skip = -1;
		}
		for(i = 1; i < des_m.call_set.length; i++){
			if(i==des_skip)continue;
			for(j = 1; j < des_m.call_set.length; j++){
				if(j==des_skip)continue;
				String a = des_m.call_set[i];
				String b = des_m.call_set[j];
				final_m.m[f_index_map.get(a)][f_index_map.get(b)] += des_p * des_m.m[i][j];
				//System.out.print(final_m.m[f_index_map.get(a)][f_index_map.get(b)]+"*");
			}
		}

		//init e_curr_m
		for(i = 0; i < curr_m.call_set.length; i++){
			for(j = 0; j < curr_m.call_set.length; j++){
				index_x = e_curr_index_map.get(curr_m.call_set[i]);
				index_y = e_curr_index_map.get(curr_m.call_set[j]);
				e_curr_m.m[index_x][index_y] = curr_m.m[i][j];
				//System.out.print(e_curr_m.m[index_x][index_y]+"#");
			}
		}

		/* recursively transform the probability in caller function e_curr_m to final_m */
		double error = 1.0E-6;
		while(positive_p(e_curr_m, error)){

			//System.out.println("one round merge");
			//(PRE)e->e null within des function, store the prob for next round
			double e2e = des_m.m[0][0];
			des_p_col = 0;
			des_p_row = 0;
			for(i = 0; i < e_curr_m.call_set.length; i++){
				des_p_col += e_curr_m.m[i][e_curr_index_map.get(des_m.function_name)];
				des_p_row += e_curr_m.m[e_curr_index_map.get(des_m.function_name)][i];
			}
			des_p = (des_p_col+des_p_row)/2;
			//System.out.println("des_p is "+des_p);
			//System.out.println("des_p_row is "+des_p_row);

			if(des_p_col==0&&des_p_row==0){//merge curr to final, except those related to des 
				for(i = 0; i < e_curr_m.call_set.length; i++){
					if (e_curr_m.call_set[i].equals(des_m.function_name))continue;
					for (j = 0; j < e_curr_m.call_set.length; j++){
						if (e_curr_m.call_set[j].equals(des_m.function_name))continue;
						final_m.m[f_index_map.get(e_curr_m.call_set[i])][f_index_map.get(e_curr_m.call_set[j])] += e_curr_m.m[i][j];

						e_curr_m.m[i][j] = 0;//clear
					}
				}
				//done!
				break;

			}
			//System.out.println("after");

			//every cell is re-assigned, so no need to clear to all 0, e_m_tmp
			for (i = 0; i < e_curr_m.call_set.length; i++){
				for (j = 0; j < e_curr_m.call_set.length; j++){				
					e_m_tmp[i][j] = 
							e_curr_m.m[i][e_curr_index_map.get(des_m.function_name)] * 
							e2e * 
							e_curr_m.m[e_curr_index_map.get(des_m.function_name)][j]/des_p;	
					//System.out.print(e_m_tmp[i][j]+"%");
				}
			}


			//(3.1) merge curr to final, except those related to des 
			for(i = 0; i < e_curr_m.call_set.length; i++){
				if (e_curr_m.call_set[i].equals(des_m.function_name))continue;
				for (j = 0; j < e_curr_m.call_set.length; j++){
					if (e_curr_m.call_set[j].equals(des_m.function_name))continue;
					final_m.m[f_index_map.get(e_curr_m.call_set[i])][f_index_map.get(e_curr_m.call_set[j])] += e_curr_m.m[i][j];

					e_curr_m.m[i][j] = 0;//clear
				}
			}



			//(3.2)curr call des function, except from des in curr
			for(i = 0; i < e_curr_m.call_set.length; i++){
				if(e_curr_m.call_set[i].equals(des_m.function_name))continue;
				ratio = e_curr_m.m[i][e_curr_index_map.get(des_m.function_name)];
				for (j = 1; j < des_m.call_set.length; j++){
					if(des_m.call_set[j].equals(e_curr_m.function_name))continue;//call back curr from des, exclude? ignore? 
					index_x = f_index_map.get(e_curr_m.call_set[i]);
					index_y = f_index_map.get(des_m.call_set[j]);
					final_m.m[index_x][index_y] += ratio * des_m.m[0][j];
					/*
				if (f_m[index_x][index_y] > 500 && index_x != 0 && index_y!=0){
					System.out.println(index_x+"\t"+index_y);
					print_matrix(des_m.function_name, matrix_map);
					System.exit(0);
				}
					 */
				}
				e_curr_m.m[i][e_curr_index_map.get(des_m.function_name)] = 0;//clear
			}

			ratio = e_curr_m.m[e_curr_index_map.get(des_m.function_name)][e_curr_index_map.get(des_m.function_name)];
			for(j = 1; j < des_m.call_set.length; j++){
				if(des_m.call_set[j].equals(e_curr_m.function_name))continue;//call back curr from des
				index_x = e_curr_index_map.get(des_m.function_name); 
				index_y = e_curr_index_map.get(des_m.call_set[j]);
				e_curr_m.m[index_x][index_y] += ratio * des_m.m[0][j];
			}
			e_curr_m.m[curr_index_map.get(des_m.function_name)][curr_index_map.get(des_m.function_name)] = 0;//clear


			//(3.3)des return to curr function, except to des in curr
			for (i = 0; i < e_curr_m.call_set.length; i++){
				if(e_curr_m.call_set[i].equals(des_m.function_name))continue;
				ratio = e_curr_m.m[e_curr_index_map.get(des_m.function_name)][i];
				for(j = 1; j < des_m.call_set.length; j++){
					if(des_m.call_set[j].equals(e_curr_m.function_name))continue;//call back curr from des, exclude 
					index_x = f_index_map.get(des_m.call_set[j]);
					index_y = f_index_map.get(e_curr_m.call_set[i]);
					final_m.m[index_x][index_y] += ratio * des_m.m[j][0];
				}
				e_curr_m.m[e_curr_index_map.get(des_m.function_name)][i] = 0;
			}

			e_curr_m.m = e_m_tmp;

			//System.out.println("final_m before next round");
			//print_matrix_direct(final_m, matrix_map);
			//System.out.println("e_curr_m before next round");
			//print_matrix_direct(e_curr_m, matrix_map);
		}

		//System.out.println("while end");
		//System.exit(0);
		return final_m;
	}

	private boolean positive_p(matrix e_curr_m, double error) {
		// TODO Auto-generated method stub
		double max = -1;
		for(int i = 0; i < e_curr_m.call_set.length; i++){
			for(int j = 0; j < e_curr_m.call_set.length; j++){
				if (e_curr_m.m[i][j]>max)max = e_curr_m.m[i][j];
			}
		}
		if(max > error)return true;
		else return false;
	}

	private void topoVisit(matrix m, HashMap<String, Boolean> visited,	Queue<String> que, Map<String, matrix> matrix_map) {
		// TODO Auto-generated method stub
		if(!visited.containsKey(m.function_name)){		

			visited.put(m.function_name, true);
			//start from i=1, ignore 0 (NULL)
			for(int i = 1; i < m.call_set.length; i++){
				String callee = helper.strip_at(m.call_set[i]);
				matrix child_m = matrix_map.get(callee);
				//System.out.println(child_m);
				if(child_m.function_name.equals(m.function_name))continue;
				topoVisit(child_m, visited, que, matrix_map);
			}
			que.offer(m.function_name);
			//System.out.println("put into queue: "+m.function_name);
		}
	}

	private void to_syscall_matrix(String matrix_name, Map<String, matrix> matrix_map, Map<String, String> name_map) throws IOException {
		matrix m = matrix_map.get(matrix_name);
		int size = m.call_set.length;
		//print_matrix_direct(m, matrix_map);
		
		//remove non syscall
		boolean done = false;
		int round = 0;
		while(!done){
			//System.out.println(round+"th round");
			//System.out.println("call set size: "+m.call_set.length);

			done = true;
			for (int i = 1; i < m.call_set.length; i++){
				if (!name_map.containsKey(helper.strip_at(m.call_set[i]))){
					m.remove(i);
					done = false;
					round++;
					break;
				}
			}		
		}
		System.out.println(round+" non syscall removed... out of "+size+" calls");
		//print_matrix_direct(m, matrix_map);
		
		//remove duplicate syscall
		int duplicate = 0;
		boolean no_duplicate = false;
		while(!no_duplicate){
			no_duplicate = true;
			for (int i = 1; i < m.call_set.length; i++){
				for (int j = i+1; j < m.call_set.length; j++){
					if(m.call_set[i].equals(m.call_set[j])){
						//System.out.println(m.call_set[i]+" is same as "+m.call_set[j]);
						m.merge(i,j);
						no_duplicate = false;
						duplicate++;
						break;
					}
				}
			}
		}
		System.out.println(duplicate+" duplicate call removed...\n");

		//store matrix to file
		print_matrix_to_file(m, name_map);
	}

	private void print_matrix_to_file(matrix m, Map<String, String> name_map) throws IOException {
		// TODO Auto-generated method stub
		FileWriter fstream = new FileWriter(program+"/initial/"+m.function_name);
		BufferedWriter initial = new BufferedWriter(fstream);

		//write the dimension
		initial.append(m.call_set.length+"\n");

		String call;
		int i,j;
		//write all system calls
		for(i = 0; i < m.call_set.length-1; i ++){
			call = m.call_set[i];
			initial.append(call+",");
		}
		call = m.call_set[i];
		initial.append(call);
		initial.append("\n");

		
		//write the NON-normalized transition times
		for(i = 0; i < m.call_set.length; i++){
			for(j = 0; j < m.call_set.length-1; j++){
				initial.append((m.m[i][j]) + "\t");
			}
			initial.append(m.m[i][j]+"");
			initial.append("\n");
		}

		initial.flush();
		initial.close();
	}

	//check matrix property: 
	private static void check_matrix(matrix curr_m) {

		for (int i = 0 ; i < curr_m.call_set.length; i++){
			double sum_col = 0;
			double sum_row = 0;
			for (int j = 0; j < curr_m.call_set.length; j++){
				sum_col += curr_m.m[j][i];
				sum_row += curr_m.m[i][j];
			}
			//if(sum_col!=sum_row){
			if((sum_col-sum_row)>0.00001){
				System.out.println(curr_m.function_name+" check failed: sum_col!=sum_row");
				System.out.println("sum_col is: "+sum_col);
				System.out.println("sum_row is: "+sum_row);
				break;
				//System.exit(0);
			}
		}
		//System.out.println(curr_m.function_name+"check passed");

		double first_row = 0;
		for (int i = 0; i < curr_m.call_set.length; i++){
			first_row += curr_m.m[0][i];
		}
		//if(first_row == 1){
		if((first_row-1)<0.00001){
		}
		else{
			System.out.println("first row is: "+first_row);
		}
	}

	//print matrix by name lookup
	private static void print_matrix(String testM, Map<String, matrix> matrixMap) {
		// TODO Auto-generated method stub
		matrix m = matrixMap.get(testM);
		System.out.println(testM);
		for (int i = 0; i < m.call_set.length; i++){
			System.out.print(m.call_set[i]+"\t");
		}
		System.out.print("\n");

		int dimention = m.call_set.length;
		if(dimention > 30) dimention = 30;

		for (int i = 0; i < dimention; i++){
			for (int j = 0; j < dimention; j++){
				System.out.print(m.m[i][j]+"\t");
			}
			System.out.print("\n");
		}

	}

	//print matrix directly
	private static void print_matrix_direct(matrix testM, Map<String, matrix> matrixMap) {
		// TODO Auto-generated method stub
		matrix m = testM;
		System.out.println(m.function_name);
		for (int i = 0; i < m.call_set.length; i++){
			System.out.print(m.call_set[i]+"\t");
		}
		System.out.print("\n");

		int dimention = m.call_set.length;
		if(dimention > 100) dimention = 100;

		for (int i = 0; i < dimention; i++){
			for (int j = 0; j < dimention; j++){
				System.out.print(m.m[i][j]+"\t");
			}
			System.out.print("\n");
		}

		System.out.print("\n");
	}
}
