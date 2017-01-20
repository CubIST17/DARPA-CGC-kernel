import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Random;

import be.ac.ulg.montefiore.run.jahmm.Hmm;
import be.ac.ulg.montefiore.run.jahmm.Observation;
import be.ac.ulg.montefiore.run.jahmm.ObservationDiscrete;
import be.ac.ulg.montefiore.run.jahmm.OpdfDiscrete;
import be.ac.ulg.montefiore.run.jahmm.OpdfDiscreteFactory;
import be.ac.ulg.montefiore.run.jahmm.learn.BaumWelchLearner;
import be.ac.ulg.montefiore.run.jahmm.toolbox.KullbackLeiblerDistanceCalculator;
import be.ac.ulg.montefiore.run.jahmm.toolbox.MarkovGenerator;


public class sys {

	/**
	 * @param args
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception {
		
		System.out.print("start... at Path:");
		System.out.println(new File("").getAbsoluteFile());
	    //System.out.println("find all names");
		//SystemCallProcessor sys_p = new SystemCallProcessor();
		//sys_p.get_all_syscall_names();

		//use programs from SIR  

		/* flex_v5 */
		/*
		System.out.println("test flex_v5");
		TransitionMatrixProcessor m_p_flex_v5 = new TransitionMatrixProcessor();
		m_p_flex_v5.build_transition_matrix("flex_v5");
   
		
		HiddenMarkovModelProcessor hmm_p_flex_v5 = new HiddenMarkovModelProcessor();
		boolean is_first_time = true;
	   	hmm_p_flex_v5.test_hmm("flex_v5", is_first_time);
        */

		/* grep_v5 */
		/*
		TransitionMatrixProcessor m_p_grep_v5 = new TransitionMatrixProcessor();
		m_p_grep_v5.build_transition_matrix("grep_v5");

		HiddenMarkovModelProcessor hmm_p_grep_v5 = new HiddenMarkovModelProcessor();
		boolean is_first_time = true;
		hmm_p_grep_v5.test_hmm("grep_v5", is_first_time);
        */

		/* gzip_v5 */
		/*
		TransitionMatrixProcessor m_p_gzip_v5 = new TransitionMatrixProcessor();
		m_p_gzip_v5.build_transition_matrix("gzip_v5");

		HiddenMarkovModelProcessor hmm_p_gzip_v5 = new HiddenMarkovModelProcessor();
		boolean is_first_time = true;
		hmm_p_gzip_v5.test_hmm("gzip_v5", is_first_time);
        */

		/* sed_v7 */
		//TransitionMatrixProcessor m_p_sed_v7 = new TransitionMatrixProcessor();
		//m_p_sed_v7.build_transition_matrix("sed_v7");

		//HiddenMarkovModelProcessor hmm_p_sed_v7 = new HiddenMarkovModelProcessor();
		//boolean is_first_time = true;
		//hmm_p_sed_v7.test_hmm("sed_v7", is_first_time);
		
		/* bash_v6 */
		//TransitionMatrixProcessor m_p_bash_v6 = new TransitionMatrixProcessor();
		//m_p_bash_v6.build_transition_matrix("bash_v6");

		//HiddenMarkovModelProcessor hmm_p_bash_v6 = new HiddenMarkovModelProcessor();
		//boolean is_first_time = true;
		//hmm_p_bash_v6.test_hmm("bash_v6", is_first_time);
		
		/* vim_v7 */
		//TransitionMatrixProcessor m_p_vim_v7 = new TransitionMatrixProcessor();
		//m_p_vim_v7.build_transition_matrix("vim_v7");

		//HiddenMarkovModelProcessor hmm_p_vim_v7 = new HiddenMarkovModelProcessor();
		//boolean is_first_time = true;
		//hmm_p_vim_v7.test_hmm("vim_v7", is_first_time);
		
		
		/* server programs */
		
		/* proftpd */
		//TransitionMatrixProcessor m_p_proftpd = new TransitionMatrixProcessor();
		//m_p_proftpd.build_transition_matrix("proftpd");

		//HiddenMarkovModelProcessor hmm_p_proftpd = new HiddenMarkovModelProcessor();
		//boolean is_first_time = true;
		//hmm_p_proftpd.test_hmm("proftpd", is_first_time);
		

		/* nginx */
		TransitionMatrixProcessor m_p_nginx = new TransitionMatrixProcessor();
		m_p_nginx.build_transition_matrix("nginx");

		HiddenMarkovModelProcessor hmm_p_nginx = new HiddenMarkovModelProcessor();
		boolean is_first_time = true;
		hmm_p_nginx.test_hmm("nginx", is_first_time);

	}

}
