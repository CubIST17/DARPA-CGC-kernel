
## To complile the parse function:
make

## To parse the program xxx using Shell

./run.sh -original xxx

./run.sh -static xxx

./run.sh -all xxx

## To draw the Dot graph

./dot.sh static

./dot.sh original

## To install depedences:

### 1.Download dyninst.deb

http://www.dyninst.org/downloads/archive/ 

We use teh version as 8.x Series 

libdyninst_8.1.2-1_amd64.deb and libdyninst-dev_8.1.2-1_amd64.deb 

### 2.Install dependencies:  boost、libelf、libdwarf、g++ 

1)boost 

```bash
sudo apt-cache search boost

sudo apt-get install libboost-dev
```

2)libelf

```bash
sudo apt-cache search libelf

sudo apt-get install libelf-dev
```

3)libdwarf.  Please refer:  http://askubuntu.com/questions/502749/install-libdwarf-so-on-ubuntu

Download libdwarf-20130207.tar.gz

Extract the archive and in terminal type:

```bash
cd dwarf-2013-02-07/libdwarf

./configure --enable-shared

make
```

At the end, just copy the libdawrf.so into /usr/lib


4）g++. wihout g++, you would has the error as:  cc: error trying to exec ‘cc1plus’: execvp: No such file or directory

```bash
sudo apt-get install g++
```

### 3. Install dyninst

```bash
sudo dpkg -i libdyninst_8.1.2-1_amd64.deb

sudo dpkg -i libdyninst-dev_8.1.2-1_amd64.deb
```

In our case, we build from the source by using dynInst-8.2.1.

Just follow cmake,make,make install ...

### 4. Unistall dyninst:

```bash
sudo dpkg -r libdyninst

sudo dpkg -r libdyninst-dev
```

