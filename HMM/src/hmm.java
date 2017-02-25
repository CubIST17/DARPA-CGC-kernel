import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.nio.Buffer;
import java.util.HashMap;
import be.ac.ulg.montefiore.run.jahmm.*;
//import be.ac.ulg.montefiore.run.jahmm.learn.*;
import java.util.*;

/**
 * Created by hul175 on 2/24/17.
 */
public class hmm {
    private HashMap<String, Integer> sys_call_dic;
    private Vector sys_call_traces;

    public hmm ()throws java.io.IOException{
        sys_call_traces = new Vector();
        sys_call_dic = new HashMap<String, Integer>();
        BufferedReader dic_file = new BufferedReader(new FileReader("../HMM/flex_v5/trace/trace.dic"));
        String line;
        while((line = dic_file.readLine()) != null){
            String parts[] = line.split(" ");
            sys_call_dic.put(parts[0], Integer.parseInt(parts[1]));
        }
        dic_file.close();
        System.out.println("Dictionary built.");
    }

    public void CreateSequences() throws java.io.IOException{
        File folder = new File("../CS-STILO/HMM/flex_v5/trace");
        FilenameFilter fnf = new FilenameFilter() {
            @Override
            public boolean accept(File file, String s) {
                return s.toLowerCase().endsWith(".trace");
            }
        }
        File[] list = folder.listFiles(fnf);
        for(int i = 0; i < list.length; ++i){
            BufferedReader cur_file = new BufferedReader(new FileReader(folder + list[i]));
            Vector sequence = new Vector();
            try (BufferedReader br = new BufferedReader(new FileReader(cur_file))){
                String line;
                while((line = br.readLine()) != null){
                    line = line.substring(0,line.indexOf('('));
                    ObservationDiscrete<Enum<E>> OD = new ObservationDiscrete<E>(new Enum<E> (line, sys_call_dic.get(line)));
                    sequence.add(OD);
                }
            }
            this.sys_call_traces.add(sequence);
        }
        System.out.println("System call trace sequences created.");
    }

    public static main(String[] args)throws java.io.IOException{
        hmm myhmm = new hmm();
        myhmm.CreateSequences();

    }



}
