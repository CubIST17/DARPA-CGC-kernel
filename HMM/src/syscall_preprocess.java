import java.io.*;

/**
 * Created by hul175 on 2/10/17.
 */
public class syscall_preprocess {
    public static void main(String [] args)throws IOException{
        File file = new File("../CS-STILO/HMM/bash_v6/trace/0.trace");
        try (BufferedReader br = new BufferedReader(new FileReader(file))){
            String line;
            while((line = br.readLine()) != null){
                line = line.substring(0,line.indexOf('('));
                System.out.println(line);
            }
        }
    }
}
