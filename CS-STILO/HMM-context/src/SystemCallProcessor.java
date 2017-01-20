import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;


public class SystemCallProcessor {
	
	BufferedReader br;		
	ArrayList<String> unistd;
	
	Map<String,String> sn;
	Map<String,String> wn;
	Map<String,String> name_map;
	
	public void get_all_syscall_names() throws IOException {
		
		/* Step 1: read system calls from unistd.h to unistd */
		read_syscall_from_unistd();
		//System.exit(0);
		
		/* Step 2: read strong name and weak name from syscalls.list files */
		read_strong_weak_name_from_syscallslist();
		
		/* Step 2.5 some adjustment */
		adjust_name();
		
		/* Step 3: add names from alias in the source file */
		add_alias_wrapper_name();
		
		/* Step 4: add system call them self to name_map */
		add_self_name();
		
		/* Step 5: print all <wrapper name, system call> pairs */
		print_all_wrapper_name();
		
		/* test */
		test();
		
	}

	private void adjust_name() {
		name_map.put("_fxstat", "fstat");
		name_map.put("_lxstat", "lstat");
		name_map.put("_xstat", "stat");
		
		name_map.put("_xmknod", "mknod");
		name_map.put("sigaction", "rt_sigaction");
		name_map.put("sigprocmask", "rt_sigprocmask");
		name_map.put("_exit", "exit_group");
		name_map.put("exit", "exit_group");
		
		name_map.put("__open_nocancel", "open");
		name_map.put("__read_nocancel", "read");
		name_map.put("__close_nocancel", "close");
		name_map.put("__write_nocancel", "write");
		name_map.put("__fcntl_nocancel", "fcntl");
		name_map.put("__connect_nocancel", "connect");
		name_map.put("__openat_nocancel", "openat");
		name_map.put("__lseek_nocancel", "lseek");
		name_map.put("__select_nocancel", "select");
		name_map.put("__epoll_wait_nocancel", "epoll_wait");
		name_map.put("__accept_nocancel", "accept");
		name_map.put("__recvmsg_nocancel", "recvmsg");
		name_map.put("__sendto_nocancel", "sendto");
		
		
	}
	
	private void test() {
		// TODO Auto-generated method stub
		//check the existence of a name (wrapper)
		//System.out.println(name_map.get("__libc_sigaction"));
		//System.out.println(unistd.contains("fstat"));
		
		
		/*
		 * following for test purpose
		 * 
		
		//see if unistd and syscalls.list contain each other's system calls
		//System.out.println("In syscalls.list, Not in unistd.h");
		Iterator<String> list_it = syscallslist.iterator();
		while(list_it.hasNext()){
			String syscall = list_it.next();
			if(!unistd.contains(syscall)){
				//System.out.println(syscall);
			}
		}

		System.out.println("In unistd.h, Not in syscalls.list");
		//system calls in syscall.h but not in glibc
		Iterator<String> unistd_it = unistd.iterator();
		while(unistd_it.hasNext()){
			String syscall = unistd_it.next();
			if(!syscallslist.contains(syscall)){
				//System.out.println(syscall);
			}
		}

		//see if a particular system call is contained in wn or sn maps
		String test = "";
		if(sn.containsKey(test)){
			System.out.println("Strong name: "+sn.get(test));
		}
		else{
			System.out.println("Strong name: NO");
		}
		if(wn.containsKey(test)){
			System.out.println("Weak name: "+wn.get(test));
		}
		else{
			System.out.println("Weak name: NO");
		}
		
		*/		
		
		/* Step 5: add name based on naming conventions */
		/*
		unistd_it = unistd.iterator();
		while(unistd_it.hasNext()){
			String syscall = unistd_it.next();
			if(!name_map.containsKey("__"+syscall)){
				System.out.println("add convention");
				name_map.put("__"+syscall, syscall);
			}
			if(!name_map.containsKey("__libc_"+syscall)){
				System.out.println("add convention");
				name_map.put("__libc_"+syscall, syscall);
			}
			if(!name_map.containsKey("__syscall_"+syscall)){
				System.out.println("add convention");
				name_map.put("__syscall_"+syscall, syscall);
			}
		}
		*/
	}

	private void print_all_wrapper_name() throws IOException {
		//let's print out all <name, syscall> pairs
				FileWriter fstream = new FileWriter("names");
				BufferedWriter name_file = new BufferedWriter(fstream);
				Iterator<String> name_it = name_map.keySet().iterator();
				while(name_it.hasNext()){
					String key = name_it.next();
					String value = name_map.get(key);
					name_file.append(key+","+value+"\n");
				}
				name_file.flush();
				name_file.close();
	}

	private void add_self_name() {
		// just add all, fine
				//Collection<String> syscall_in_map = name_map.values();
				Iterator<String> unistd_it = unistd.iterator();
				while(unistd_it.hasNext()){
					String syscall = unistd_it.next();
					if(name_map.containsKey(syscall))continue;
					name_map.put(syscall, syscall);
				}
				//done!
	}

	private void add_alias_wrapper_name() throws IOException {
		// TODO Auto-generated method stub
		//3.1. find alias names for system calls, one is system call, one is name
				br = new BufferedReader(new FileReader("alias"));
				while(br.ready()){
					String line = br.readLine();
					String first=null, second=null;
					//if(helper.checkplatform(line))continue;//filter out lines for different platforms

					if(line.contains("weak_alias")||line.contains("strong_alias")){
						//if(!line.contains("(")||!line.contains("")||!line.contains(","))continue;
						//System.out.println(line);
						first = line.substring(line.indexOf('(')+1,line.indexOf(','));
						second = line.substring(line.indexOf(',')+2,line.lastIndexOf(')'));

						//one is system call, the other is new name
						if(unistd.contains(first)){//another wrapper for system call 'first'
							//					System.out.println(line);
							if(!name_map.containsKey(second))name_map.put(second, first);
							//					System.out.println("add one!");
						}
						else if(unistd.contains(second)){//another name for system call 'second'
							//					System.out.println(line);
							if(!name_map.containsKey(first))name_map.put(first, second);
							//					System.out.println("add one!");
						}
					}		
				}
				br.close();

				//3.2. find alias names for names, both are names
				boolean all_name_connected = false;
				int round = 0;
				while(!all_name_connected){
					//System.out.println("round: "+round);
					all_name_connected = true;
					round++;
					br = new BufferedReader(new FileReader("alias"));
					while(br.ready()){
						String line = br.readLine();
						String first=null, second=null;
						//if(helper.checkplatform(line))continue;//filter out lines for different platforms

						if(line.contains("weak_alias")||line.contains("strong_alias")){
							//				System.out.println(line);
							first = line.substring(line.indexOf('(')+1,line.indexOf(','));
							second = line.substring(line.indexOf(',')+2,line.lastIndexOf(')'));
							//System.out.println(first);
							//System.out.println(second);
							
							if(!unistd.contains(first)&&!unistd.contains(second)){// none of the 2 names are system calls
								//					System.out.println(line);
								if(name_map.containsKey(first)&&name_map.containsKey(second)){
									//both names already in name_map, then do nothing
								}
								else if(name_map.containsKey(first)){
									//						System.out.println(first+" "+second);
									String syscall = name_map.get(first);
									name_map.put(second, syscall);
									all_name_connected = false;
								}
								else if(name_map.containsKey(second)){
									//						System.out.println(first+" "+second);
									String syscall = name_map.get(second);
									name_map.put(first, syscall);
									all_name_connected = false;
								}
							}

						}		
					}
					br.close();
				}
				
	}



	private void read_strong_weak_name_from_syscallslist() throws IOException {
		// TODO Auto-generated method stub
		sn = new HashMap<String,String>();
		wn = new HashMap<String,String>();
		name_map = new HashMap<String, String>();

		String loc[] = {
				
				"./unix/common/syscalls.list",
				"./unix/syscalls.list",
				"./unix/mman/syscalls.list",
				"./unix/bsd/bsd4.4/syscalls.list",
				"./unix/bsd/syscalls.list",
				"./unix/inet/syscalls.list",
				"./unix/sysv/linux/wordsize-64/syscalls.list",
				"./unix/sysv/linux/powerpc/syscalls.list",
				"./unix/sysv/linux/powerpc/powerpc64/syscalls.list",
				"./unix/sysv/linux/powerpc/powerpc32/syscalls.list",
				"./unix/sysv/linux/syscalls.list",
				"./unix/sysv/linux/sh/syscalls.list",
				"./unix/sysv/linux/sparc/syscalls.list",
				"./unix/sysv/linux/sparc/sparc32/syscalls.list",
				"./unix/sysv/linux/sparc/sparc64/syscalls.list",
				"./unix/sysv/linux/x86_64/syscalls.list",
				"./unix/sysv/linux/s390/s390-64/syscalls.list",
				"./unix/sysv/linux/s390/s390-32/syscalls.list",
				"./unix/sysv/linux/ia64/syscalls.list",
				"./unix/sysv/linux/i386/syscalls.list",
				"./unix/sysv/syscalls.list"

		};

		//System.out.println("\nreading system call names from syscalls.list files ....");
		//# File name		Caller	Syscall name	# args	Strong name		Weak names
		for(int i = 0; i<loc.length; i++){
			br = new BufferedReader(new FileReader(loc[i]));
			while(br.ready()){
				String File_name = "", Caller = "", Syscall_name = "", Args = "", Strong_name = "", Weak_name = "";
				String line = br.readLine();
				if(line.contains("#")||line.isEmpty())continue;
				//				String search = "___xstat64";
				//				if(line.contains(search))System.out.println("YES");

				String[] r = line.split("\\s");//split by whitespace
				int index = 1;
				for(int j = 0; j < r.length; j++){
					if (!r[j].isEmpty()){//empty between two white spaces
						if(index == 1){
							File_name = r[j];
							index++;
						}
						else if(index == 2){
							Caller = r[j];
							index++;
						}
						else if(index == 3){
							Syscall_name = r[j];
							index++;
							if(Caller.equals("-")||Caller.equals("EXTRA")){
								//do nothing
							}
							else{
								//System.out.println("Caller: "+Caller);
								//System.out.println(loc[i]);
								//System.out.println(line+"\n");
								if(!name_map.containsKey(r[j])&&unistd.contains(Syscall_name)){
									name_map.put(Caller, Syscall_name);
								}
							}
							//							syscallslist.add(Syscall_name);
						}
						else if(index == 4){
							Args = r[j];
							index++;
						}
						else if(index == 5){
							//only one strong name
							Strong_name = r[j];
							index++;
							if(!name_map.containsKey(r[j])&&unistd.contains(Syscall_name)){
								//if it is a system call wrapper in the OS and the name has not been stored
								name_map.put(r[j], Syscall_name);
							}
						}
						else{
							//could have multiple weak names
							if(Weak_name.equals(""))Weak_name = r[j];
							else Weak_name = Weak_name+" "+r[j];
							index++;
							if(!name_map.containsKey(r[j])&&unistd.contains(Syscall_name)){
								//if it is a system call wrapper in the OS and the name has not been stored
								name_map.put(r[j], Syscall_name);
							}
						}
					}
				}
				if(!Strong_name.equals("")){
					//if(sn.containsKey(Syscall_name))System.out.println(Syscall_name+" already! S");
					sn.put(Syscall_name, Strong_name);
				}
				if(!Weak_name.equals("")){
					//if(wn.containsKey(Syscall_name))System.out.println(Syscall_name+" already! W");
					wn.put(Syscall_name, Weak_name);
				}
				//System.out.println(File_name+"\t"+Caller+"\t"+Syscall_name+"\t"+Args+"\t"+Strong_name+"\t"+Weak_name);

			}
			br.close();
		}
	}

	private void read_syscall_from_unistd() throws IOException {
		unistd =new ArrayList<String>();
		br = new BufferedReader(new FileReader("usr-include-asm/unistd_64.h"));
		while(br.ready()){
			String line = br.readLine();
			if(!line.contains("#define __NR"))continue;
			String temp = line.substring(line.indexOf("__NR_")+5);
			//System.out.println(temp);
			String syscall = temp.split("\\s")[0];
			//System.out.println(syscall);
			unistd.add(syscall);
		}
		br.close();
	}

}
