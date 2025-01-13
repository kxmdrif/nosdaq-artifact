# -*- coding:utf-8 -*-

import os

DIR = os.path.dirname(__file__)

from scripts.stats.parser import Parser
import numpy as np
import pandas as pd

parser = Parser()



id2source = {}
excel_file_path = DIR + "/../Mongo_Synth_Benchmarks.xlsx"
df_src = pd.read_excel(excel_file_path, sheet_name="BenchStats", usecols="A,L", skiprows=2, nrows=110, header=None)
for row_src in df_src.itertuples():
    benchId = row_src[1].strip()
    source_raw = row_src[2].strip()
    if source_raw.startswith("StackOverflow"):
        id2source[benchId] = "StackOverflow"
    elif source_raw.startswith("Official Document"):
        id2source[benchId] = "Doc"
    elif source_raw.startswith("Twitter"):
        id2source[benchId] = "Twitter"
    elif source_raw.startswith("Kaggle"):
        id2source[benchId] = "Kaggle"
assert len(id2source) == 110


"""
query -> AST
db.aggregate([]) -> {'db.collectionaggregate': [None]}
db.aggregate([S_1]) -> {'db.collectionaggregate': [S_1]}
db.aggregate([S_1, S_2]) -> {'db.collectionaggregate': [S_1, S_2]}
db.aggregate([S_1, S_2, S_3]) -> {'db.collectionaggregate': [[S_1, S_2], S_3]}
...
db.aggregate([S_1, S_2, S_3, ..., S_n]) -> {'db.collectionaggregate': [[S_1, S_2, ..., S_(n-1)], S_n]}
"""

def count(ast):
    if isinstance(ast, list):
        return sum(count(node) for node in ast)
    elif isinstance(ast, dict):
        return sum(1 + count(value) for value in ast.values())
    elif isinstance(ast, int | float | str | None):
        return 1
    else:
        raise NotImplementedError(type(ast))

def count_stage(ast):
    lst = ast['db.collectionaggregate']
    # flatten
    if isinstance(lst[0], list):
        lst = lst[0] + [lst[1]]
    if lst[0] is None:
        return 0
    else:
        return len(lst)

# (ast_size, num_stage)
def query_size_stage(query):
    query = query.replace('\n', '').replace('\t', '').replace(' ', '').replace('\'', '\"')
    try:
        ast = parser.parse(query)
        ast_size, num_stage = count(ast), count_stage(ast)
    except Exception as err:
        print(query)
        print(err)
        exit()
    return ast_size, num_stage

# (ast_size, num_stage, P, M, L, U, G, A)
def query_datapoint(query):
    query = query.replace('\n', '').replace('\t', '').replace(' ', '').replace('\'', '\"')
    ast = parser.parse(query)
    ast_size = count(ast)
    num_stage = count_stage(ast)
    lst = ast['db.collectionaggregate']
    if isinstance(lst[0], list):
        lst = lst[0] + [lst[1]]
    if lst[0] is None:
        return ast_size, num_stage, 0, 0, 0, 0, 0, 0
    n_p = len([x for x in lst if '$project' in x])
    n_m = len([x for x in lst if '$match' in x])
    n_l = len([x for x in lst if '$lookup' in x])
    n_u = len([x for x in lst if '$unwind' in x])
    n_g = len([x for x in lst if '$group' in x])
    n_a = len([x for x in lst if '$addFields' in x])
    return ast_size, num_stage, n_p, n_m, n_l, n_u, n_g, n_a

def get_stats(lst):
    lst = np.asarray(lst).astype(np.int_)
    return {"min": min(lst), "max": max(lst), "avg": round(np.mean(lst), 2), "med": round(np.median(lst), 2)}


def get_gts():
    data = {"StackOverflow": [], "Doc": [], "Twitter": [], "Kaggle": []}
    total_data = {"Total": []}
    df = pd.read_excel(excel_file_path, sheet_name="MainEval", usecols="A,I", skiprows=2, nrows=110, header=None)
    for row in df.itertuples():
        bench_id = row[1].strip()
        query = row[2].strip()
        total_data["Total"].append(query_size_stage(query))
        match id2source[bench_id]:
            case "StackOverflow":
                data["StackOverflow"].append(query_size_stage(query))
            case "Doc":
                data["Doc"].append(query_size_stage(query))
            case "Twitter":
                data["Twitter"].append(query_size_stage(query))
            case "Kaggle":
                data["Kaggle"].append(query_size_stage(query))
            case _:
                raise NotImplementedError

    for source, sizes in data.items():
        data[source] = {
            "ast_size": get_stats([x[0] for x in sizes]),
            "num_stage": get_stats([x[1] for x in sizes])
        }
    total_data["Total"] = {
        "ast_size": get_stats([x[0] for x in total_data["Total"]]),
        "num_stage": get_stats([x[1] for x in total_data["Total"]])
    }
    return data, total_data


def get_gen():
    data = {"StackOverflow": [], "Doc": [], "Twitter": [], "Kaggle": []}
    total_data = {"Total": []}
    # maybe also consider cases where time > 300 but still synthesize a program
    # (not exists in full experiment but in other ablation study)
    FILE = os.path.join("../../result/Nosdaq/programs.txt")
    with open(FILE, 'r') as reader:
        ctx = reader.read()
        benchmarks = ctx.strip().split("\n\n")
        for benchmark in benchmarks:
            if '\n' not in benchmark:
                continue
            id, query, _ = benchmark.split('\n')
            query = query.replace('\n', '').replace('\t', '').replace(' ', '').replace('\'', '\"')
            total_data["Total"].append(query_size_stage(query))
            match id2source[id[1:]]:
                case "StackOverflow":
                    data["StackOverflow"].append(query_size_stage(query))
                case "Doc":
                    data["Doc"].append(query_size_stage(query))
                case "Twitter":
                    data["Twitter"].append(query_size_stage(query))
                case "Kaggle":
                    data["Kaggle"].append(query_size_stage(query))
                case _:
                    raise NotImplementedError
    for source, sizes in data.items():
        data[source] = {
            "ast_size": get_stats([x[0] for x in sizes]),
            "num_stage": get_stats([x[1] for x in sizes])
        }
    total_data["Total"] = {
        "ast_size": get_stats([x[0] for x in total_data["Total"]]),
        "num_stage": get_stats([x[1] for x in total_data["Total"]])
    }
    return data, total_data

def print_stat(stat):
    data, total_data = stat
    print("Source, Metric, Num_Stage, Ast_Size")
    for source in ["StackOverflow", "Doc", "Twitter", "Kaggle"]:
        for metric in ["avg", "med", "min", "max"]:
            print(f"{source}, {metric}, {data[source]['num_stage'][metric]}, {data[source]['ast_size'][metric]}")

    for metric in ["avg", "med", "min", "max"]:
        print(f"Total, {metric}, {total_data['Total']['num_stage'][metric]}, {total_data['Total']['ast_size'][metric]}")

def print_full_gt_stat():
    data = {"StackOverflow": [], "Doc": [], "Twitter": [], "Kaggle": [], "Total": []}
    df = pd.read_excel(excel_file_path, sheet_name="MainEval", usecols="A,I", skiprows=2, nrows=110, header=None)
    for row in df.itertuples():
        bench_id = row[1].strip()
        query = row[2].strip()
        dp = query_datapoint(query)
        data["Total"].append(dp)
        match id2source[bench_id]:
            case "StackOverflow":
                data["StackOverflow"].append(dp)
            case "Doc":
                data["Doc"].append(dp)
            case "Twitter":
                data["Twitter"].append(dp)
            case "Kaggle":
                data["Kaggle"].append(dp)
            case _:
                raise NotImplementedError
    for source, dps in data.items():
        data[source] = {
            "ast_size": get_stats([x[0] for x in dps]),
            "num_stage": get_stats([x[1] for x in dps]),
            "P": get_stats([x[2] for x in dps]),
            "M": get_stats([x[3] for x in dps]),
            "L": get_stats([x[4] for x in dps]),
            "U": get_stats([x[5] for x in dps]),
            "G": get_stats([x[6] for x in dps]),
            "A": get_stats([x[7] for x in dps])
        }
    print("Source, Metric, Ast_Size, Num_Stage, P, M, L, U, G, A")
    for source in ["StackOverflow", "Doc", "Twitter", "Kaggle", "Total"]:
        for metric in ["avg", "med", "min", "max"]:
            print(f"{source}, {metric}, "
                  f"{data[source]['ast_size'][metric]}, "
                  f"{data[source]['num_stage'][metric]}, "
                  f"{data[source]['P'][metric]}, "
                  f"{data[source]['M'][metric]}, "
                  f"{data[source]['L'][metric]}, "
                  f"{data[source]['U'][metric]}, "
                  f"{data[source]['G'][metric]}, "
                  f"{data[source]['A'][metric]}")





if __name__ == '__main__':
    gt_stat = get_gts()
    print("Ground Truth:")
    print_stat(gt_stat)
    gen_stat = get_gen()
    print("Synthesized:")
    print_stat(gen_stat)
    print_full_gt_stat()
