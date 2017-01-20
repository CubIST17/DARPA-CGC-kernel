import be.ac.ulg.montefiore.run.jahmm.ObservationDiscrete;


public enum SYSCALLRAND {

		
		//SIR
		//flex_v5
		env, closeFROM__close_nocancel, exit_groupFROM_exit, dup3FROMdup3, readFROM__read_nocancel, unameFROM__uname, writeFROM__write_nocancel, openFROM__open_nocancel, brkFROMbrk, munmapFROM__munmap, mmapFROMmmap64, fstatFROM__fxstat, unlinkFROMunlink;
	
		//grep_v5
		//env, closeFROM__close_nocancel, exit_groupFROM_exit, lseekFROM__lseek_nocancel, readFROM__read_nocancel, mremapFROM__mremap, unameFROM__uname, writeFROM__write_nocancel, openFROM__open_nocancel, brkFROMbrk, munmapFROM__munmap, mmapFROMmmap64, fstatFROM__fxstat;
	
		//gzip_v5
	    //env, closeFROM__close_nocancel, getdentsFROM__getdents64, exit_groupFROM_exit, statFROM__xstat, readFROM__read_nocancel, chmodFROM__chmod, unlinkFROMunlink, chownFROM__libc_chown, writeFROM__write_nocancel, rt_sigactionFROM__libc_sigaction, openFROM__open_nocancel, brkFROMbrk, unameFROM__uname, fstatFROM__fxstat, mmapFROMmmap64, openatFROM__openat_nocancel, utimeFROMutime;
	
		//sed_v7
		//env, closeFROM__close_nocancel, exit_groupFROM_exit, lseekFROM__lseek_nocancel, readFROM__read_nocancel, getpidFROMgetpid, unameFROM__uname, gettimeofdayFROMgettimeofday, fcntlFROMfcntl, renameFROMrename, writeFROM__write_nocancel, openFROM__open_nocancel, brkFROMbrk, munmapFROM__munmap, mmapFROMmmap64, fstatFROM__fxstat;
		
		//bash_v6
		//env, rt_sigprocmaskFROMsigprocmask, lseekFROM__lseek_nocancel, statFROM__xstat, setgidFROM__setgid, pipeFROM__pipe, fcntlFROMfcntl, setrlimitFROMsetrlimit, fstatFROM__fxstat, getegidFROM__getegid, openatFROM__openat_nocancel, geteuidFROM__geteuid, closeFROM__close_nocancel, getuidFROM__getuid, socketFROM__socket, dupFROMdup, accessFROM__access, openFROM__open_nocancel, unameFROM__uname, timeFROMtime, getppidFROMgetppid, getdentsFROM__getdents64, readFROM__read_nocancel, dup2FROMdup2, getrlimitFROM__getrlimit, getpidFROMgetpid, connectFROM__connect_nocancel, statfsFROM__statfs, writeFROM__write_nocancel, rt_sigactionFROM__libc_sigaction, getgroupsFROMgetgroups, munmapFROM__munmap, getpgrpFROMgetpgrp, exit_groupFROM_exit, mprotectFROMmprotect, chdirFROM__chdir, unlinkFROMunlink, getgidFROM__getgid, brkFROMbrk, mmapFROMmmap64, umaskFROMumask, setuidFROMsetuid, getrusageFROMgetrusage;
	
		//vim_v7
		//env, rt_sigprocmaskFROMsigprocmask, lseekFROM__lseek_nocancel, readFROM__read_nocancel, chmodFROM__chmod, rmdirFROM__rmdir, renameFROMrename, fstatFROM__fxstat, fchdirFROM__fchdir, openatFROM__openat_nocancel, mkdirFROMmkdir, closeFROM__close_nocancel, utimeFROMutime, statFROM__xstat, socketFROM__socket, accessFROM__access, openFROM__open_nocancel, unameFROM__uname, timeFROMtime, lstatFROM__lxstat64, ioctlFROM__ioctl, getdentsFROM__getdents64, selectFROM__select_nocancel, nanosleepFROM__nanosleep_nocancel, getpidFROMgetpid, connectFROM__connect_nocancel, writeFROM__write_nocancel, rt_sigactionFROM__libc_sigaction, munmapFROM__munmap, fsyncFROM__fsync_nocancel, getuidFROM__getuid, exit_groupFROM_exit, chdirFROM__chdir, unlinkFROMunlink, mprotectFROMmprotect, chownFROM__libc_chown, brkFROMbrk, mmapFROMmmap64, fchownFROM__fchown;		
		
		//proftpd
		//env, rt_sigprocmaskFROMsigprocmask, getsocknameFROMgetsockname, lseekFROM__lseek_nocancel, readFROM__read_nocancel, rmdirFROM__rmdir, gettimeofdayFROMgettimeofday, fchmodFROM__fchmod, shutdownFROMshutdown, dup2FROMdup2, fstatFROM__fxstat, openatFROM__openat_nocancel, geteuidFROM__geteuid, closeFROM__close_nocancel, setreuidFROMsetreuid, mkdirFROMmkdir, statFROM__xstat, socketFROM__socket, openFROM__open_nocancel, setsockoptFROM__setsockopt, sendfileFROMsendfile64, timeFROMtime, getpeernameFROMgetpeername, getegidFROM__getegid, lstatFROM__lxstat64, acceptFROM__accept_nocancel, umaskFROMumask, sendtoFROM__libc_send, getdentsFROM__getdents64, fcntlFROMfcntl, pipeFROM__pipe, selectFROM__select_nocancel, dupFROMdup, listenFROMlisten, connectFROM__connect_nocancel, alarmFROMalarm, writeFROM__write_nocancel, rt_sigactionFROM__libc_sigaction, munmapFROM__munmap, getuidFROM__getuid, exit_groupFROM_exit, capsetFROMcapset, setuidFROMsetuid, chdirFROM__chdir, setgidFROM__setgid, setgroupsFROMsetgroups, brkFROMbrk, mmapFROMmmap64, bindFROM__bind, unlinkFROMunlink, capgetFROMcapget;
		
		//nginx
		//env, sendfileFROMsendfile, writevFROMwritev, readFROM__read_nocancel, gettimeofdayFROMgettimeofday, socketFROMsocket, setsockoptFROMsetsockopt, closeFROM__close_nocancel, statFROM__xstat, epoll_waitFROM__epoll_wait_nocancel, openFROM__open_nocancel, accept4FROMaccept4, readvFROM__readv, connectFROM__connect_nocancel, writeFROM__write_nocancel, epoll_ctlFROMepoll_ctl, munmapFROM__munmap, fstatFROM_fxstat, timeFROMtime, getsockoptFROMgetsockopt, brkFROMbrk, getsocknameFROMgetsockname, mmapFROMmmap64;
		
		
		
		public ObservationDiscrete<SYSCALLRAND> observation() {
		return new ObservationDiscrete<SYSCALLRAND>(this);
	}
	
}