import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.util.HashMap;
import be.ac.ulg.montefiore.run.jahmm.*;
import be.ac.ulg.montefiore.run.jahmm.learn.BaumWelchLearner;
import be.ac.ulg.montefiore.run.jahmm.learn.KMeansLearner;
//import be.ac.ulg.montefiore.run.jahmm.learn.*;
import java.util.*;

/**
 * Created by hul175 on 2/24/17.
 */
public class hmm {
    private HashMap<String, Integer> sys_call_dic;
    private Vector sys_call_traces;
    private enum sys_call{
        exit_group(0),
        dup3(1),
        read(2),
        mmap(3),
        arch_prctl(4),
        write(5),
        uname(6),
        ioctl(7),
        munmap(8),
        brk(9),
        close(10),
        unlink(11),
        open(12),
        fstat(13),
        execve(14);

        private final int val;
        sys_call(int i){val = i;}
    }

    public hmm() throws java.io.IOException{
        sys_call_traces = new Vector();
        sys_call_dic = new HashMap<String, Integer>();
        BufferedReader dic_file = new BufferedReader(new FileReader("../CS-STILO/HMM/flex_v5/trace/trace.dic"));
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
        };
        File[] list = folder.listFiles(fnf);
        for(int i = 0; i < list.length; ++i){
            BufferedReader cur_file = new BufferedReader(new FileReader(list[i]));
            Vector sequence = new Vector();
            try (BufferedReader br = new BufferedReader(cur_file)){
                String line;
                while((line = br.readLine()) != null){
                    line = line.substring(0,line.indexOf('('));
                    ObservationDiscrete OD = new ObservationDiscrete(sys_call.valueOf(line));
                    sequence.add(OD);
                }
            }
            this.sys_call_traces.add(sequence);
        }
        System.out.println("System call trace sequences created.");
    }

    public static void main(String[] args)throws java.io.IOException{
        hmm myhmm = new hmm();
        myhmm.CreateSequences();

        //Class<sys_call> declaringclass = sys_call.getDeclaringClass();
        KMeansLearner kml = new KMeansLearner(15, new OpdfDiscreteFactory(sys_call.dup3.getDeclaringClass()), myhmm.sys_call_traces);
        Hmm<ObservationInteger> initHmm = kml.learn();
        BaumWelchLearner bwl = new BaumWelchLearner();//(2, factory);
        Hmm learntHmm = bwl.learn(initHmm, myhmm.sys_call_traces);
        for (int i = 0; i < 10; i++) {
            learntHmm = bwl.iterate(learntHmm, myhmm.sys_call_traces);
        }
    }



}
