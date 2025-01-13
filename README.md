# Artifact Evaluation - Synthesizing Document Database Queries using Collection Abstractions

## 1 Purpose
- This artifact is used for the evaluation of paper *Synthesizing Document Database Queries using Collection Abstractions* in ICSE 2025.
- The tool in this paper is called *Nosdaq* which is a program synthesizer for synthesizing MongoDB queries from input-output excamples.
- It contains 110 benchmarks of synthesizing MongoDB queries from four datasets: StackOverflow; MongoDB Official Doucumnet; Twitter API; Kaggle.
- Badges to earn:
  - **Available**: It is public accessable in Zenodo and there is link to access to it.
  - **Functional**: This artifact is documented for use. It can generate similar evaluation results as declared in the paper. It contains all components related to the paper. There are scripts to run the benchmarks and generate the results in the associated paper.
  - **Reusable**: Source code is provided so it can be reused and repurposed.
## 2 Provenience
- Link to artifact: [https://doi.org/10.5281/zenodo.14635722](https://doi.org/10.5281/zenodo.14635722)
- Link to paper: [https://doi.org/10.48550/arXiv.2412.06102](https://doi.org/10.48550/arXiv.2412.06102)

## 3 Data
Our benchmarks are collected from four datasets
- StackOverflow
- MongoDB Official document
- Twitter API
- Kaggle

## 4 Setup
### 4.1 Hardware
- At least 6 threads for CPU
- At least 8GB memory
- At least 50GB storage
### 4.2 Operating System
- Ubuntu, Debian or:
- Ubuntu, Debian for Windows Subsystem for Linux 2 (WSL2)

### 4.3 Prerequisites
- Python 3 installed
- Docker and docker-compose installed
- Z3 solver installed (for baseline).

## 5 Usage
### 5.1 Install z3 if have not been installed
```bash
sudo apt update
sudo apt install unzip wget
cd ~
wget https://github.com/Z3Prover/z3/releases/download/z3-4.12.2/z3-4.12.2-x64-glibc-2.35.zip
unzip z3-4.12.2-x64-glibc-2.35.zip
CURRENT_DIR=$(pwd)
export Z3_HOME="${CURRENT_DIR}/z3-4.12.2-x64-glibc-2.35"
export LD_LIBRARY_PATH="$Z3_HOME/bin:$LD_LIBRARY_PATH"
```

### 5.2 Build docker image
Download tha artifact and extract `nosdaq.zip` file. Then, under the `nosdaq` directory, run:

```bash
# In the nosdaq/ directory
chmod +x nosdaq.sh && ./nosdaq.sh
```
This might take around **5 minutes**. After finishing, you will automatically enter the docker environment.

### 5.3 Run all the 110 benchmarks for the `Nosdaq` synthesizer and its related ablation studies
Now you are in a docker environment with the container's shell. Run:
```bash
python3 -m main --mode FULL --timeout 300
python3 -m main --mode NO_LENGTH --timeout 300
python3 -m main --mode NO_TYPE --timeout 300
python3 -m main --mode NO_BOTH --timeout 300
```
The result will be in the `result/` folder and different mode has different folder under `result/`.
In each mode's folder contain three folders and three files:

- `benchstats/`: benchmark statistics for each benchmark.
- `maineval/`: benchmark evaluation for each benchmark
- `programs/`: synthesized program for each benchmark.
- `benchstats.csv`: benchmark statistics.
- `maineval.csv`: benchmark evaluation (if timeout there will only be the benchmark name).
- `programs.txt`: synthesized programs in MongoDB native query format and AST format.

Depending on your hardware, this might take around **1 hour** or more or less.

### 5.4 (OPTIONAL) Experiment for collection size impact on synthesis
In the same docker environment, run:
```bash
python3 -m main --mode FULL --timeout 300 --experiment SIZE_IMPACT
```
The result will be in the `result_size_impact/` folder. Depending on the hardware, this might take around **4 hours**  or more or less. If you want to save time, you can skip it.

### 5.5 Run baseline experiments.
Exit the docker environment (typing `exit` in the container's shell). In the host operating system **(will be the same for the following steps)**, run:


```bash
# Firstly, please exit the docker environment by typing `exit` in the shell of docker
cd baselines/eusolver
sudo apt install cmake
pip3 install pyparsing z3-solver
pip3 install -r requirements.txt
chmod +x scripts/build.sh && ./scripts/build.sh
cd src
python3 __main__.py
```

The result will be dumped in the file `nosdaq/baselines/eusolver/result.out`.

 Depending on the hardware, this might take around **1 hour**  or more or less.

### 5.6 Draw the plot for abation study, baseline and collection size impact
Note:If you skipped the collection size impact experiment. You will not get related plot

In the `nosdaq` directory, run
```bash
pip3 install -r requirements.txt
python3 plot.py
```
This might take around **30 seconds** or more especially if you finished the collection size impact experiment.

The generated polt files are described as follows:

| File name | Description |
|---------------------------------------------------------------------------|-----------------|
| fig-ablation-study.pdf, fig-ablation-study.png                            | Ablation study |
| fig-baseline.pdf, fig-baseline.png | Comparasion with baseline                             |
| fig-collection_size-impact-time.pdf, fig-collection_size-impact-time.png  | Collection size impact on synthesis time            |
| fig-collection_size-impact-rate.pdf, fig-collection_size-impact-rate.png  | Collection size impact on desired and plausible rate               |



### 5.7 Data Files
The data files will be described as follows:

|   Data Directory      | Related experiment |
|-------------------|-----------------|
|  `nosdaq/result`     | `Nosdaq` synthesizer and its related ablation studies              |
|  `nosdaq/result_size_impact` | Experiment for collection size impact on synthesis            |
| `nosdaq/baselines/eusolver/result.out`| Baseline               |
| `nosdaq/Mongo_Synth_Benchmarks.xlsx`| Benchmark details      |

The table in the paper can be obtained through the above data files.




