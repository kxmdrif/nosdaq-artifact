import argparse
from pathlib import Path
import subprocess
import shutil
import os 
from concurrent.futures import ProcessPoolExecutor, as_completed

BENCHMARK_COUNT = 110
MAIN_BID_LIST = [x for x in range(1, 1 + BENCHMARK_COUNT)]
SIZE_IMPACT_BID_LIST = [1000 * x + i for x in MAIN_BID_LIST for i in range(10)]
# serial run lookup benchmarks to avoid multi thread writing same foreign collection
LOOKUP_BID_LIST = [43, 52]


def get_folder(result_folder, mode):
    if result_folder is None:
        raise ValueError("result_folder cannot be None")
    folder_map = {
        "FULL": f"{result_folder}/Nosdaq",
        "NO_LENGTH": f"{result_folder}/Nosdaq_No_Length",
        "NO_TYPE": f"{result_folder}/Nosdaq_No_Type",
        "NO_BOTH": f"{result_folder}/Nosdaq_No_Both"
    }
    return folder_map[mode]

def init(result_folder, mode):
    base_dir = get_folder(result_folder, mode)
    if os.path.exists(base_dir):
        shutil.rmtree(base_dir) # clean before run
    Path(base_dir).mkdir(parents=True, exist_ok=True)
    Path(base_dir + "/benchstats").mkdir(parents=True, exist_ok=True)
    Path(base_dir + "/maineval").mkdir(parents=True, exist_ok=True)
    Path(base_dir + "/programs").mkdir(parents=True, exist_ok=True)
    with open(base_dir + "/benchstats.csv", "w") as f:
        f.write("ID, #Attr, Depth, #List_Attr, #Document_Attr, #Example, In_Avg_#Doc, Out_Avg_#Doc, #Const\n")
    
    with open(base_dir + "/programs.txt", "w") as f:
        f.write("")

    with open(base_dir + "/maineval.csv", "w") as f:
        f.write("ID, Time, #Sketch_Iterated, #Completed_Program, #Solution_Stage, #Solution_AST_Node\n")

    pass

def generate_report(result_folder, mode, bid_list):
    base_dir = get_folder(result_folder, mode)
    with open(base_dir + "/benchstats.csv", "a") as f:
        for i in bid_list:
            path = base_dir + f"/benchstats/benchmark{i}.csv"

            if not os.path.exists(path):
                f.write(f"benchmark{i}\n")
                continue
            with open(path, "r") as bench:
                lines = bench.readlines()
                for line in lines:
                    f.write(line)
    
    with open(base_dir + "/programs.txt", "a") as f:
        for i in bid_list:
            path = base_dir + f"/programs/benchmark{i}.txt"

            if not os.path.exists(path):
                f.write(f"#benchmark{i}\n\n\n\n")
                continue
            with open(path, "r") as bench:
                lines = bench.readlines()
                for line in lines:
                    f.write(line)


    with open(base_dir + "/maineval.csv", "a") as f:
        for i in bid_list:
            path = base_dir + f"/maineval/benchmark{i}.csv"

            if not os.path.exists(path):
                f.write(f"benchmark{i}\n")
                continue
            with open(path, "r") as bench:
                lines = bench.readlines()
                for line in lines:
                    f.write(line)

def execute_command_with_timeout(command, timeout):
    try:
        result = subprocess.run(command, shell=True, timeout=timeout, capture_output=True, text=True)
        return False, result.stdout
    except subprocess.TimeoutExpired as e:
        return True, None

def print_colored(text, color_code):
    # 31 red, 32 green, 34 blue
    print(f"\033[{color_code}m{text}\033[0m")

def run_benchmark(mode, i, timeout, experiment, result_folder):
    command = f'java -cp "lib/*:nosdaq-1.0-SNAPSHOT.jar" -Djava.library.path=/nosdaq/z3-4.12.2-x64-glibc-2.35/bin nosdaq.Main {mode} {i} {experiment} {result_folder}'
    print_colored(f"Start running benchmark{i}", 34)
    is_timeout, stdout = execute_command_with_timeout(command, timeout)
    if not is_timeout:
        print_colored(f"Run benchmark{i} success.", 32)
    else:
        print_colored(f"Run benchmark{i} timeout in {timeout} seconds", 31)
    return is_timeout, stdout


def main_experiment(result_folder, args):
    init(result_folder=result_folder, mode=args.mode)

    with ProcessPoolExecutor(max_workers=args.concurrency) as executor:
        futures = [executor.submit(run_benchmark, args.mode, i, args.timeout, args.experiment, result_folder) for i in MAIN_BID_LIST]
        for future in as_completed(futures):
            try:
                future.result()
            except Exception as exc:
                print(f'Exception: {exc}')

    generate_report(result_folder, args.mode, MAIN_BID_LIST)

def collection_size_experiment(result_folder, args):
    init(result_folder=result_folder, mode=args.mode)

    concurrent_bid_list = [1000 * x + i for x in MAIN_BID_LIST if x not in LOOKUP_BID_LIST for i in range(10)]
    serial_bid_list =  [1000 * x + i for x in LOOKUP_BID_LIST for i in range(10)]

    for i in serial_bid_list:
        try:
            run_benchmark(args.mode, i, args.timeout, args.experiment, result_folder)
        except Exception as exc:
            print(f'Exception: {exc}')

    with ProcessPoolExecutor(max_workers=args.concurrency) as executor:
        futures = [executor.submit(run_benchmark, args.mode, i, args.timeout, args.experiment, result_folder) for i in concurrent_bid_list]
        for future in as_completed(futures):
            try:
                future.result()
            except Exception as exc:
                print(f'Exception: {exc}')

    generate_report(result_folder, args.mode, SIZE_IMPACT_BID_LIST)


if __name__ == "__main__":
    parser = argparse.ArgumentParser(description="Nosdaq with mode and timeout")

    parser.add_argument('-m', '--mode', type=str, default="FULL", help='mode')
    parser.add_argument('-t', '--timeout', type=int, default="300", help='timeout')
    parser.add_argument('-c', '--concurrency', type=int, default="6", help='concurrency')
    # parser.add_argument('-c', '--concurrency', type=int, default=os.cpu_count(), help='concurrency')
    # hidden option: CUSTOM
    parser.add_argument('-e', '--experiment', type=str, default="MAIN", help='experiment type [MAIN/SIZE_IMPACT]')
    parser.add_argument('-r', '--result_folder', type=str, default=None, help='result folder name')
    args = parser.parse_args()

    if args.experiment == "MAIN":
        main_experiment("result", args)
    elif args.experiment == "SIZE_IMPACT":
        collection_size_experiment("result_size_impact", args)
    elif args.experiment == "CUSTOM":
        main_experiment(args.result_folder, args)
    else:
        print(f"Unknown experiment type: {args.experiment}")


