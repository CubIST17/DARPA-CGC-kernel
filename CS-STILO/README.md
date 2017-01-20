# CS-STILO
The implementation of STILO (STatically InitiaLized markOv) and its extension CMarkov (Context-sensitive Markov )

## Step 1: Extract CFG from a program

Folder: parse-program with [Instructions](https://github.com/yaoGroupAnomaly/CS-STILO/tree/master/parse_program)


- input: executable program
- output: the control flow graph 

parse the program to generate the control flow graph (cfg.dot) and extract all the functions in the program (all_functions)


## Step 2: Initialize Transitive Probabilities for HMM

Folder: process-graph with [Instructions](https://github.com/yaoGroupAnomaly/CS-STILO/tree/master/process_graph)

- inpput: the control flow graph 
- output: transition probability matrix   


## Step 3: Train the HMM with collected traces
We use syscall as the example.

Folder: [HMM](https://github.com/yaoGroupAnomaly/CS-STILO/tree/master/HMM) -> used by STILO (context insensitive traces)

Folder: [HMM-context](https://github.com/yaoGroupAnomaly/CS-STILO/tree/master/HMM-context) -> used by CMarkov (context sensitive traces)

For how to collect traces using strace/ltrace based on the SIR project, please refer to 
[traceCollect](https://github.com/yaoGroupAnomaly/traceCollect)

- input: traces and the initialized probabilities
- output: the trained model


# Dependencies:

- Dyninst-8.0
- jahmm-0.6.1.jar
- graphviz
- Sklearn

# Disclaimer
All implementations are only research prototypes

# Reference
If you feel these introductions helpful, citing the papers:

```
@inproceedings{xu2015probabilistic,
  title={Probabilistic program modeling for high-precision anomaly classification},
  author={Xu, Kui and Yao, Danfeng Daphne and Ryder, Barbara G and Tian, Ke},
  booktitle={2015 IEEE 28th Computer Security Foundations Symposium (CSF)},
  pages={497--511},
  year={2015}
}
```

```
@inproceedings{xusharper,
  title={A Sharper Sense of Self: Probabilistic Reasoning of Program Behaviors for Anomaly Detection with Context Sensitivity},
  author={Xu, Kui and Tian, Ke and Yao, Danfeng Daphne and Ryder, Barbara G},
  booktitle={The 47th IEEE/IFIP International Conference on Dependable Systems and Networks (DSN)},
  year={2016}
}
```
are highly aprreciated.
