import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import be.ac.ulg.montefiore.run.jahmm.Hmm;
import be.ac.ulg.montefiore.run.jahmm.Observation;
import be.ac.ulg.montefiore.run.jahmm.ObservationDiscrete;
import be.ac.ulg.montefiore.run.jahmm.Opdf;
import be.ac.ulg.montefiore.run.jahmm.OpdfDiscrete;
import be.ac.ulg.montefiore.run.jahmm.OpdfDiscreteFactory;
import be.ac.ulg.montefiore.run.jahmm.io.HmmWriter;
import be.ac.ulg.montefiore.run.jahmm.io.OpdfWriter;
import be.ac.ulg.montefiore.run.jahmm.learn.BaumWelchLearner;
import be.ac.ulg.montefiore.run.jahmm.toolbox.KullbackLeiblerDistanceCalculator;
import be.ac.ulg.montefiore.run.jahmm.toolbox.MarkovGenerator;


public class HiddenMarkovModelProcessor {

	String program;
	int seg_length = 15;
	
	List<List<ObservationDiscrete<SYSCALL>>> all_sequences;
	List<List<ObservationDiscrete<SYSCALL>>> all_sequences_distinct;
	List<List<ObservationDiscrete<SYSCALL>>> veri_sequences;
	List<List<ObservationDiscrete<SYSCALL>>> train_sequences;
	List<List<ObservationDiscrete<SYSCALL>>> test_sequences;
	List<List<ObservationDiscrete<SYSCALL>>> false_sequences;
	List<List<ObservationDiscrete<SYSCALL>>> false_sequences_half;
	List<List<ObservationDiscrete<SYSCALL>>> evil_sequences;


	List<List<ObservationDiscrete<SYSCALLRAND>>> all_sequences_rand;
	List<List<ObservationDiscrete<SYSCALLRAND>>> all_sequences_distinct_rand;
	List<List<ObservationDiscrete<SYSCALLRAND>>> veri_sequences_rand;
	List<List<ObservationDiscrete<SYSCALLRAND>>> train_sequences_rand;
	List<List<ObservationDiscrete<SYSCALLRAND>>> test_sequences_rand;
	List<List<ObservationDiscrete<SYSCALLRAND>>> false_sequences_rand;
	List<List<ObservationDiscrete<SYSCALLRAND>>> false_sequences_half_rand;
	List<List<ObservationDiscrete<SYSCALLRAND>>> evil_sequences_rand;

	//actually only for our hmm model, 
	int state_num;
	int emit_num;

	int state_num_rand;
	int emit_num_rand;


	Hmm<ObservationDiscrete<SYSCALL>> hmm;
	Hmm<ObservationDiscrete<SYSCALLRAND>> rand_hmm;

	Hmm<ObservationDiscrete<SYSCALL>> hmm_trained_1 = null;
	Hmm<ObservationDiscrete<SYSCALL>> hmm_trained_2 = null;
	Hmm<ObservationDiscrete<SYSCALL>> hmm_trained_3 = null;
	Hmm<ObservationDiscrete<SYSCALL>> hmm_trained_4 = null;
	Hmm<ObservationDiscrete<SYSCALL>> hmm_trained_5 = null;
	Hmm<ObservationDiscrete<SYSCALL>> hmm_trained_6 = null;
	Hmm<ObservationDiscrete<SYSCALL>> hmm_trained_7 = null;
	Hmm<ObservationDiscrete<SYSCALL>> hmm_trained_8 = null;
	Hmm<ObservationDiscrete<SYSCALL>> hmm_trained_9 = null;
	Hmm<ObservationDiscrete<SYSCALL>> hmm_trained_10 = null;

	int fold_num;
	int fold_size;
	

	ArrayList<Double> probList_hmm_true = new ArrayList<Double>();
	ArrayList<Double> probList_hmm_false = new ArrayList<Double>();
	ArrayList<Double> probList_hmm_false_half = new ArrayList<Double>();
	ArrayList<Double> probList_hmm_veri = new ArrayList<Double>();

	ArrayList<Double> probList_rand_true = new ArrayList<Double>();
	ArrayList<Double> probList_rand_false = new ArrayList<Double>();
	ArrayList<Double> probList_rand_false_half = new ArrayList<Double>();
	ArrayList<Double> probList_rand_veri = new ArrayList<Double>();


	ArrayList<Double> probList_hmm_true_it5 = new ArrayList<Double>();
	ArrayList<Double> probList_hmm_false_it5 = new ArrayList<Double>();
	ArrayList<Double> probList_hmm_false_half_it5 = new ArrayList<Double>();
	ArrayList<Double> probList_hmm_veri_it5 = new ArrayList<Double>();


	ArrayList<Double> probList_rand_true_it5 = new ArrayList<Double>();
	ArrayList<Double> probList_rand_false_it5 = new ArrayList<Double>();
	ArrayList<Double> probList_rand_false_half_it5 = new ArrayList<Double>();
	ArrayList<Double> probList_rand_veri_it5 = new ArrayList<Double>();


	ArrayList<Double> probList_hmm_true_it10 = new ArrayList<Double>();
	ArrayList<Double> probList_hmm_false_it10 = new ArrayList<Double>();
	ArrayList<Double> probList_hmm_false_half_it10 = new ArrayList<Double>();
	ArrayList<Double> probList_hmm_veri_it10 = new ArrayList<Double>();


	ArrayList<Double> probList_rand_true_it10 = new ArrayList<Double>();
	ArrayList<Double> probList_rand_false_it10 = new ArrayList<Double>();
	ArrayList<Double> probList_rand_false_half_it10 = new ArrayList<Double>();
	ArrayList<Double> probList_rand_veri_it10 = new ArrayList<Double>();


	ArrayList<Double> probList_hmm_true_it15 = new ArrayList<Double>();
	ArrayList<Double> probList_hmm_false_it15 = new ArrayList<Double>();
	ArrayList<Double> probList_hmm_false_half_it15 = new ArrayList<Double>();
	ArrayList<Double> probList_hmm_veri_it15 = new ArrayList<Double>();


	ArrayList<Double> probList_rand_true_it15 = new ArrayList<Double>();
	ArrayList<Double> probList_rand_false_it15 = new ArrayList<Double>();
	ArrayList<Double> probList_rand_false_half_it15 = new ArrayList<Double>();
	ArrayList<Double> probList_rand_veri_it15 = new ArrayList<Double>();


	double[] addup_hmm_true_prob = new double[1000];//default is zero
	double[] addup_hmm_false_prob = new double[1000];//default is zero
	double[] addup_hmm_false_half_prob = new double[1000];//default is zero
	double[] addup_hmm_veri_prob = new double[1000];//default is zero
	int[] addup_hmm_num = new int[1000];//default is zero

	double[] addup_rand_true_prob = new double[1000];//default is zero
	double[] addup_rand_false_prob = new double[1000];//default is zero
	double[] addup_rand_false_half_prob = new double[1000];//default is zero
	double[] addup_rand_veri_prob = new double[1000];//default is zero
	int[] addup_rand_num = new int[1000];//default is zero

	int[] round_num_hmm = new int[10]; //records how many rounds each model used
	int[] round_num_rand = new int[10];

	int cur_fold;

	//when test initial model, set emit_prob = 1.0, //output_model(), in train_hmm() and train_rand_hmm() set round = 0
	public void test_hmm(String app, boolean is_first_time) throws IOException, CloneNotSupportedException {

		//cl change, adding the whole path ???
		
		//cl change end
		program = app;

		if(is_first_time) {

			long hmm_time = 0;
			long rand_time = 0;
			long start_time, end_time;

			//KullbackLeiblerDistanceCalculator klc = new KullbackLeiblerDistanceCalculator();


			double emit_prob = 0.5;
			//double emit_prob = 1.0; //use this when test initial model

			fold_num = 10;
			double[] fp = {0, 
					0.0001, 0.0002, 0.0003, 0.0004, 0.0005, 0.0006, 0.0007, 0.0008, 0.0009,
					0.001, 0.002, 0.003, 0.004, 0.005, 0.006, 0.007, 0.008, 0.009, 
					0.01, 0.02, 0.03, 0.04, 0.05, 0.06, 0.07, 0.08, 0.09,
					0.1, 0.15, 0.2, 0.25, 0.3, 0.35, 0.4, 0.45, 0.5};


			/* read in trace segments with indicated window size*/
			//all_sequences and veri_sequecnes are assigned for both our model and random model
			read_sequences(seg_length);

			System.out.println("all_sequences "+all_sequences.size()+" trace segments");
			System.out.println("veri_sequences "+veri_sequences.size()+" trace segments");
			System.out.println("totally "+(all_sequences.size() + veri_sequences.size())+" trace segments");

			System.out.println("all_sequences_rand "+all_sequences_rand.size()+" trace segments");
			System.out.println("veri_sequences_rand "+veri_sequences_rand.size()+" trace segments");
			System.out.println("totally "+(all_sequences_rand.size() + veri_sequences_rand.size())+" trace segments");



			/* test initialized models */
			//init_hmm(1.0);
			//init_rand_hmm();
			//test_hmm_initialized();
			//test_rand_hmm_initialized();
			//System.out.println("original distance : "+klc.distance(hmm, rand_hmm));


			/* train and test trained models */
			fold_size = all_sequences.size()/fold_num;

			train_sequences = new ArrayList<List<ObservationDiscrete<SYSCALL>>>();
			test_sequences = new ArrayList<List<ObservationDiscrete<SYSCALL>>>();
			false_sequences = new ArrayList<List<ObservationDiscrete<SYSCALL>>>();
			false_sequences_half = new ArrayList<List<ObservationDiscrete<SYSCALL>>>();

			train_sequences_rand = new ArrayList<List<ObservationDiscrete<SYSCALLRAND>>>();
			test_sequences_rand = new ArrayList<List<ObservationDiscrete<SYSCALLRAND>>>();
			false_sequences_rand = new ArrayList<List<ObservationDiscrete<SYSCALLRAND>>>();
			false_sequences_half_rand = new ArrayList<List<ObservationDiscrete<SYSCALLRAND>>>();

			double error = 1.0E-4;
			System.out.println("\n");
			for (int i = 0; i < fold_num; i++){
				cur_fold = i;
				init_hmm(emit_prob);
				init_rand_hmm();

				System.out.println("****** testing fold #: "+i);

				test_sequences.clear();
				train_sequences.clear();
				false_sequences.clear();
				false_sequences_half.clear();

				test_sequences_rand.clear();
				train_sequences_rand.clear();
				false_sequences_rand.clear();
				false_sequences_half_rand.clear();


				int start = i*fold_size;
				int end = (i+1)*fold_size;
				if(i==fold_num-1)end = all_sequences.size();
				System.out.println("testing sequence from: "+start+" to: "+(end-1));

				//construct training and test sequences for each fold
				for (int j = 0; j< all_sequences.size(); j++){


					if(j>= start && j< end){//test segments, all put int
						test_sequences.add(all_sequences.get(j));
						test_sequences_rand.add(all_sequences_rand.get(j));
					}
					else{//train sequences, remove duplicate
						if(!helper.contains(train_sequences, all_sequences.get(j)))
							train_sequences.add(all_sequences.get(j));

						if(!helper.contains_rand(train_sequences_rand, all_sequences_rand.get(j)))
							train_sequences_rand.add(all_sequences_rand.get(j));

					}
				}

				System.out.println("train_sequences size: "+train_sequences.size());
				System.out.println("test_sequences size: "+test_sequences.size());

				System.out.println("train_sequences_rand size: "+train_sequences_rand.size());
				System.out.println("test_sequences_rand size: "+test_sequences_rand.size());

				//build false(fake) sequences
				build_false_sequences();

				//System.exit(0);
				//hmm
				start_time = System.currentTimeMillis();

				train_hmm(error);

				end_time = System.currentTimeMillis();
				hmm_time += (end_time - start_time);

				//rand
				start_time = System.currentTimeMillis();

				train_rand_hmm(error);

				end_time = System.currentTimeMillis();
				rand_time += (end_time - start_time);


				/* compare trained models */

				//System.out.println("distance between hmm and rand_hmm trained: "+klc.distance(hmm, rand_hmm));
				//System.out.println("original distance : "+klc.distance(hmm, rand_hmm));
				System.out.println("\n");

				//output trained model
				//System.out.println("before");

				output_model();

				//System.out.println("after");


			}
			test_hmm_trained(fp);
			test_rand_hmm_trained(fp);
			System.out.println("\nsyscall\t"+program+"\t"+seg_length);
			System.out.println("hmm avg rounds: "+helper.average_array(round_num_hmm));
			System.out.println("rand avg rounds: "+helper.average_array(round_num_rand));
			System.out.println("acceleration: "+ (helper.average_array(round_num_rand)-helper.average_array(round_num_hmm))/helper.average_array(round_num_rand));

			System.out.println("hmm time: "+hmm_time+"\tmilisec 10 fold");
			System.out.println("rand time: "+rand_time+"\tmilisec 10 fold");

		}
		else{

			//read in model from existing file, all 10
			readin_model();

			///*
			//read in and test attack evil traces
			int trace_num = 7;
			read_trace_evil(trace_num);
			//double threshold = 0.1; //gzip_v5
			double threshold = 0.04; //proftpd
			test_readin_model(threshold);
			//*/

			//test random genrated traces
			//test_random_seq();

		}

	}

	private void build_false_sequences() {
		// TODO Auto-generated method stub
		//int false_num = 20000; // just for find rop seq
		int false_num = 1000;
		int divide1=seg_length*10/15;
		//int divide2=seg_length*12/15; //replace last 3
		int divide2=seg_length*11/15; //replace last 4


		//for our model
		Random r = new Random();
		int fake_num = 0;
		while(fake_num < false_num){
			int r_num = r.nextInt(all_sequences_distinct.size());
			//System.out.println(all_sequences.get(r_num));

			List<ObservationDiscrete<SYSCALL>> e_from = new ArrayList<ObservationDiscrete<SYSCALL>>();
			e_from = all_sequences_distinct.get(r_num);

			List<ObservationDiscrete<SYSCALL>> e = new ArrayList<ObservationDiscrete<SYSCALL>>();

			for (int k = 0; k < divide1; k++){
				e.add(e_from.get(k));
			}
			for (int k = divide1; k < e_from.size(); k++){
				e.add(helper.generate_call());
			}
			
			//System.out.println(all_sequences.get(r_num));
			//System.out.println(e);
			if(!helper.contains(all_sequences_distinct,e) && !helper.contains(veri_sequences, e) && !helper.contains(false_sequences, e)){
				false_sequences.add(e);
				fake_num++;
			}
			else{
				//collision++;
			}

		}
		System.out.println(fake_num+" unique false sequence segments generated");

		//build half-shuffled sequences
		r = new Random();
		fake_num = 0;
		while(fake_num < false_num){
			int r_num = r.nextInt(all_sequences_distinct.size());
			//System.out.println(all_sequences.get(r_num));

			List<ObservationDiscrete<SYSCALL>> e_from = new ArrayList<ObservationDiscrete<SYSCALL>>();
			e_from = all_sequences_distinct.get(r_num);

			List<ObservationDiscrete<SYSCALL>> e = new ArrayList<ObservationDiscrete<SYSCALL>>();

			for (int k = 0; k < divide2; k++){
				e.add(e_from.get(k));
			}
			for (int k = divide2; k < e_from.size(); k++){
				e.add(helper.generate_call());
			}
			
			//System.out.println(all_sequences.get(r_num));
			//System.out.println(e);
			if(!helper.contains(all_sequences_distinct,e) && !helper.contains(veri_sequences, e) && !helper.contains(false_sequences_half, e)){
				false_sequences_half.add(e);
				fake_num++;
			}
			else{
				//collision++;
			}

		}
		System.out.println(fake_num+" unique false sequence segments generated (half shuffled)");


		//for random model
		r = new Random();
		fake_num = 0;
		while(fake_num < false_num){
			int r_num = r.nextInt(all_sequences_distinct_rand.size());

			List<ObservationDiscrete<SYSCALLRAND>> e_from = new ArrayList<ObservationDiscrete<SYSCALLRAND>>();
			e_from = all_sequences_distinct_rand.get(r_num);

			List<ObservationDiscrete<SYSCALLRAND>> e = new ArrayList<ObservationDiscrete<SYSCALLRAND>>();

			for (int k = 0; k < divide1; k++){
				e.add(e_from.get(k));
			}
			for (int k = divide1; k < e_from.size(); k++){
				e.add(helper.generate_call_rand());
			}
			
			//System.out.println(all_sequences.get(r_num));
			//System.out.println(e);
			if(!helper.contains_rand(all_sequences_distinct_rand,e) && !helper.contains_rand(veri_sequences_rand, e) && !helper.contains_rand(false_sequences_rand, e)){
				false_sequences_rand.add(e);
				fake_num++;
			}
			else{
				//collision++;
			}

		}
		System.out.println(fake_num+" unique false sequence segments generated");

		//build half-shuffled sequences
		r = new Random();
		fake_num = 0;
		while(fake_num < false_num){
			int r_num = r.nextInt(all_sequences_distinct_rand.size());

			List<ObservationDiscrete<SYSCALLRAND>> e_from = new ArrayList<ObservationDiscrete<SYSCALLRAND>>();
			e_from = all_sequences_distinct_rand.get(r_num);

			List<ObservationDiscrete<SYSCALLRAND>> e = new ArrayList<ObservationDiscrete<SYSCALLRAND>>();

			for (int k = 0; k < divide2; k++){
				e.add(e_from.get(k));
			}
			for (int k = divide2; k < e_from.size(); k++){
				e.add(helper.generate_call_rand());
			}
			
			//System.out.println(all_sequences.get(r_num));
			//System.out.println(e);
			if(!helper.contains_rand(all_sequences_distinct_rand,e) && !helper.contains_rand(veri_sequences_rand, e) && !helper.contains_rand(false_sequences_half_rand, e)){
				false_sequences_half_rand.add(e);
				fake_num++;
			}
			else{
				//collision++;
			}

		}
		System.out.println(fake_num+" unique false sequence segments generated (half shuffled)");
	}

	private void test_random_seq() throws IOException {

		String count_data_file_name = program+"_count_hmm_randseq-15";
		//int randseq_num = 1830;//proftpd
		//int randseq_num = 750;//lighttpd
		//int randseq_num = 80;//cat
		//int randseq_num = 50;//chmod
		//int randseq_num = 590;//gzip
		int randseq_num = 510;//ps

		//String count_data_file_name = program+"_count_hmm_randseq-5";
		//int randseq_num = 670;


		ArrayList<List<ObservationDiscrete<SYSCALL>>> rand_sequences = new ArrayList<List<ObservationDiscrete<SYSCALL>>>();

		int i = 0;
		Random generator = new Random();
		while(i < randseq_num){

			ArrayList<ObservationDiscrete<SYSCALL>> one_seq = new ArrayList<ObservationDiscrete<SYSCALL>>();
			int l = 0;
			while(l<seg_length){
				SYSCALL syscall = SYSCALL.values()[generator.nextInt(1000)%SYSCALL.values().length];
				ObservationDiscrete<SYSCALL> e = new ObservationDiscrete<SYSCALL>(syscall);
				one_seq.add(e);
				l++;
			}
			//System.out.println(one_seq);
			rand_sequences.add(one_seq);
			i++;
		}

		HashMap<Double, Integer> count_map = new HashMap<Double, Integer>();
		double right = 0;
		int Divide = 16;
		int increment = 5; 
		for(i = 1; i <= Divide; i++ ){
			int count = 0;
			right += increment;
			//System.out.println(1.0*right/100);
			count_map.put(1.0*right/100, count);
		}
		int num = 0;
		for(i = 0; i < randseq_num; i++){
			double prob = 0;
			prob += Math.pow(hmm_trained_1.probability(rand_sequences.get(i)), 1.0/rand_sequences.get(i).size());
			prob += Math.pow(hmm_trained_2.probability(rand_sequences.get(i)), 1.0/rand_sequences.get(i).size());
			prob += Math.pow(hmm_trained_3.probability(rand_sequences.get(i)), 1.0/rand_sequences.get(i).size());
			prob += Math.pow(hmm_trained_4.probability(rand_sequences.get(i)), 1.0/rand_sequences.get(i).size());
			prob += Math.pow(hmm_trained_5.probability(rand_sequences.get(i)), 1.0/rand_sequences.get(i).size());
			prob += Math.pow(hmm_trained_6.probability(rand_sequences.get(i)), 1.0/rand_sequences.get(i).size());
			prob += Math.pow(hmm_trained_7.probability(rand_sequences.get(i)), 1.0/rand_sequences.get(i).size());
			prob += Math.pow(hmm_trained_8.probability(rand_sequences.get(i)), 1.0/rand_sequences.get(i).size());
			prob += Math.pow(hmm_trained_9.probability(rand_sequences.get(i)), 1.0/rand_sequences.get(i).size());
			prob += Math.pow(hmm_trained_10.probability(rand_sequences.get(i)), 1.0/rand_sequences.get(i).size());
			prob = prob/10; 

			if(prob>=0){
				//if(prob>1.0E-8){
				System.out.println("i: "+prob);
				num++;
			}
			int k = 1;
			right = 0;
			for(; k <= Divide;){
				right += increment;
				if(prob*100 > right)k++;
				else{
					break;
				}
			}
			count_map.put( 1.0*right/100, count_map.get(1.0*right/100)+1);
		}

		System.out.println("num above threshold"+num);


		FileWriter fw;
		fw = new FileWriter(count_data_file_name);
		right = 0;
		for(int k = 1; k <= Divide; k++){
			right += increment;
			fw.append("["+1.0*(right-increment)/100+"-"+1.0*right/100+"]"+"\t"+count_map.get(1.0*right/100)+"\n");
		}
		fw.close();
	}

	private void test_readin_model(double threshold) {
		//System.out.println(evil_sequences.size());
		
		int above = 0, below = 0;
				
		for(int i = 0; i<evil_sequences.size(); i++){
			double prob = 0;
			prob += Math.pow(hmm_trained_1.probability(evil_sequences.get(i)), 1.0/evil_sequences.get(i).size());
			prob += Math.pow(hmm_trained_2.probability(evil_sequences.get(i)), 1.0/evil_sequences.get(i).size());
			prob += Math.pow(hmm_trained_3.probability(evil_sequences.get(i)), 1.0/evil_sequences.get(i).size());
			prob += Math.pow(hmm_trained_4.probability(evil_sequences.get(i)), 1.0/evil_sequences.get(i).size());
			prob += Math.pow(hmm_trained_5.probability(evil_sequences.get(i)), 1.0/evil_sequences.get(i).size());
			prob += Math.pow(hmm_trained_6.probability(evil_sequences.get(i)), 1.0/evil_sequences.get(i).size());
			prob += Math.pow(hmm_trained_7.probability(evil_sequences.get(i)), 1.0/evil_sequences.get(i).size());
			prob += Math.pow(hmm_trained_8.probability(evil_sequences.get(i)), 1.0/evil_sequences.get(i).size());
			prob += Math.pow(hmm_trained_9.probability(evil_sequences.get(i)), 1.0/evil_sequences.get(i).size());
			prob += Math.pow(hmm_trained_10.probability(evil_sequences.get(i)), 1.0/evil_sequences.get(i).size());
			prob = prob/10;
			//if(prob>0)
			//System.out.println(prob+"\t"+evil_sequences.get(i));
			if(prob < threshold){
				below++;
				//System.out.print("*");
				//if(prob>0)System.out.println(prob+"\t"+evil_sequences.get(i));
			}
			else{
				above++;
			}
		}
		
		System.out.println("above: "+above);
		System.out.println("below: "+below);
	}

	private void read_trace_evil(int trace_num) throws IOException {
		//comment out the following two lines after first use
		//pre_process_evil_trace(program, trace_num);
		//System.exit(0);

		evil_sequences = new ArrayList<List<ObservationDiscrete<SYSCALL>>>();
		int total_seg_num = 0;
		int zero_num = 0;

		BufferedReader br;

		String path= program+"/trace-evil";
		File file=new File(path);
		File[] tempList = file.listFiles();
		List<ObservationDiscrete<SYSCALL>> one_seq;
		for(int i = 0; i < tempList.length; i++){
			//System.out.println(tempList[i].getPath());
			//System.out.println(tempList[i].getName());
			if(tempList[i].getName().endsWith(".trace") && tempList[i].getName().startsWith(trace_num+"_") ){
				one_seq = new ArrayList<ObservationDiscrete<SYSCALL>>();

				ObservationDiscrete<SYSCALL> e = new ObservationDiscrete<SYSCALL>(SYSCALL.valueOf("env"));
				one_seq.add(e);

				br = new BufferedReader(new FileReader(tempList[i].getPath()));
				while(br.ready()){
					String line = br.readLine();
					//System.out.println(line);
					String syscall = line.substring(0, line.indexOf('('));
					//System.out.println(syscall);
					boolean has = false;
					for(SYSCALL s : SYSCALL.values()){
						if(s.name().equals(syscall)){
							has = true;
							break;
						}
					}
					if(has){
						e = new ObservationDiscrete<SYSCALL>(SYSCALL.valueOf(syscall));
						one_seq.add(e);
					}
					
					else if(program.equals("proftpd")){
						if(syscall.equals("clone")
								||syscall.equals("setresuid")
								||syscall.equals("rt_sigreturn")
								||syscall.equals("setresgid")
								||syscall.equals("wait4")
								||syscall.equals("recvfrom")
								||syscall.equals("set_tid_address")
								||syscall.equals("set_robust_list")
								||syscall.equals("futex")
								||syscall.equals("sendmsg")
								||syscall.equals("getresuid")
								||syscall.equals("getresgid")
								||syscall.contains("--- SIG")
								)
						{
							continue;
						}
						else{
							if(syscall.equals("fcntl64")){
								e = new ObservationDiscrete<SYSCALL>(SYSCALL.valueOf("fcntl"));
								one_seq.add(e);
								continue;
							}
							else if(syscall.equals("_llseek")){
								e = new ObservationDiscrete<SYSCALL>(SYSCALL.valueOf("lseek"));
								one_seq.add(e);
								continue;
							}
							else if(syscall.equals("stat64")){
								e = new ObservationDiscrete<SYSCALL>(SYSCALL.valueOf("stat"));
								one_seq.add(e);
								continue;
							}
							else if(syscall.equals("fstat64")){
								e = new ObservationDiscrete<SYSCALL>(SYSCALL.valueOf("fstat"));
								one_seq.add(e);
								continue;
							}
							else if(syscall.equals("mmap2")){
								e = new ObservationDiscrete<SYSCALL>(SYSCALL.valueOf("mmap"));
								one_seq.add(e);
								continue;
							}
							else if(syscall.equals("lstat64")){
								e = new ObservationDiscrete<SYSCALL>(SYSCALL.valueOf("lstat"));
								one_seq.add(e);
								continue;
							}
							else if(syscall.equals("geteuid32")){
								e = new ObservationDiscrete<SYSCALL>(SYSCALL.valueOf("geteuid"));
								one_seq.add(e);
								continue;
							}
							else if(syscall.equals("setuid32")){
								e = new ObservationDiscrete<SYSCALL>(SYSCALL.valueOf("setuid"));
								one_seq.add(e);
								continue;
							}
							else if(syscall.equals("getuid32")){
								e = new ObservationDiscrete<SYSCALL>(SYSCALL.valueOf("getuid"));
								one_seq.add(e);
								continue;
							}
							else if(syscall.equals("getegid32")){
								e = new ObservationDiscrete<SYSCALL>(SYSCALL.valueOf("getegid"));
								one_seq.add(e);
								continue;
							}
							else if(syscall.equals("getgid32")){
								e = new ObservationDiscrete<SYSCALL>(SYSCALL.valueOf("getgid"));
								one_seq.add(e);
								continue;
							}
							else if(syscall.equals("setgid32")){
								e = new ObservationDiscrete<SYSCALL>(SYSCALL.valueOf("setgid"));
								one_seq.add(e);
								continue;
							}
							else if(syscall.equals("send")){
								e = new ObservationDiscrete<SYSCALL>(SYSCALL.valueOf("sendto"));
								one_seq.add(e);
								continue;
							}
							else if(syscall.equals("getdents64")){
								e = new ObservationDiscrete<SYSCALL>(SYSCALL.valueOf("getdents"));
								one_seq.add(e);
								continue;
							}
							else if(syscall.equals("statfs64")){
								e = new ObservationDiscrete<SYSCALL>(SYSCALL.valueOf("statfs"));
								one_seq.add(e);
								continue;
							}
							System.out.println("does not have: "+syscall);
							e = null;
							one_seq.add(e);
							continue;
						}			
					}
					else if(program.equals("gzip_v5")){
						if(syscall.equals("execve")
								||syscall.equals("ioctl")
								||syscall.contains("--- SIG")
								||syscall.equals("rt_sigreturn")
								||syscall.equals("arch_prctl")){
							continue;
						}
						else{
							if(syscall.equals("fcntl64")){
								e = new ObservationDiscrete<SYSCALL>(SYSCALL.valueOf("fcntl"));
								one_seq.add(e);
								continue;
							}
							else if(syscall.equals("_llseek")){
								e = new ObservationDiscrete<SYSCALL>(SYSCALL.valueOf("lseek"));
								one_seq.add(e);
								continue;
							}
							else if(syscall.equals("stat64")){
								e = new ObservationDiscrete<SYSCALL>(SYSCALL.valueOf("stat"));
								one_seq.add(e);
								continue;
							}
							else if(syscall.equals("fstat64")){
								e = new ObservationDiscrete<SYSCALL>(SYSCALL.valueOf("fstat"));
								one_seq.add(e);
								continue;
							}
							else if(syscall.equals("mmap2")){
								e = new ObservationDiscrete<SYSCALL>(SYSCALL.valueOf("mmap"));
								one_seq.add(e);
								continue;
							}
							else if(syscall.equals("lstat64")){
								e = new ObservationDiscrete<SYSCALL>(SYSCALL.valueOf("lstat"));
								one_seq.add(e);
								continue;
							}
							else if(syscall.equals("geteuid32")){
								e = new ObservationDiscrete<SYSCALL>(SYSCALL.valueOf("geteuid"));
								one_seq.add(e);
								continue;
							}
							else if(syscall.equals("setuid32")){
								e = new ObservationDiscrete<SYSCALL>(SYSCALL.valueOf("setuid"));
								one_seq.add(e);
								continue;
							}
							else if(syscall.equals("getuid32")){
								e = new ObservationDiscrete<SYSCALL>(SYSCALL.valueOf("getuid"));
								one_seq.add(e);
								continue;
							}
							else if(syscall.equals("getegid32")){
								e = new ObservationDiscrete<SYSCALL>(SYSCALL.valueOf("getegid"));
								one_seq.add(e);
								continue;
							}
							else if(syscall.equals("getgid32")){
								e = new ObservationDiscrete<SYSCALL>(SYSCALL.valueOf("getgid"));
								one_seq.add(e);
								continue;
							}
							else if(syscall.equals("setgid32")){
								e = new ObservationDiscrete<SYSCALL>(SYSCALL.valueOf("setgid"));
								one_seq.add(e);
								continue;
							}
							else if(syscall.equals("getdents64")){
								e = new ObservationDiscrete<SYSCALL>(SYSCALL.valueOf("getdents"));
								one_seq.add(e);
								continue;
							}
							else if(syscall.equals("statfs64")){
								e = new ObservationDiscrete<SYSCALL>(SYSCALL.valueOf("statfs"));
								one_seq.add(e);
								continue;
							}
							else if(syscall.equals("chown32")){
								e = new ObservationDiscrete<SYSCALL>(SYSCALL.valueOf("chown"));
								one_seq.add(e);
								continue;
							}
							//new call
							System.out.println("does not have: "+syscall);
							e = null;
							one_seq.add(e);
							continue;
						}	
					}
					else{
						System.out.println("new program: "+syscall);
					}
				}
				e = new ObservationDiscrete<SYSCALL>(SYSCALL.valueOf("env"));
				one_seq.add(e);

				int head = 0, tail = seg_length;				

				//System.out.println(one_seq.subList(head, tail).size());
				boolean has_null = false;
				while(tail <= one_seq.size()){
					has_null = false;
					total_seg_num++;	
					for(int pos = head; pos < tail; pos++){
						if(one_seq.get(pos) == null){
							has_null = true;
							continue;
						}
					}
					if(has_null == false){
						evil_sequences.add(one_seq.subList(head, tail));
					}
					else{
						zero_num ++;
					}
					head++;
					tail++;
				}
				
				
			}
		}

		System.out.println("segments total number: "+total_seg_num);
		System.out.println("segments with new calls: "+zero_num);
	}

	private void pre_process_evil_trace(String program, int trace_num) throws IOException {
		String path= program+"/trace-evil";
		File file=new File(path);
		File[] tempList = file.listFiles();
		HashMap<String, Integer> file_num_map = new HashMap<String, Integer>();
		int file_num = 0;
		int curr_num = 0;
		for(int i = 0; i < tempList.length; i++){
			String filename = tempList[i].getName();
			//System.out.println(filename);
			if(filename.contains("trace") && filename.startsWith("evil-"+trace_num)){
				System.out.println(filename);
				String trace_id = filename.substring(5, filename.indexOf("."));
				System.out.println(trace_id);

				//System.exit(0);
				file_num_map.clear();

				BufferedReader br = new BufferedReader(new FileReader(program+"/trace-evil/"+filename));

				while(br.ready()){
					String line = br.readLine();
					if(line.contains("resumed"))continue;
					String pid = line.substring(0, line.indexOf(' '));
					String to_append = line.substring(line.indexOf(' '));
					while(to_append.startsWith(" ")){
						to_append = to_append.substring(1);
					}
					if(file_num_map.containsKey(pid)){//already have the pid
						file_num = file_num_map.get(pid);
						BufferedWriter out = new BufferedWriter(new FileWriter(program+"/trace-evil/"+trace_id+"_"+file_num+".trace", true));//append to existing file for that pid
						out.append(to_append+"\n");
						out.close();
					}
					else{//new pid found
						curr_num++;
						file_num = curr_num;
						file_num_map.put(pid, file_num);
						BufferedWriter out = new BufferedWriter(new FileWriter(program+"/trace-evil/"+trace_id+"_"+file_num+".trace", true));//append to existing file for that pid
						out.append(to_append+"\n");
						out.close();
					}

				}	
				br.close();
			}
		}
		System.exit(0);		
	}

	private void readin_model() throws IOException {

		//hmm_trained_1 = new Hmm<ObservationDiscrete<SYSCALL>>(SYSCALL.values().length,new OpdfDiscreteFactory<SYSCALL>(SYSCALL.class));
		//hmm_trained_2 = new Hmm<ObservationDiscrete<SYSCALL>>(SYSCALL.values().length,new OpdfDiscreteFactory<SYSCALL>(SYSCALL.class));
		//hmm_trained_3 = new Hmm<ObservationDiscrete<SYSCALL>>(SYSCALL.values().length,new OpdfDiscreteFactory<SYSCALL>(SYSCALL.class));
		//hmm_trained_4 = new Hmm<ObservationDiscrete<SYSCALL>>(SYSCALL.values().length,new OpdfDiscreteFactory<SYSCALL>(SYSCALL.class));
		//hmm_trained_5 = new Hmm<ObservationDiscrete<SYSCALL>>(SYSCALL.values().length,new OpdfDiscreteFactory<SYSCALL>(SYSCALL.class));
		//hmm_trained_6 = new Hmm<ObservationDiscrete<SYSCALL>>(SYSCALL.values().length,new OpdfDiscreteFactory<SYSCALL>(SYSCALL.class));
		//hmm_trained_7 = new Hmm<ObservationDiscrete<SYSCALL>>(SYSCALL.values().length,new OpdfDiscreteFactory<SYSCALL>(SYSCALL.class));
		//hmm_trained_8 = new Hmm<ObservationDiscrete<SYSCALL>>(SYSCALL.values().length,new OpdfDiscreteFactory<SYSCALL>(SYSCALL.class));
		//hmm_trained_9 = new Hmm<ObservationDiscrete<SYSCALL>>(SYSCALL.values().length,new OpdfDiscreteFactory<SYSCALL>(SYSCALL.class));
		//hmm_trained_10 = new Hmm<ObservationDiscrete<SYSCALL>>(SYSCALL.values().length,new OpdfDiscreteFactory<SYSCALL>(SYSCALL.class));
		Hmm<ObservationDiscrete<SYSCALL>> hmm_trained;
		BufferedReader br;
		for(int model_i = 1; model_i<=10; model_i++){
			br = new BufferedReader(new FileReader(program+"/result-last-4/model/hmm_length_"+seg_length+"_"+(model_i-1)+".txt"));
			//num
			int num = Integer.parseInt(br.readLine());
			hmm_trained = new Hmm<ObservationDiscrete<SYSCALL>>(SYSCALL.values().length,new OpdfDiscreteFactory<SYSCALL>(SYSCALL.class));;
			//transi
			for(int i = 0; i<num; i++){
				String transi = br.readLine();
				String[] transi_prob = transi.split("\t");
				for(int j = 0; j<num; j++){
					hmm_trained.setAij(i, j, Double.parseDouble(transi_prob[j]));
				}
			}
			//emit
			for(int i = 0; i<num; i++){
				String emit = br.readLine();
				String[] emit_prob_s = emit.split("\t");
				double[] emit_prob = new double[num];
				for(int j = 0; j<num; j++){
					emit_prob[j] = Double.parseDouble(emit_prob_s[j]);
				}
				hmm_trained.setOpdf(i, new OpdfDiscrete<SYSCALL>(SYSCALL.class, emit_prob));
			}

			//pi
			String[] pi = br.readLine().split("\t");
			for(int i = 0; i<num; i++){
				hmm_trained.setPi(i, Double.parseDouble(pi[i]));
			}
			br.close();

			if(model_i == 1)hmm_trained_1 = hmm_trained;
			if(model_i == 2)hmm_trained_2 = hmm_trained;
			if(model_i == 3)hmm_trained_3 = hmm_trained;
			if(model_i == 4)hmm_trained_4 = hmm_trained;
			if(model_i == 5)hmm_trained_5 = hmm_trained;
			if(model_i == 6)hmm_trained_6 = hmm_trained;
			if(model_i == 7)hmm_trained_7 = hmm_trained;
			if(model_i == 8)hmm_trained_8 = hmm_trained;
			if(model_i == 9)hmm_trained_9 = hmm_trained;
			if(model_i == 10)hmm_trained_10 = hmm_trained;
		}
	}

	private void output_model() throws IOException {
		//hmm
		
		//System.out.println("[CL test] program name:"+program);
		
		FileWriter fstream_hmm = new FileWriter(program+"/result-last-4/model/hmm_length_"+seg_length+"_"+cur_fold+".txt");
		BufferedWriter out_hmm = new BufferedWriter(fstream_hmm);
		out_hmm.append(hmm.nbStates()+"\n");
		int i;
		//transi
		i=0;
		for(; i < hmm.nbStates(); i++){
			int j = 0;
			for(; j < hmm.nbStates()-1; j++){
				out_hmm.append(hmm.getAij(i, j)+"\t");
			}
			out_hmm.append(hmm.getAij(i, j)+"\n");
		}
		//emit
		i=0;
		for(; i < hmm.nbStates(); i++){
			int j = 0;
			for(; j < hmm.nbStates()-1; j++){
				ObservationDiscrete<SYSCALL> s = new ObservationDiscrete<SYSCALL>(SYSCALL.values()[j]);
				out_hmm.append(hmm.getOpdf(i).probability(s)+"\t");
			}
			ObservationDiscrete<SYSCALL> s = new ObservationDiscrete<SYSCALL>(SYSCALL.values()[j]);
			out_hmm.append(hmm.getOpdf(i).probability(s)+"\n");
		}
		//pi
		i=0;
		for(; i < hmm.nbStates()-1; i++){
			out_hmm.append(hmm.getPi(i)+"\t");
		}
		out_hmm.append(hmm.getPi(i)+"\n");
		out_hmm.close();


		//rand
		FileWriter fstream_rand = new FileWriter(program+"/result-last-4/model/rand_length_"+seg_length+"_"+cur_fold+".txt");
		BufferedWriter out_rand = new BufferedWriter(fstream_rand);
		out_rand.append(rand_hmm.nbStates()+"\n");
		//transi
		i=0;
		for(; i < rand_hmm.nbStates(); i++){
			int j = 0;
			for(; j < rand_hmm.nbStates()-1; j++){
				out_rand.append(rand_hmm.getAij(i, j)+"\t");
			}
			out_rand.append(rand_hmm.getAij(i, j)+"\n");
		}
		//emit
		i=0;
		for(; i < rand_hmm.nbStates(); i++){
			int j = 0;
			for(; j < SYSCALLRAND.values().length-1; j++){
				ObservationDiscrete<SYSCALLRAND> s = new ObservationDiscrete<SYSCALLRAND>(SYSCALLRAND.values()[j]);
				out_rand.append(rand_hmm.getOpdf(i).probability(s)+"\t");
			}
			ObservationDiscrete<SYSCALLRAND> s = new ObservationDiscrete<SYSCALLRAND>(SYSCALLRAND.values()[j]);
			out_rand.append(rand_hmm.getOpdf(i).probability(s)+"\n");
		}
		//pi
		i=0;
		for(; i < rand_hmm.nbStates()-1; i++){
			out_rand.append(rand_hmm.getPi(i)+"\t");
		}
		out_rand.append(rand_hmm.getPi(i)+"\n");
		out_rand.close();

	}

	private void test_rand_hmm_trained(double[] fp) throws IOException {

		Collections.sort(probList_rand_true);
		Collections.sort(probList_rand_false);
		Collections.sort(probList_rand_false_half);

		BufferedWriter rand_true= new BufferedWriter(new FileWriter(program+"/rand_true-"+seg_length+".txt"));
		BufferedWriter rand_false= new BufferedWriter(new FileWriter(program+"/rand_false-"+seg_length+".txt"));
		BufferedWriter rand_false_half= new BufferedWriter(new FileWriter(program+"/rand_false_half-"+seg_length+".txt"));

		for(int i = 0; i < probList_rand_true.size(); i++){
			double prob = probList_rand_true.get(i);
			rand_true.append(prob+"\n");
		}

		for(int i = 0; i < probList_rand_false.size(); i++){			
			double prob = probList_rand_false.get(i);
			rand_false.append(prob+"\n");
		}

		for(int i = 0; i < probList_rand_false_half.size(); i++){			
			double prob = probList_rand_false_half.get(i);
			rand_false_half.append(prob+"\n");
		}

		rand_true.close();
		rand_false.close();
		rand_false_half.close();
		
		//it5
		Collections.sort(probList_rand_true_it5);
		Collections.sort(probList_rand_false_it5);
		Collections.sort(probList_rand_false_half_it5);

		BufferedWriter rand_true_it5= new BufferedWriter(new FileWriter(program+"/rand_true-"+seg_length+"_it5.txt"));
		BufferedWriter rand_false_it5= new BufferedWriter(new FileWriter(program+"/rand_false-"+seg_length+"_it5.txt"));
		BufferedWriter rand_false_half_it5= new BufferedWriter(new FileWriter(program+"/rand_false_half-"+seg_length+"_it5.txt"));

		for(int i = 0; i < probList_rand_true_it5.size(); i++){
			double prob = probList_rand_true_it5.get(i);
			rand_true_it5.append(prob+"\n");
		}

		for(int i = 0; i < probList_rand_false_it5.size(); i++){			
			double prob = probList_rand_false_it5.get(i);
			rand_false_it5.append(prob+"\n");
		}

		for(int i = 0; i < probList_rand_false_half_it5.size(); i++){			
			double prob = probList_rand_false_half_it5.get(i);
			rand_false_half_it5.append(prob+"\n");
		}

		rand_true_it5.close();
		rand_false_it5.close();
		rand_false_half_it5.close();
		
		//it10
		Collections.sort(probList_rand_true_it10);
		Collections.sort(probList_rand_false_it10);
		Collections.sort(probList_rand_false_half_it10);

		BufferedWriter rand_true_it10= new BufferedWriter(new FileWriter(program+"/rand_true-"+seg_length+"_it10.txt"));
		BufferedWriter rand_false_it10= new BufferedWriter(new FileWriter(program+"/rand_false-"+seg_length+"_it10.txt"));
		BufferedWriter rand_false_half_it10= new BufferedWriter(new FileWriter(program+"/rand_false_half-"+seg_length+"_it10.txt"));

		for(int i = 0; i < probList_rand_true_it10.size(); i++){
			double prob = probList_rand_true_it10.get(i);
			rand_true_it10.append(prob+"\n");
		}

		for(int i = 0; i < probList_rand_false_it10.size(); i++){			
			double prob = probList_rand_false_it10.get(i);
			rand_false_it10.append(prob+"\n");
		}

		for(int i = 0; i < probList_rand_false_half_it10.size(); i++){			
			double prob = probList_rand_false_half_it10.get(i);
			rand_false_half_it10.append(prob+"\n");
		}

		rand_true_it10.close();
		rand_false_it10.close();
		rand_false_half_it10.close();
		
		//it15
		Collections.sort(probList_rand_true_it15);
		Collections.sort(probList_rand_false_it15);
		Collections.sort(probList_rand_false_half_it15);

		BufferedWriter rand_true_it15= new BufferedWriter(new FileWriter(program+"/rand_true-"+seg_length+"_it15.txt"));
		BufferedWriter rand_false_it15= new BufferedWriter(new FileWriter(program+"/rand_false-"+seg_length+"_it15.txt"));
		BufferedWriter rand_false_half_it15= new BufferedWriter(new FileWriter(program+"/rand_false_half-"+seg_length+"_it15.txt"));

		for(int i = 0; i < probList_rand_true_it15.size(); i++){
			double prob = probList_rand_true_it15.get(i);
			rand_true_it15.append(prob+"\n");
		}

		for(int i = 0; i < probList_rand_false_it15.size(); i++){			
			double prob = probList_rand_false_it15.get(i);
			rand_false_it15.append(prob+"\n");
		}

		for(int i = 0; i < probList_rand_false_half_it15.size(); i++){			
			double prob = probList_rand_false_half_it15.get(i);
			rand_false_half_it15.append(prob+"\n");
		}

		rand_true_it15.close();
		rand_false_it15.close();
		rand_false_half_it15.close();

		
		
		//output fp fn
		BufferedWriter fp_fn = new BufferedWriter(new FileWriter(program+"/rand-fp-fn_"+seg_length+".txt"));
		//after training
		fp_fn.append("rand_hmm after learning:\n");
		fp_fn.append("probList_rand_true size: "+probList_rand_true.size()+"\n");
		fp_fn.append("fp"+"\t"+"fn-"+seg_length+"\t"+"fn-half-"+seg_length+"\t"+"threshold"+"\n");

		for(int j = 0; j<fp.length; j++){
			int threshold_index = (int) Math.ceil(probList_rand_true.size()*fp[j]);
			if(threshold_index >= probList_rand_true.size()) threshold_index = probList_rand_true.size() -1;
			double threshold = probList_rand_true.get(threshold_index);
			//System.out.println("threshold is: "+threshold);

			int miss = 0;
			for(int i = 0; i < probList_rand_false.size(); i++){		
				double prob = probList_rand_false.get(i);
				if(prob < threshold)miss++;
				//else break;
			}

			int miss_half = 0;
			for(int i = 0; i < probList_rand_false_half.size(); i++){		
				double prob = probList_rand_false_half.get(i);
				if(prob < threshold)miss_half++;
				//else break;
			}
			fp_fn.append(fp[j]+"\t"+(1-1.0*miss/probList_rand_false.size())+"\t"+(1-1.0*miss_half/probList_rand_false_half.size())+"\t"+threshold+"\n");
		}
		
		//it5
		if(!probList_rand_true_it5.isEmpty()){
			fp_fn.append("rand_hmm after 5 ierations:\n");
			fp_fn.append("probList_rand_true size: "+probList_rand_true_it5.size()+"\n");
			fp_fn.append("fp"+"\t"+"fn-"+seg_length+"\t"+"fn-half-"+seg_length+"\t"+"threshold"+"\n");

			for(int j = 0; j<fp.length; j++){
				int threshold_index = (int) Math.ceil(probList_rand_true_it5.size()*fp[j]);
				if(threshold_index >= probList_rand_true_it5.size()) threshold_index = probList_rand_true_it5.size() -1;
				double threshold = probList_rand_true_it5.get(threshold_index);
				//System.out.println("threshold is: "+threshold);

				int miss = 0;
				for(int i = 0; i < probList_rand_false_it5.size(); i++){		
					double prob = probList_rand_false_it5.get(i);
					if(prob < threshold)miss++;
					//else break;
				}

				int miss_half = 0;
				for(int i = 0; i < probList_rand_false_half_it5.size(); i++){		
					double prob = probList_rand_false_half_it5.get(i);
					if(prob < threshold)miss_half++;
					//else break;
				}
				fp_fn.append(fp[j]+"\t"+(1-1.0*miss/probList_rand_false_it5.size())+"\t"+(1-1.0*miss_half/probList_rand_false_half_it5.size())+"\t"+threshold+"\n");
			}
		}
		
		//it10
		if(!probList_rand_true_it10.isEmpty()){
			fp_fn.append("rand_hmm after 10 iterations:\n");
			fp_fn.append("probList_rand_true size: "+probList_rand_true_it10.size()+"\n");
			fp_fn.append("fp"+"\t"+"fn-"+seg_length+"\t"+"fn-half-"+seg_length+"\t"+"threshold"+"\n");

			for(int j = 0; j<fp.length; j++){
				int threshold_index = (int) Math.ceil(probList_rand_true_it10.size()*fp[j]);
				if(threshold_index >= probList_rand_true_it10.size()) threshold_index = probList_rand_true_it10.size() -1;
				double threshold = probList_rand_true_it10.get(threshold_index);
				//System.out.println("threshold is: "+threshold);

				int miss = 0;
				for(int i = 0; i < probList_rand_false_it10.size(); i++){		
					double prob = probList_rand_false_it10.get(i);
					if(prob < threshold)miss++;
					//else break;
				}

				int miss_half = 0;
				for(int i = 0; i < probList_rand_false_half_it10.size(); i++){		
					double prob = probList_rand_false_half_it10.get(i);
					if(prob < threshold)miss_half++;
					//else break;
				}
				fp_fn.append(fp[j]+"\t"+(1-1.0*miss/probList_rand_false_it10.size())+"\t"+(1-1.0*miss_half/probList_rand_false_half_it10.size())+"\t"+threshold+"\n");
			}
		}
		
		//it15
		if(!probList_rand_true_it15.isEmpty()){
			fp_fn.append("rand_hmm after 15 iterations:\n");
			fp_fn.append("probList_rand_true size: "+probList_rand_true_it15.size()+"\n");
			fp_fn.append("fp"+"\t"+"fn-"+seg_length+"\t"+"fn-half-"+seg_length+"\t"+"threshold"+"\n");

			for(int j = 0; j<fp.length; j++){
				int threshold_index = (int) Math.ceil(probList_rand_true_it15.size()*fp[j]);
				if(threshold_index >= probList_rand_true_it15.size()) threshold_index = probList_rand_true_it15.size() -1;
				double threshold = probList_rand_true_it15.get(threshold_index);
				//System.out.println("threshold is: "+threshold);

				int miss = 0;
				for(int i = 0; i < probList_rand_false_it15.size(); i++){		
					double prob = probList_rand_false_it15.get(i);
					if(prob < threshold)miss++;
					//else break;
				}

				int miss_half = 0;
				for(int i = 0; i < probList_rand_false_half_it15.size(); i++){		
					double prob = probList_rand_false_half_it15.get(i);
					if(prob < threshold)miss_half++;
					//else break;
				}
				fp_fn.append(fp[j]+"\t"+(1-1.0*miss/probList_rand_false_it15.size())+"\t"+(1-1.0*miss_half/probList_rand_false_half_it15.size())+"\t"+threshold+"\n");
			}
		}
		
		fp_fn.close();
		
		
		//average prob information for every iteration step
		BufferedWriter step_prob = new BufferedWriter(new FileWriter(program+"/rand-step-prob-"+seg_length+".txt"));
		step_prob.append("veri_prob"+"\t"+"true_prob"+"\t"+"false_prob"+"\t"+"half_prob"+"\t"+"num"+"\n");
		for (int i = 0; i < addup_rand_num.length; i++){
			step_prob.append(addup_rand_veri_prob[i]/addup_rand_num[i]+"\t" +addup_rand_true_prob[i]/addup_rand_num[i]+"\t"+addup_rand_false_prob[i]/addup_rand_num[i]+"\t"+addup_rand_false_half_prob[i]/addup_rand_num[i]+"\t"+addup_rand_num[i]+"\n");
		}
		step_prob.close();

	}

	private void train_rand_hmm(double error) throws IOException {

		System.out.println("\nrand_hmm: start training");
		BaumWelchLearner bwl = new BaumWelchLearner();
		Hmm<ObservationDiscrete<SYSCALLRAND>> tmp_hmm = new Hmm<ObservationDiscrete<SYSCALLRAND>>(state_num_rand,new OpdfDiscreteFactory<SYSCALLRAND>(SYSCALLRAND.class));
		KullbackLeiblerDistanceCalculator klc = new KullbackLeiblerDistanceCalculator();
		double distance = 1;
		int round = 0;


		double veri_prob = 0;
		double veri_prob_tmp = verify_model_rand(rand_hmm);

		double true_prob = 0, false_prob = 0, half_prob = 0;
		true_prob = get_prob_rand(rand_hmm,"true");
		false_prob = get_prob_rand(rand_hmm,"false");
		half_prob = get_prob_rand(rand_hmm, "half");
		//System.out.println("round "+round+":	true:	"+true_prob+"	false:	"+false_prob);
		addup_rand_true_prob[round] += true_prob;
		addup_rand_false_prob[round] += false_prob;
		addup_rand_false_half_prob[round] += half_prob;
		addup_rand_veri_prob[round] += veri_prob_tmp;
		addup_rand_num[round] ++;

		//learn
		//while(distance > error){
		//while(round < 30){//use this when test initial model
		while((veri_prob_tmp - veri_prob)>error || round < 5){//standard
			//System.out.println("veri_prob_tmp: "+ veri_prob_tmp + "\tveri_prob: "+veri_prob);

			//System.out.println("(veri_prob_tmp-veri_pro) is :"+(veri_prob_tmp - veri_prob));
			tmp_hmm = bwl.iterate(rand_hmm, train_sequences_rand);
			rand_hmm = tmp_hmm;
			round++;
			record_rand_hmm(round);

			true_prob = get_prob_rand(rand_hmm,"true");
			false_prob = get_prob_rand(rand_hmm,"false");
			half_prob = get_prob_rand(rand_hmm,"half");
			//System.out.println("round "+round+":	true:	"+true_prob+"	false:	"+false_prob);
			veri_prob = veri_prob_tmp;
			veri_prob_tmp = verify_model_rand(rand_hmm);


			addup_rand_true_prob[round] += true_prob;
			addup_rand_false_prob[round] += false_prob;
			addup_rand_false_half_prob[round] += half_prob;
			addup_rand_veri_prob[round] += veri_prob_tmp;
			addup_rand_num[round] ++;

		}

		System.out.println(round+" round training used");
		round_num_rand[cur_fold] = round;

		record_rand_hmm(0);

		for(int i = round+1; i <= 200; i++){
			addup_rand_true_prob[i] += true_prob;
			addup_rand_false_prob[i] += false_prob;
			addup_rand_false_half_prob[i] += half_prob;
			addup_rand_veri_prob[i] += veri_prob_tmp;
			addup_rand_num[i] ++;
		}
	}

	private double verify_model_rand(Hmm<ObservationDiscrete<SYSCALLRAND>> hmm_to_test) {

		double veri_prob = 0;
		for(int i = 0; i < veri_sequences_rand.size(); i++){
			double prob = Math.pow(hmm_to_test.probability(veri_sequences_rand.get(i)), 1.0/veri_sequences_rand.get(i).size());
			veri_prob += prob;
		}
		return (veri_prob/veri_sequences_rand.size());
	}

	private void record_hmm(int round) throws IOException {
		if(round == 5){
			for(int i = 0; i < test_sequences.size(); i++){
				double prob = Math.pow(hmm.probability(test_sequences.get(i)), 1.0/test_sequences.get(i).size());
				probList_hmm_true_it5.add(prob);
			}

			for(int i = 0; i < false_sequences.size(); i++){
				double prob = Math.pow(hmm.probability(false_sequences.get(i)), 1.0/false_sequences.get(i).size());
				probList_hmm_false_it5.add(prob);
			}

			for(int i = 0; i < false_sequences_half.size(); i++){
				double prob = Math.pow(hmm.probability(false_sequences_half.get(i)), 1.0/false_sequences_half.get(i).size());
				probList_hmm_false_half_it5.add(prob);
			}
		}
		else if(round == 10){
			for(int i = 0; i < test_sequences.size(); i++){
				double prob = Math.pow(hmm.probability(test_sequences.get(i)), 1.0/test_sequences.get(i).size());
				probList_hmm_true_it10.add(prob);
			}

			for(int i = 0; i < false_sequences.size(); i++){
				double prob = Math.pow(hmm.probability(false_sequences.get(i)), 1.0/false_sequences.get(i).size());
				probList_hmm_false_it10.add(prob);
			}

			for(int i = 0; i < false_sequences_half.size(); i++){
				double prob = Math.pow(hmm.probability(false_sequences_half.get(i)), 1.0/false_sequences_half.get(i).size());
				probList_hmm_false_half_it10.add(prob);
			}
		}
		else if(round == 15){
			for(int i = 0; i < test_sequences.size(); i++){
				double prob = Math.pow(hmm.probability(test_sequences.get(i)), 1.0/test_sequences.get(i).size());
				probList_hmm_true_it15.add(prob);
			}

			for(int i = 0; i < false_sequences.size(); i++){
				double prob = Math.pow(hmm.probability(false_sequences.get(i)), 1.0/false_sequences.get(i).size());
				probList_hmm_false_it15.add(prob);
			}

			for(int i = 0; i < false_sequences_half.size(); i++){
				double prob = Math.pow(hmm.probability(false_sequences_half.get(i)), 1.0/false_sequences_half.get(i).size());
				probList_hmm_false_half_it15.add(prob);
			}
		}
		else if(round == 0){
			for(int i = 0; i < test_sequences.size(); i++){
				double prob = Math.pow(hmm.probability(test_sequences.get(i)), 1.0/test_sequences.get(i).size());
				probList_hmm_true.add(prob);
			}

			for(int i = 0; i < false_sequences.size(); i++){
				double prob = Math.pow(hmm.probability(false_sequences.get(i)), 1.0/false_sequences.get(i).size());
				probList_hmm_false.add(prob);
			}

			for(int i = 0; i < false_sequences_half.size(); i++){
				double prob = Math.pow(hmm.probability(false_sequences_half.get(i)), 1.0/false_sequences_half.get(i).size());
				probList_hmm_false_half.add(prob);
			}
			
			//output sequences and probs for later verification
			boolean verification = false;
			if(verification == true){
				
				//output probs for later verification
				BufferedWriter output;
		        output = new BufferedWriter(new FileWriter(program+"/probList_hmm_true", true));
				for(int i = 0; i < test_sequences.size(); i++){
					double prob = Math.pow(hmm.probability(test_sequences.get(i)), 1.0/test_sequences.get(i).size());
					output.append(prob+"\n");
				}
				output.flush();
				output.close();
				
				output = new BufferedWriter(new FileWriter(program+"/probList_hmm_false", true));
				for(int i = 0; i < false_sequences.size(); i++){
					double prob = Math.pow(hmm.probability(false_sequences.get(i)), 1.0/false_sequences.get(i).size());
					output.append(prob+"\n");
				}
				output.flush();
				output.close();
				
				output = new BufferedWriter(new FileWriter(program+"/probList_hmm_false_half", true));
				for(int i = 0; i < false_sequences_half.size(); i++){
					double prob = Math.pow(hmm.probability(false_sequences_half.get(i)), 1.0/false_sequences_half.get(i).size());
					output.append(prob+"\n");
				}
				output.flush();
				output.close();
				
				
				//output seqs for later verification
				BufferedWriter output_seq;
				output_seq = new BufferedWriter(new FileWriter(program+"/probList_hmm_true_seq", true));
				for(int i = 0; i < test_sequences.size(); i++){
					int j;
					for( j = 0; j < test_sequences.get(i).size() - 1; j++){
						output_seq.append(test_sequences.get(i).get(j).toString()+",");
					}
					output_seq.append(test_sequences.get(i).get(j).toString()+"\n");
					
				}
				output_seq.flush();
				output_seq.close();
				
				
				output_seq = new BufferedWriter(new FileWriter(program+"/probList_hmm_false_seq", true));
				for(int i = 0; i < false_sequences.size(); i++){
					int j;
					for( j = 0; j < false_sequences.get(i).size() - 1; j++){
						output_seq.append(false_sequences.get(i).get(j).toString()+",");
					}
					output_seq.append(false_sequences.get(i).get(j).toString()+"\n");
				}
				output_seq.flush();
				output_seq.close();
			}
			
		}
	}

	private void record_rand_hmm(int round) throws IOException {
		if(round == 5){
			for(int i = 0; i < test_sequences_rand.size(); i++){
				double prob = Math.pow(rand_hmm.probability(test_sequences_rand.get(i)), 1.0/test_sequences_rand.get(i).size());
				probList_rand_true_it5.add(prob);
			}

			for(int i = 0; i < false_sequences_rand.size(); i++){
				double prob = Math.pow(rand_hmm.probability(false_sequences_rand.get(i)), 1.0/false_sequences_rand.get(i).size());
				probList_rand_false_it5.add(prob);
			}

			for(int i = 0; i < false_sequences_half_rand.size(); i++){
				double prob = Math.pow(rand_hmm.probability(false_sequences_half_rand.get(i)), 1.0/false_sequences_half_rand.get(i).size());
				probList_rand_false_half_it5.add(prob);
			}
		}
		else if(round == 10){
			for(int i = 0; i < test_sequences_rand.size(); i++){
				double prob = Math.pow(rand_hmm.probability(test_sequences_rand.get(i)), 1.0/test_sequences_rand.get(i).size());
				probList_rand_true_it10.add(prob);
			}

			for(int i = 0; i < false_sequences_rand.size(); i++){
				double prob = Math.pow(rand_hmm.probability(false_sequences_rand.get(i)), 1.0/false_sequences_rand.get(i).size());
				probList_rand_false_it10.add(prob);
			}

			for(int i = 0; i < false_sequences_half_rand.size(); i++){
				double prob = Math.pow(rand_hmm.probability(false_sequences_half_rand.get(i)), 1.0/false_sequences_half_rand.get(i).size());
				probList_rand_false_half_it10.add(prob);
			}
		}
		else if(round == 15){
			for(int i = 0; i < test_sequences_rand.size(); i++){
				double prob = Math.pow(rand_hmm.probability(test_sequences_rand.get(i)), 1.0/test_sequences_rand.get(i).size());
				probList_rand_true_it15.add(prob);
			}

			for(int i = 0; i < false_sequences_rand.size(); i++){
				double prob = Math.pow(rand_hmm.probability(false_sequences_rand.get(i)), 1.0/false_sequences_rand.get(i).size());
				probList_rand_false_it15.add(prob);
			}

			for(int i = 0; i < false_sequences_half_rand.size(); i++){
				double prob = Math.pow(rand_hmm.probability(false_sequences_half_rand.get(i)), 1.0/false_sequences_half_rand.get(i).size());
				probList_rand_false_half_it15.add(prob);
			}
		}
		else if(round == 0){
			for(int i = 0; i < test_sequences_rand.size(); i++){
				double prob = Math.pow(rand_hmm.probability(test_sequences_rand.get(i)), 1.0/test_sequences_rand.get(i).size());
				probList_rand_true.add(prob);
			}

			for(int i = 0; i < false_sequences_rand.size(); i++){
				double prob = Math.pow(rand_hmm.probability(false_sequences_rand.get(i)), 1.0/false_sequences_rand.get(i).size());
				probList_rand_false.add(prob);
			}

			for(int i = 0; i < false_sequences_half_rand.size(); i++){
				double prob = Math.pow(rand_hmm.probability(false_sequences_half_rand.get(i)), 1.0/false_sequences_half_rand.get(i).size());
				probList_rand_false_half.add(prob);
			}
			
			
			//output sequences and probs for later verification
			boolean verification = false;
			if(verification == true){
				
				//output probs for later verification
				BufferedWriter output;
		        output = new BufferedWriter(new FileWriter(program+"/probList_rand_true", true));
				for(int i = 0; i < test_sequences_rand.size(); i++){
					double prob = Math.pow(rand_hmm.probability(test_sequences_rand.get(i)), 1.0/test_sequences_rand.get(i).size());
					output.append(prob+"\n");
				}
				output.flush();
				output.close();
				
				output = new BufferedWriter(new FileWriter(program+"/probList_rand_false", true));
				for(int i = 0; i < false_sequences_rand.size(); i++){
					double prob = Math.pow(rand_hmm.probability(false_sequences_rand.get(i)), 1.0/false_sequences_rand.get(i).size());
					output.append(prob+"\n");
				}
				output.flush();
				output.close();
				
				output = new BufferedWriter(new FileWriter(program+"/probList_rand_false_half", true));
				for(int i = 0; i < false_sequences_half_rand.size(); i++){
					double prob = Math.pow(rand_hmm.probability(false_sequences_half_rand.get(i)), 1.0/false_sequences_half_rand.get(i).size());
					output.append(prob+"\n");
				}
				output.flush();
				output.close();
				
				
				//output seqs for later verification
				BufferedWriter output_seq;
				output_seq = new BufferedWriter(new FileWriter(program+"/probList_rand_true_seq", true));
				for(int i = 0; i < test_sequences_rand.size(); i++){
					int j;
					for( j = 0; j < test_sequences_rand.get(i).size() - 1; j++){
						output_seq.append(test_sequences_rand.get(i).get(j).toString()+",");
					}
					output_seq.append(test_sequences_rand.get(i).get(j).toString()+"\n");
					
				}
				output_seq.flush();
				output_seq.close();
				
				
				output_seq = new BufferedWriter(new FileWriter(program+"/probList_rand_false_seq", true));
				for(int i = 0; i < false_sequences_rand.size(); i++){
					int j;
					for( j = 0; j < false_sequences_rand.get(i).size() - 1; j++){
						output_seq.append(false_sequences_rand.get(i).get(j).toString()+",");
					}
					output_seq.append(false_sequences_rand.get(i).get(j).toString()+"\n");
				}
				output_seq.flush();
				output_seq.close();
			}
		}
	}

	private double verify_model(Hmm<ObservationDiscrete<SYSCALL>> hmm_to_test) {

		double veri_prob = 0;
		for(int i = 0; i < veri_sequences.size(); i++){
			double prob = Math.pow(hmm_to_test.probability(veri_sequences.get(i)), 1.0/veri_sequences.get(i).size());
			veri_prob += prob;
		}
		return (veri_prob/veri_sequences.size());
	}

	/*
	private void test_rand_hmm_initialized() {
		System.out.println("rand_hmm before learning learning:");
		double rand_before = 0;
		for(int i = 0; i < all_sequences.size(); i++){
			//System.out.println(rand_hmm.probability(train_sequences.get(i)));
			//System.out.println(Math.pow(rand_hmm.probability(train_sequences.get(i)), 1.0/train_sequences.get(i).size()));
			rand_before += Math.pow(rand_hmm.probability(all_sequences.get(i)), 1.0/all_sequences.get(i).size());
		}
		System.out.println("On average: "+ rand_before/all_sequences.size());		
	}
	 */

	private void init_rand_hmm() {
		
		double x = 1.0;
		
		state_num_rand = (int) Math.round(SYSCALLRAND.values().length * x);
		//System.out.println("rand_state_num is: " + state_num_rand);
		
		rand_hmm = new Hmm<ObservationDiscrete<SYSCALLRAND>>(state_num_rand,new OpdfDiscreteFactory<SYSCALLRAND>(SYSCALLRAND.class));
		double[] rand_state_set = new double[state_num_rand];
		//init transi
		for(int i = 0; i < state_num_rand; i++){
			for(int j = 0; j < state_num_rand; j++){
				helper.random_init_array(rand_state_set);
				rand_hmm.setAij(i, j, rand_state_set[j]);
			}
		}
		
		
		//init pi
		helper.random_init_array(rand_state_set);
		for(int i = 0; i < state_num_rand; i++){
			rand_hmm.setPi(i, rand_state_set[i]);
		}

		
		//init emission
		emit_num_rand = SYSCALLRAND.values().length;
		//System.out.println("emit_num_rand is: " + emit_num_rand);
		
		double[] rand_emit_set = new double[emit_num_rand];
		for(int i = 0; i < state_num_rand; i++){
			helper.random_init_array(rand_emit_set);

			rand_hmm.setOpdf(i, new OpdfDiscrete<SYSCALLRAND>(SYSCALLRAND.class, rand_emit_set));
		}		



		/*
		//for test
		//init emission
		//give high prob for one emission for each hidden state

		double emit_prob = 0.5;
		double[] emit = new double[state_num];
		helper.random_init_array(emit);
		for(int i = 0; i < emit_num; i++){
			emit[i] = (1-emit_prob)*emit[i];
		}
		for(int i = 0; i < state_num; i++){
			emit[i] += emit_prob;
			rand_hmm.setOpdf(i, new OpdfDiscrete<SYSCALL>(SYSCALL.class, emit));
			emit[i] -= emit_prob;
		}
		 */
	}

	private void test_hmm_trained(double[] fp) throws IOException {
		Collections.sort(probList_hmm_true);
		Collections.sort(probList_hmm_false);
		Collections.sort(probList_hmm_false_half);

		BufferedWriter hmm_true= new BufferedWriter(new FileWriter(program+"/hmm_true-"+seg_length+".txt"));
		BufferedWriter hmm_false= new BufferedWriter(new FileWriter(program+"/hmm_false-"+seg_length+".txt"));
		BufferedWriter hmm_false_half= new BufferedWriter(new FileWriter(program+"/hmm_false_half-"+seg_length+".txt"));

		for(int i = 0; i < probList_hmm_true.size(); i++){
			double prob = probList_hmm_true.get(i);
			hmm_true.append(prob+"\n");
		}

		for(int i = 0; i < probList_hmm_false.size(); i++){			
			double prob = probList_hmm_false.get(i);
			hmm_false.append(prob+"\n");
		}

		for(int i = 0; i < probList_hmm_false_half.size(); i++){			
			double prob = probList_hmm_false_half.get(i);
			hmm_false_half.append(prob+"\n");
		}

		hmm_true.close();
		hmm_false.close();
		hmm_false_half.close();

		//it5
		Collections.sort(probList_hmm_true_it5);
		Collections.sort(probList_hmm_false_it5);
		Collections.sort(probList_hmm_false_half_it5);

		BufferedWriter hmm_true_it5= new BufferedWriter(new FileWriter(program+"/hmm_true-"+seg_length+"_it5.txt"));
		BufferedWriter hmm_false_it5= new BufferedWriter(new FileWriter(program+"/hmm_false-"+seg_length+"_it5.txt"));
		BufferedWriter hmm_false_half_it5= new BufferedWriter(new FileWriter(program+"/hmm_false_half-"+seg_length+"_it5.txt"));

		for(int i = 0; i < probList_hmm_true_it5.size(); i++){
			double prob = probList_hmm_true_it5.get(i);
			hmm_true_it5.append(prob+"\n");
		}

		for(int i = 0; i < probList_hmm_false_it5.size(); i++){			
			double prob = probList_hmm_false_it5.get(i);
			hmm_false_it5.append(prob+"\n");
		}

		for(int i = 0; i < probList_hmm_false_half_it5.size(); i++){			
			double prob = probList_hmm_false_half_it5.get(i);
			hmm_false_half_it5.append(prob+"\n");
		}

		hmm_true_it5.close();
		hmm_false_it5.close();
		hmm_false_half_it5.close();

		//it10
		Collections.sort(probList_hmm_true_it10);
		Collections.sort(probList_hmm_false_it10);
		Collections.sort(probList_hmm_false_half_it10);

		BufferedWriter hmm_true_it10= new BufferedWriter(new FileWriter(program+"/hmm_true-"+seg_length+"_it10.txt"));
		BufferedWriter hmm_false_it10= new BufferedWriter(new FileWriter(program+"/hmm_false-"+seg_length+"_it10.txt"));
		BufferedWriter hmm_false_half_it10= new BufferedWriter(new FileWriter(program+"/hmm_false_half-"+seg_length+"_it10.txt"));

		for(int i = 0; i < probList_hmm_true_it10.size(); i++){
			double prob = probList_hmm_true_it10.get(i);
			hmm_true_it10.append(prob+"\n");
		}

		for(int i = 0; i < probList_hmm_false_it10.size(); i++){			
			double prob = probList_hmm_false_it10.get(i);
			hmm_false_it10.append(prob+"\n");
		}

		for(int i = 0; i < probList_hmm_false_half_it10.size(); i++){			
			double prob = probList_hmm_false_half_it10.get(i);
			hmm_false_half_it10.append(prob+"\n");
		}

		hmm_true_it10.close();
		hmm_false_it10.close();
		hmm_false_half_it10.close();

		//it15
		Collections.sort(probList_hmm_true_it15);
		Collections.sort(probList_hmm_false_it15);
		Collections.sort(probList_hmm_false_half_it15);

		BufferedWriter hmm_true_it15= new BufferedWriter(new FileWriter(program+"/hmm_true-"+seg_length+"_it15.txt"));
		BufferedWriter hmm_false_it15= new BufferedWriter(new FileWriter(program+"/hmm_false-"+seg_length+"_it15.txt"));
		BufferedWriter hmm_false_half_it15= new BufferedWriter(new FileWriter(program+"/hmm_false_half-"+seg_length+"_it15.txt"));

		for(int i = 0; i < probList_hmm_true_it15.size(); i++){
			double prob = probList_hmm_true_it15.get(i);
			hmm_true_it15.append(prob+"\n");
		}

		for(int i = 0; i < probList_hmm_false_it15.size(); i++){			
			double prob = probList_hmm_false_it15.get(i);
			hmm_false_it15.append(prob+"\n");
		}

		for(int i = 0; i < probList_hmm_false_half_it15.size(); i++){			
			double prob = probList_hmm_false_half_it15.get(i);
			hmm_false_half_it15.append(prob+"\n");
		}

		hmm_true_it15.close();
		hmm_false_it15.close();
		hmm_false_half_it15.close();


		//output fp fn
		BufferedWriter fp_fn = new BufferedWriter(new FileWriter(program+"/hmm-fp-fn_"+seg_length+".txt"));


		//after training
		fp_fn.append("hmm after training:\n");
		fp_fn.append("probList_hmm_true size: "+probList_hmm_true.size()+"\n");
		fp_fn.append("fp"+"\t"+"fn-"+seg_length+"\t"+"fn-half-"+seg_length+"\t"+"threshold\n");

		for(int j = 0; j<fp.length; j++){
			int threshold_index = (int) Math.ceil(probList_hmm_true.size()*fp[j]);
			if(threshold_index >= probList_hmm_true.size())threshold_index = probList_hmm_true.size()-1;
			double threshold = probList_hmm_true.get(threshold_index);
			//System.out.println("threshold is: "+threshold);

			int miss = 0;
			for(int i = 0; i < probList_hmm_false.size(); i++){		
				double prob = probList_hmm_false.get(i);
				if(prob < threshold)miss++;
				//else break;
			}

			int miss_half = 0;
			for(int i = 0; i < probList_hmm_false_half.size(); i++){		
				double prob = probList_hmm_false_half.get(i);
				if(prob < threshold)miss_half++;
				//else break;
			}
			fp_fn.append(fp[j]+"\t"+(1-1.0*miss/probList_hmm_false.size())+"\t"+(1-1.0*miss_half/probList_hmm_false_half.size())+"\t"+threshold+"\n");
		}

		//it5
		if(!probList_hmm_true_it5.isEmpty()){
			fp_fn.append("hmm after 5 iterations:\n");
			fp_fn.append("probList_hmm_true size: "+probList_hmm_true_it5.size()+"\n");
			fp_fn.append("fp"+"\t"+"fn-"+seg_length+"\t"+"fn-half-"+seg_length+"\t"+"threshold\n");

			for(int j = 0; j<fp.length; j++){
				int threshold_index = (int) Math.ceil(probList_hmm_true_it5.size()*fp[j]);
				if(threshold_index >= probList_hmm_true_it5.size()) threshold_index = probList_hmm_true_it5.size() - 1;
				double threshold = probList_hmm_true_it5.get(threshold_index);
				//System.out.println("threshold is: "+threshold);

				int miss = 0;
				for(int i = 0; i < probList_hmm_false_it5.size(); i++){		
					double prob = probList_hmm_false_it5.get(i);
					if(prob < threshold)miss++;
					//else break;
				}

				int miss_half = 0;
				for(int i = 0; i < probList_hmm_false_half_it5.size(); i++){		
					double prob = probList_hmm_false_half_it5.get(i);
					if(prob < threshold)miss_half++;
					//else break;
				}
				fp_fn.append(fp[j]+"\t"+(1-1.0*miss/probList_hmm_false_it5.size())+"\t"+(1-1.0*miss_half/probList_hmm_false_half_it5.size())+"\t"+threshold+"\n");
			}
		}

		//it10
		if(!probList_hmm_true_it10.isEmpty()){
			fp_fn.append("hmm after 10 iterations:\n");
			fp_fn.append("probList_hmm_true size: "+probList_hmm_true_it10.size()+"\n");
			fp_fn.append("fp"+"\t"+"fn-"+seg_length+"\t"+"fn-half-"+seg_length+"\t"+"threshold\n");

			for(int j = 0; j<fp.length; j++){
				int threshold_index = (int) Math.ceil(probList_hmm_true_it10.size()*fp[j]);
				if(threshold_index >= probList_hmm_true_it10.size()) threshold_index = probList_hmm_true_it10.size() - 1;
				double threshold = probList_hmm_true_it10.get(threshold_index);
				//System.out.println("threshold is: "+threshold);

				int miss = 0;
				for(int i = 0; i < probList_hmm_false_it10.size(); i++){		
					double prob = probList_hmm_false_it10.get(i);
					if(prob < threshold)miss++;
					//else break;
				}

				int miss_half = 0;
				for(int i = 0; i < probList_hmm_false_half_it10.size(); i++){		
					double prob = probList_hmm_false_half_it10.get(i);
					if(prob < threshold)miss_half++;
					//else break;
				}
				fp_fn.append(fp[j]+"\t"+(1-1.0*miss/probList_hmm_false_it10.size())+"\t"+(1-1.0*miss_half/probList_hmm_false_half_it10.size())+"\t"+threshold+"\n");
			}
		}

		//it15
		if(!probList_hmm_true_it15.isEmpty()){
			fp_fn.append("hmm after 15 iterations:\n");
			fp_fn.append("probList_hmm_true size: "+probList_hmm_true_it15.size()+"\n");
			fp_fn.append("fp"+"\t"+"fn-"+seg_length+"\t"+"fn-half-"+seg_length+"\t"+"threshold\n");

			for(int j = 0; j<fp.length; j++){
				int threshold_index = (int) Math.ceil(probList_hmm_true_it15.size()*fp[j]);
				if(threshold_index >= probList_hmm_true_it15.size()) threshold_index = probList_hmm_true_it15.size() - 1;
				double threshold = probList_hmm_true_it15.get(threshold_index);
				//System.out.println("threshold is: "+threshold);

				int miss = 0;
				for(int i = 0; i < probList_hmm_false_it15.size(); i++){		
					double prob = probList_hmm_false_it15.get(i);
					if(prob < threshold)miss++;
					//else break;
				}

				int miss_half = 0;
				for(int i = 0; i < probList_hmm_false_half_it15.size(); i++){		
					double prob = probList_hmm_false_half_it15.get(i);
					if(prob < threshold)miss_half++;
					//else break;
				}
				fp_fn.append(fp[j]+"\t"+(1-1.0*miss/probList_hmm_false_it15.size())+"\t"+(1-1.0*miss_half/probList_hmm_false_half_it15.size())+"\t"+threshold+"\n");
			}
		}

		fp_fn.close();

		//average prob information for every iteration step
		BufferedWriter step_prob = new BufferedWriter(new FileWriter(program+"/hmm-step-prob-"+seg_length+".txt"));
		step_prob.append("veri_prob"+"\t"+"true_prob"+"\t"+"false_prob"+"\t"+"half_prob"+"\t"+"num"+"\n");
		for (int i = 0; i < addup_hmm_num.length; i++){
			step_prob.append(addup_hmm_veri_prob[i]/addup_hmm_num[i]+"\t"+addup_hmm_true_prob[i]/addup_hmm_num[i]+"\t"+addup_hmm_false_prob[i]/addup_hmm_num[i]+"\t"+addup_hmm_false_half_prob[i]/addup_hmm_num[i]+"\t"+addup_hmm_num[i]+"\n");
		}
		step_prob.close();


	}

	private void train_hmm(double error) throws IOException {
		//train hmm
		System.out.println("\nhmm: start training");

		BaumWelchLearner bwl = new BaumWelchLearner();
		Hmm<ObservationDiscrete<SYSCALL>> tmp_hmm = new Hmm<ObservationDiscrete<SYSCALL>>(state_num,new OpdfDiscreteFactory<SYSCALL>(SYSCALL.class));
		KullbackLeiblerDistanceCalculator klc = new KullbackLeiblerDistanceCalculator();
		double distance = 1;
		int round = 0;

		double veri_prob = 0;
		double veri_prob_tmp = verify_model(hmm);	

		double true_prob = 0, false_prob = 0, half_prob = 0;
		true_prob = get_prob(hmm,"true");
		false_prob = get_prob(hmm,"false");
		half_prob = get_prob(hmm, "half");
		
		System.out.println("round "+round+":	true:	"+true_prob+"	false:	"+false_prob);
		addup_hmm_true_prob[round] += true_prob;
		addup_hmm_false_prob[round] += false_prob;
		addup_hmm_false_half_prob[round] += half_prob;
		addup_hmm_veri_prob[round] += veri_prob_tmp;
		addup_hmm_num[round] ++;

		//while(distance > error){
		//while(round < 30){//use this when test initial model or test fixed rounds of training
		while((veri_prob_tmp - veri_prob)>error){//standard
			//System.out.println("veri_prob_tmp: "+ veri_prob_tmp + "\tveri_prob: "+veri_prob);
			//System.out.println("(veri_prob_tmp-veri_pro) is :"+(veri_prob_tmp - veri_prob));
			tmp_hmm = bwl.iterate(hmm, train_sequences);
			//distance = klc.distance(tmp_hmm, hmm);
			//System.out.println(round+"th round training "+" distance: "+distance);
			hmm = tmp_hmm;
			round++;
			record_hmm(round);

			true_prob = get_prob(hmm,"true");
			false_prob = get_prob(hmm,"false");
			half_prob = get_prob(hmm, "half");
			//System.out.println("round "+round+":	true:	"+true_prob+"	false:	"+false_prob);
			veri_prob = veri_prob_tmp;
			veri_prob_tmp = verify_model(hmm);
			
			addup_hmm_true_prob[round] += true_prob;
			addup_hmm_false_prob[round] += false_prob;
			addup_hmm_false_half_prob[round] += half_prob;
			addup_hmm_veri_prob[round] += veri_prob_tmp;
			addup_hmm_num[round] ++;		

		}
		System.out.println(round+" round training used");
		round_num_hmm[cur_fold] = round;
		
		record_hmm(0);
		
		for(int i = round+1; i <= 200; i++){
			addup_hmm_true_prob[i] += true_prob;
			addup_hmm_false_prob[i] += false_prob;
			addup_hmm_false_half_prob[i] += half_prob;
			addup_hmm_veri_prob[i] += veri_prob_tmp;
			addup_hmm_num[i] ++;;
		}
	}

	private double get_prob(Hmm<ObservationDiscrete<SYSCALL>> model, String selection) {
		double total_prob = 0;
		if(selection.equals("true")){
			for(int i = 0; i < test_sequences.size(); i++){

				double prob = Math.pow(model.probability(test_sequences.get(i)), 1.0/test_sequences.get(i).size());
				total_prob += prob;
			}

			return (total_prob/test_sequences.size());
		}
		else if(selection.equals("false")){

			for(int i = 0; i < false_sequences.size(); i++){
				double prob = Math.pow(model.probability(false_sequences.get(i)), 1.0/false_sequences.get(i).size());
				total_prob += prob;
			}
			return (total_prob/false_sequences.size());
		}
		else if(selection.equals("half")){

			for(int i = 0; i < false_sequences_half.size(); i++){
				double prob = Math.pow(model.probability(false_sequences_half.get(i)), 1.0/false_sequences_half.get(i).size());
				total_prob += prob;
			}
			return (total_prob/false_sequences_half.size());
		}
		else{
			return -1;
		}
	}

	
	private double get_prob_rand(
			Hmm<ObservationDiscrete<SYSCALLRAND>> model, String selection) {
		// TODO Auto-generated method stub
		
		double total_prob = 0;
		if(selection.equals("true")){
			for(int i = 0; i < test_sequences_rand.size(); i++){

				double prob = Math.pow(model.probability(test_sequences_rand.get(i)), 1.0/test_sequences_rand.get(i).size());
				total_prob += prob;
			}

			return (total_prob/test_sequences_rand.size());
		}
		else if(selection.equals("false")){

			for(int i = 0; i < false_sequences_rand.size(); i++){
				double prob = Math.pow(model.probability(false_sequences_rand.get(i)), 1.0/false_sequences_rand.get(i).size());
				total_prob += prob;
			}
			return (total_prob/false_sequences_rand.size());
		}
		else if(selection.equals("half")){

			for(int i = 0; i < false_sequences_half_rand.size(); i++){
				double prob = Math.pow(model.probability(false_sequences_half_rand.get(i)), 1.0/false_sequences_half_rand.get(i).size());
				total_prob += prob;
			}
			return (total_prob/false_sequences_half_rand.size());
		}
		else{
			return -1;
		}
	}
	
	
	/*
	private void test_hmm_initialized() {
		//with only initialized hmm 
		System.out.println("hmm before learning, with only initialized hmm:");
		double hmm_before = 0;
		int num_zero = 0;
		int num_non_zero = 0;
		for(int i = 0; i < all_sequences.size(); i++){
			//System.out.println(Math.pow(hmm.probability(train_sequences.get(i)), 1.0/train_sequences.get(i).size()));
			double prob = Math.pow(hmm.probability(all_sequences.get(i)), 1.0/all_sequences.get(i).size());
			hmm_before += prob;
			if(prob == 0)num_zero++;
			else num_non_zero++;
		}
		System.out.println("On average: "+ hmm_before/all_sequences.size());
		System.out.println("zero: "+num_zero+"  non-zero: "+num_non_zero);
	}
	 */

	private void read_sequences(int window) throws IOException {


		//comment out after first use
		/*
		 *  seperate the traces for different pid
		if(program.equals("proftpd")||program.equals("lighttpd")){
			pre_process_trace(program);
		}
		 */


		all_sequences = new ArrayList<List<ObservationDiscrete<SYSCALL>>>();
		all_sequences_distinct = new ArrayList<List<ObservationDiscrete<SYSCALL>>>();
		veri_sequences = new ArrayList<List<ObservationDiscrete<SYSCALL>>>();
		
		all_sequences_rand = new ArrayList<List<ObservationDiscrete<SYSCALLRAND>>>();
		all_sequences_distinct_rand = new ArrayList<List<ObservationDiscrete<SYSCALLRAND>>>();
		veri_sequences_rand = new ArrayList<List<ObservationDiscrete<SYSCALLRAND>>>();

		BufferedReader br;

		String path= program+"/strace-context";
		File file=new File(path);
		File[] tempList = file.listFiles();
		
		List<ObservationDiscrete<SYSCALL>> one_seq;
		List<ObservationDiscrete<SYSCALLRAND>> one_seq_rand;
		
		for(int i = 0; i < tempList.length; i++){
			//System.out.println(tempList[i].getPath());
			//System.out.println(tempList[i].getName());
			if(tempList[i].getName().endsWith(".trace")){
				
				one_seq = new ArrayList<ObservationDiscrete<SYSCALL>>();
				one_seq_rand = new ArrayList<ObservationDiscrete<SYSCALLRAND>>();

				ObservationDiscrete<SYSCALL> e = new ObservationDiscrete<SYSCALL>(SYSCALL.valueOf("env"));
				one_seq.add(e);
				
				ObservationDiscrete<SYSCALLRAND> e_rand = new ObservationDiscrete<SYSCALLRAND>(SYSCALLRAND.valueOf("env"));
				one_seq_rand.add(e_rand);

				br = new BufferedReader(new FileReader(tempList[i].getPath()));
				while(br.ready()){
					String line = br.readLine();
					//System.out.println(line);
					if(!line.contains("@"))continue;
					String syscall = line.replace("@", "FROM");
					//System.out.println(syscall);
					boolean has = false;
					for(SYSCALL s : SYSCALL.values()){
						if(s.name().equals(syscall)){
							has = true;
							break;
						}
					}
					if(has){
						e = new ObservationDiscrete<SYSCALL>(SYSCALL.valueOf(syscall));
						one_seq.add(e);
						
						e_rand = new ObservationDiscrete<SYSCALLRAND>(SYSCALLRAND.valueOf(syscall));
						one_seq_rand.add(e_rand);
					}
					else{
						System.out.println("Oops!\t"+line);
					}
				}
				
				e = new ObservationDiscrete<SYSCALL>(SYSCALL.valueOf("env"));
				one_seq.add(e);
				
				e_rand = new ObservationDiscrete<SYSCALLRAND>(SYSCALLRAND.valueOf("env"));
				one_seq_rand.add(e_rand);
				
				if(one_seq.size() < seg_length){
					System.out.println(one_seq.size());
				}

				int head = 0, tail = window, total_seg_num = 0;			

				//System.out.println(one_seq.subList(head, tail).size());

				while(tail <= one_seq.size()){
					List<ObservationDiscrete<SYSCALL>> s = one_seq.subList(head, tail);
					

					total_seg_num++;
					if(total_seg_num%5==0){
						if(!helper.contains(veri_sequences, s))
							veri_sequences.add(one_seq.subList(head, tail));
					}
					else{
						all_sequences.add(one_seq.subList(head, tail));
						if(!helper.contains(all_sequences_distinct, s))
							all_sequences_distinct.add(one_seq.subList(head, tail));
					}
					head++;
					tail++;
				}
				
				
				head = 0; tail = window; total_seg_num = 0;
				while(tail <= one_seq_rand.size()){
					List<ObservationDiscrete<SYSCALLRAND>> s_rand = one_seq_rand.subList(head, tail);
					

					total_seg_num++;
					if(total_seg_num%5==0){
						if(!helper.contains_rand(veri_sequences_rand, s_rand))
							veri_sequences_rand.add(one_seq_rand.subList(head, tail));
					}
					else{
						all_sequences_rand.add(one_seq_rand.subList(head, tail));
						if(!helper.contains_rand(all_sequences_distinct_rand, s_rand))
							all_sequences_distinct_rand.add(one_seq_rand.subList(head, tail));
					}
					head++;
					tail++;
				}
				
			}
		}
		Collections.shuffle(all_sequences);
		Collections.shuffle(all_sequences_rand);
		
		Collections.shuffle(all_sequences_distinct);
		Collections.shuffle(all_sequences_distinct_rand);
	}

	private void init_hmm(double emit_prob) throws IOException {

		/* Step 1: init hmm using extracted data */

		BufferedReader br = new BufferedReader(new FileReader(program+"/initial-all/complete"));
		//System.out.println("SYSCALL enum size: "+SYSCALL.values().length);

		state_num = Integer.parseInt(br.readLine());
		hmm = new Hmm<ObservationDiscrete<SYSCALL>>(state_num,new OpdfDiscreteFactory<SYSCALL>(SYSCALL.class));
		String all_calls = br.readLine();
		//System.out.println("all_calls: "+all_calls);
		emit_num = all_calls.split(",").length;
		//System.out.println("emit_num is: "+emit_num);
		//System.out.println("state_num is: "+state_num);

		///*
		//init transi
		for(int i = 0; i < state_num; i++){
			String[] transProb_string = br.readLine().split("\t");
			double[] transProb = helper.smooth(transProb_string);
			//System.out.println(transProb.length);
			for(int j = 0; j < state_num; j++){
				hmm.setAij(i, j, transProb[j]);
			}
		}

		//init pi
		String[] pi_string = br.readLine().split("\t");
		double[] pi = helper.smooth(pi_string);
		for(int i = 0; i < emit_num; i++){
			hmm.setPi(i, pi[i]);
		}
		//*/



		/*
		//for test
		double[] rand_set = new double[state_num];
		//init transi
		for(int i = 0; i < state_num; i++){
			for(int j = 0; j < state_num; j++){
				helper.random_init_array(rand_set);
				hmm.setAij(i, j, rand_set[j]);
			}
		}
		//init pi
		for(int i = 0; i < emit_num; i++){
			helper.random_init_array(rand_set);
			hmm.setPi(i, rand_set[i]);
		}
		 */


		//init emission
		System.out.println(SYSCALL.values().length+"\t"+state_num);
		double[] emit = new double[state_num];
		helper.random_init_array(emit);
		for(int i = 0; i < emit_num; i++){
			emit[i] = (1-emit_prob)*emit[i];
		}
		for(int i = 0; i < state_num; i++){
			emit[i] += emit_prob;
			hmm.setOpdf(i, new OpdfDiscrete<SYSCALL>(SYSCALL.class, emit));
			emit[i] -= emit_prob;
		}
		//System.out.println(hmm);

		/*
		//generate some sequences using the initial hmm
		List<List<ObservationDiscrete<SYSCALL>>> sequences;
		sequences = generateSequences(hmm);
		for(int i = 0; i < sequences.size(); i++){
			System.out.println(sequences.get(i));
		}
		 */

	}

	private <O extends Observation> List<List<O>> generateSequences(Hmm<O> hmm){
		MarkovGenerator<O> mg = new MarkovGenerator<O>(hmm);

		List<List<O>> sequences = new ArrayList<List<O>>();
		for (int i = 0; i < 50; i++)
			sequences.add(mg.observationSequence(10));

		return sequences;
	}

	private void pre_process_trace(String app) throws IOException {

		String path= program+"/trace";
		File file=new File(path);
		File[] tempList = file.listFiles();
		HashMap<String, Integer> file_num_map = new HashMap<String, Integer>();
		int file_num = 0;
		int curr_num = 0;
		for(int i = 0; i < tempList.length; i++){
			String filename = tempList[i].getName();
			//System.out.println(filename);
			if(filename.contains("trace") && !filename.endsWith(".trace")){
				//System.out.println(filename);
				file_num_map.clear();

				BufferedReader br = new BufferedReader(new FileReader(program+"/trace/"+filename));

				while(br.ready()){
					String line = br.readLine();
					if(line.contains("resumed"))continue;
					String pid = line.substring(0, line.indexOf(' '));
					String to_append = line.substring(line.indexOf(' ')+2);
					if(file_num_map.containsKey(pid)){//already have the pid
						file_num = file_num_map.get(pid);
						BufferedWriter out = new BufferedWriter(new FileWriter(program+"/trace/"+file_num+".trace", true));//append to existing file for that pid
						out.append(to_append+"\n");
						out.close();
					}
					else{//new pid found
						curr_num++;
						file_num = curr_num;
						file_num_map.put(pid, file_num);
						BufferedWriter out = new BufferedWriter(new FileWriter(program+"/trace/"+file_num+".trace", true));//append to existing file for that pid
						out.append(to_append+"\n");
						out.close();
					}

				}	
				br.close();
			}
		}
		System.exit(0);
	}

}
