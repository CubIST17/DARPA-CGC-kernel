
public class matrix implements Cloneable {

	String function_name;
	String call_set[];
	double m[][];
	//some function has only one node
	boolean make_call;

	//return a copy of itself
	public Object clone() { 
		try { 
			return super.clone(); 
		} catch (CloneNotSupportedException e) { 
			return null; 
		} 
	}

	//remove one particular call from matrix
	public void remove(int rm) {

		//new call set
		String[] new_call_set = new String[call_set.length-1];
		for(int i = 0; i < call_set.length; i++){
			if(i < rm){
				new_call_set[i] = call_set[i];
			}
			else if(i > rm){
				new_call_set[i-1] = call_set[i];
			}
		}


		//new make call
		boolean new_make_call;
		if(new_call_set.length == 1){
			new_make_call = false;
		}
		else{
			new_make_call = true;
		}


		//new transition matrix
		double[][] new_m = new double[new_call_set.length][new_call_set.length];
		//copy over other prob
		for(int i = 0; i < call_set.length; i++){
			if(i==rm)continue;
			for(int j = 0; j < call_set.length; j++){
				if(j==rm)continue;
				int new_i = i;
				int new_j = j;
				if(i>rm)new_i--;
				if(j>rm)new_j--;
				new_m[new_i][new_j] = m[i][j];
			}
		}
		//redistribute prob
		double total_in = 0, total_out = 0, total = 0;
		for (int i = 0; i < call_set.length; i++){
			if(i==rm)continue;
			total_in+= m[i][rm];
			total_out+= m[rm][i];
		}

		total = (total_in + total_out)/2;
		if(total_in == 0 && total_out == 0){
			System.out.println("!! when remove total_in and total_out are both 0 !!"+call_set[rm]);
			//System.exit(0);
		}
		else{

			for (int i = 0; i < call_set.length; i++){
				if(i==rm)continue;
				for (int j = 0; j < call_set.length; j++){
					if(j==rm)continue;
					int new_i = i;
					int new_j = j;
					if(i>rm)new_i--;
					if(j>rm)new_j--;
					new_m[new_i][new_j]+=m[i][rm]*m[rm][j]/total;
				}
			}
		}
		
		call_set = new_call_set;
		make_call = new_make_call;
		m = new_m;
	}

	//normalize matrix
	public void normalize() {
		double sum = 0;
		for (int i = 0; i < call_set.length; i++){
			sum += m[0][i];
		}
		if(sum==0){
			System.out.println("when normalizing matrix, first row sum is 0!");
			System.exit(0);
		}

		for (int i = 0; i < call_set.length; i++){
			for (int j = 0; j < call_set.length; j++){
				m[i][j] = m[i][j]/sum;
			}
		}
	}

	public void merge(int one, int two) {
		//copy over probs from two to one
		for(int i = 0; i < call_set.length; i++){
			m[i][one] += m[i][two];
			m[i][two] = 0;
		}
		for(int i = 0; i < call_set.length; i++){
			m[one][i] += m[two][i];
			m[two][i] = 0;
		}
		
		//new call set
		String[] new_call_set = new String[call_set.length-1];
		for(int i = 0; i < call_set.length; i++){
			if(i < two){
				new_call_set[i] = call_set[i];
			}
			else if(i > two){
				new_call_set[i-1] = call_set[i];
			}
		}

		//new make call
		boolean new_make_call;
		if(new_call_set.length == 1){
			new_make_call = false;
		}
		else{
			new_make_call = true;
		}


		//new transition matrix
		double[][] new_m = new double[new_call_set.length][new_call_set.length];
		//copy over prob to new matrix
		for(int i = 0; i < call_set.length; i++){
			if(i==two)continue;
			for(int j = 0; j < call_set.length; j++){
				if(j==two)continue;
				int new_i = i;
				int new_j = j;
				if(i>two)new_i--;
				if(j>two)new_j--;
				new_m[new_i][new_j] = m[i][j];
			}
		}
		
		call_set = new_call_set;
		make_call = new_make_call;
		m = new_m;		
	}
}
