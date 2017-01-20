import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import be.ac.ulg.montefiore.run.jahmm.ObservationDiscrete;


public class helper {

	public static boolean checkplatform(String line) {
		
		if(line.contains("/bsd/"))return true;
		if(line.contains("/sparc/"))return true;
		if(line.contains("/s390/"))return true;
		if(line.contains("/ia64/"))return true;
		if(line.contains("/powerpc/"))return true;
		if(line.contains("/wordsize-64/"))return true;
		if(line.contains("/x86_64/"))return true;

		if(line.contains("/Makefile"))return true;
		if(line.contains("/make-syscalls.sh"))return true;

		return false;
	}

	public static void random_init_array(double[] rand_set) {
		// TODO Auto-generated method stub
		double rand_total;
		rand_total = 0;
		Random generator = new Random();
		for (int i = 0; i < rand_set.length; i++){
			rand_set[i] = generator.nextDouble();
			//if(i==1)System.out.println(rand_set[i]);
			rand_total += rand_set[i];
		}
		for (int i = 0; i < rand_set.length; i++){
			rand_set[i] = rand_set[i]/rand_total;
		}
	}

	public static String[] call_set_union(String[] curr_call_set, String[] new_call_set) {
		// find number of unique sys call in new_call_set
		
		int new_unique = 0;
		String[] tmp_new = new String[new_call_set.length];
		//for every call in new_call_set
		for (int  j = 0; j < new_call_set.length; j++){
			boolean curr_have_it = false;
			//if curr_m has it
			for ( int i = 0; i < curr_call_set.length; i++){
				if(curr_call_set[i].equals(new_call_set[j])){
					curr_have_it = true;
				}

			}

			if (curr_have_it == false){
				tmp_new[new_unique] = new_call_set[j];
				new_unique ++;
			}
		}

		//(2.1) copy over new call set from curr and new
		String[] final_call_set = new String[curr_call_set.length+new_unique];
		int i = 0, j = 0;
		for(; i < curr_call_set.length; i++){
			final_call_set[j] = curr_call_set[i];
			j++;
		}
		i = 0;
		for(; j < curr_call_set.length+new_unique; j++){
			final_call_set[j] = tmp_new[i];
			i++;
		}		
		return final_call_set;
	}

	public static boolean contains(
			List<List<ObservationDiscrete<SYSCALL>>> all_sequences,
			List<ObservationDiscrete<SYSCALL>> e) {
		// TODO Auto-generated method stub
		for(int i = 0; i < all_sequences.size(); i++){
			List<ObservationDiscrete<SYSCALL>> s = all_sequences.get(i);
			int j;
			for (j = 0; j < s.size(); j++){
				if(!s.get(j).toString().equals(e.get(j).toString()))break;
			}
			if (j == s.size()){
				//System.out.println(all_sequences.get(i));
				//System.out.println(e);
				return true;
			}
		}
		//System.out.print("nope!");

		return false;
	}
	
	
	public static boolean contains_rand(
			List<List<ObservationDiscrete<SYSCALLRAND>>> all_sequences_rand,
			List<ObservationDiscrete<SYSCALLRAND>> e) {
		// TODO Auto-generated method stub
		for(int i = 0; i < all_sequences_rand.size(); i++){
			List<ObservationDiscrete<SYSCALLRAND>> s = all_sequences_rand.get(i);
			int j;
			for (j = 0; j < s.size(); j++){
				if(!s.get(j).toString().equals(e.get(j).toString()))break;
			}
			if (j == s.size()){
				//System.out.println(all_sequences.get(i));
				//System.out.println(e);
				return true;
			}
		}
		//System.out.print("nope!");

		return false;
	}

	public static double average_array(int[] A) {
		double sum = 0;
		for(int i = 0; i<A.length; i++){
			sum += A[i];
		}
		return sum/A.length;
	}

	public static double[] smooth(String[] prob_string) {
		// TODO Auto-generated method stub
		double min_pos = 1.0;
		double prob[] = new double[prob_string.length]; 
		for(int i = 0; i < prob_string.length; i++){
			double tmp = Double.parseDouble(prob_string[i]);
			prob[i] = tmp;
			if(tmp < min_pos && tmp > 0){
				min_pos = tmp;
			}
		}
		
		//smooth the array of probs
		for(int i = 0; i < prob.length; i++){
			if(prob[i] == 0){
				prob[i] = min_pos;
				
			}
		}
		
		
		
		//normalize
		double sum = 0;
		
		for(int i = 0; i < prob.length; i++){
			sum+=prob[i];
		}
		
		for(int i = 0; i < prob.length; i++){
			prob[i]=prob[i]/sum;
		}
		
		return prob;
	}
	
	
	public static ObservationDiscrete<SYSCALL> generate_call() {
		// TODO Auto-generated method stub

		Random generator = new Random();
		
		String syscall_str = SYSCALLRAND.values()[generator.nextInt(1000)%(SYSCALLRAND.values().length-1) + 1].toString();

		ObservationDiscrete<SYSCALL> e = new ObservationDiscrete<SYSCALL>(SYSCALL.valueOf(syscall_str));

		//System.out.println(e);
		return e;
	}
	
	public static ObservationDiscrete<SYSCALLRAND> generate_call_rand() {
		// TODO Auto-generated method stub

		Random generator = new Random();
		
		String syscall_str = SYSCALLRAND.values()[generator.nextInt(1000)%(SYSCALLRAND.values().length-1) + 1].toString();

		ObservationDiscrete<SYSCALLRAND> e = new ObservationDiscrete<SYSCALLRAND>(SYSCALLRAND.valueOf(syscall_str));

		//System.out.println(e);
		
		return e;
	}

	public static String strip_at(String call) {
		// TODO Auto-generated method stub
		if(call.contains("@"))
			call = call.substring(call.indexOf("@")+1,call.length());
		return call;
	}

	public static String add_at(String call, String function) {
		// TODO Auto-generated method stub
		
		return call+"@"+function;
	}

	public static int find_index(Map<String, Integer> index_map, String function_name) {
		// TODO Auto-generated method stub
		Set<String> keys = index_map.keySet();
		for(int i = 0; i < keys.size(); i++){
			String call = keys.toArray()[i].toString();
			if(helper.strip_at(call).equals(function_name))
				return index_map.get(keys.toArray()[i]);
		}
		
		return -1;
	}

	public static boolean child_make_syscall(String call, Map<String, matrix> matrix_map, Map<String, String> name_map) {
		// TODO Auto-generated method stub
		matrix m = matrix_map.get(call);
		boolean child_make_call = false;
		for(int i = 0; i < m.call_set.length; i++){
			if (name_map.containsKey(helper.strip_at(m.call_set[i])) ) {
				child_make_call = true;
				break;
			}
		}
		
		if(child_make_call == true) return true;
		return false;
	}

}
