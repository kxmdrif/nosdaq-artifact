import json
import os
import statistics

import matplotlib.pyplot as plt
import pandas as pd
from scripts.stats.main import query_size_stage

TIME_LIMIT = 300


DUMMY_QUERY = "aggregate([match(true)])"
# [(id, time, correct)] correct = 0 or 1
def load_size_impact_data():
    if not os.path.exists(f"result_size_impact/Nosdaq"):
        return []
    no_time_out_b_bids = get_no_time_out_bench_ids_main_full()
    df = pd.read_csv(f"result_size_impact/Nosdaq/maineval.csv", dtype=str)
    ids = [int(x[9:]) for x in df.iloc[:, 0]]
    times = df.iloc[:, 1].fillna('').tolist()
    id_time_list =  [(x[0], float(x[1])) for x in list(zip(ids, times)) if not_timeout(x[1])]
    id_time_correct_list = []
    for bid, time in id_time_list:
        b_bid = bid // 1000
        # if the base benchmark timeout, treat the size benchmark as incorrect
        if b_bid not in no_time_out_b_bids:
            # id_time_correct_list.append((bid, time, 0))
            continue
        # otherwise compare the ast size, if same, treated as correct
        with open(f"result/Nosdaq/programs/benchmark{b_bid}.txt", "r") as f:
            ref_query = f.readlines()[1].strip()
        with open(f"result_size_impact/Nosdaq/programs/benchmark{bid}.txt", "r") as f:
            query = f.readlines()[1].strip()
        if query_size_stage(ref_query) == query_size_stage(query):
            id_time_correct_list.append((bid, time, 1))
        else:
            id_time_correct_list.append((bid, time, 0))
    return id_time_correct_list


def get_no_time_out_bench_ids_main_full():
    if not os.path.exists(f"result/Nosdaq"):
        return set()
    df = pd.read_csv(f"result/Nosdaq/maineval.csv", dtype=str)
    ids = [int(x[9:]) for x in df.iloc[:, 0]]
    times = df.iloc[:, 1].fillna('').tolist()
    id_time_list =  [(x[0], float(x[1])) for x in list(zip(ids, times)) if not_timeout(x[1])]
    return set([x[0] for x in id_time_list])

def load_data(folder_name):
    if not os.path.exists(f"result/{folder_name}"):
        return []
    df = pd.read_csv(f"result/{folder_name}/maineval.csv", dtype=str)
    times = df.iloc[:, 1].fillna('').tolist()
    return times


def not_timeout(time):
    return not (len(time) == 0 or float(time) > TIME_LIMIT)


def load_all_data():
    both_data = [float(x) for x in load_data("Nosdaq") if not_timeout(x)]
    ab_length_data = [float(x) for x in load_data("Nosdaq_No_Length") if not_timeout(x)]
    ab_type_data = [float(x) for x in load_data("Nosdaq_No_Type") if not_timeout(x)]
    ab_both_data = [float(x) for x in load_data("Nosdaq_No_Both") if not_timeout(x)]
    return both_data, ab_length_data, ab_type_data, ab_both_data


def ablation_study():
    both_data, ab_length_data, ab_type_data, ab_both_data = load_all_data()
    both_data.sort()
    ab_length_data.sort()
    ab_type_data.sort()
    ab_both_data.sort()
    x_both = [x for x in range(1, 1 + len(both_data))]
    y_both = both_data

    x_ab_length = [x for x in range(1, 1 + len(ab_length_data))]
    y_ab_length = ab_length_data

    x_ab_type = [x for x in range(1, 1 + len(ab_type_data))]
    y_ab_type = ab_type_data

    x_ab_both = [x for x in range(1, 1 + len(ab_both_data))]
    y_ab_both = ab_both_data

    fig = plt.figure(figsize=(10, 5))
    ax = fig.add_subplot(111)
    plt.subplots_adjust(top=.96, bottom=0.12, right=.98, left=0.09)
    ax.spines['right'].set_visible(False)
    ax.spines['top'].set_visible(False)
    ax.set_yticks(range(0, 350, 50))
    ax.margins(0.05)
    ax.set_ylim(0, 300)
    ax.set_xticks(list(range(0, 120, 20)) + [110])
    ax.set_xlim(0, 110)

    # Plot each series
    plt.plot(x_ab_both, y_ab_both, label='Nosdaq w/o Size & Type', marker='*', color="#e41a1c")
    plt.plot(x_ab_type, y_ab_type, label='Nosdaq w/o Type', marker='^', color="#4daf4a")
    plt.plot(x_ab_length, y_ab_length, label='Nosdaq w/o Size', marker='s', color="#984ea3")
    plt.plot(x_both, y_both, label='Nosdaq', marker='o', color="#377eb8")

    plt.xticks(fontsize=16)
    plt.yticks(fontsize=16)
    plt.grid(linestyle="--", linewidth=0.3)
    # Add labels and title
    plt.xlabel('#Solved benchmarks', fontsize=20)
    plt.ylabel('Time (s)', fontsize=20)

    # Add a legend
    handles, labels = plt.gca().get_legend_handles_labels()
    plt.legend(handles[::-1], labels[::-1], loc="upper left", fontsize=16, )

    plt.savefig("fig-ablation-study.pdf", transparent=True)
    plt.savefig("fig-ablation-study.png")
    # Display the plot
    plt.show()


def baseline():
    timecosts = []
    with open("baselines/eusolver/result.out", 'r') as reader:
        for line in reader:
            line = json.loads(line)
            if line['time'] < 300:
                timecosts.append(line['time'])
    timecosts.sort()
    x_timecosts = list(range(1, 1 + len(timecosts)))

    both_data, _, _, _ = load_all_data()
    both_data.sort()
    x_both = [x for x in range(1, 1 + len(both_data))]
    y_both = both_data

    fig = plt.figure(figsize=(10, 3))
    ax = fig.add_subplot(111)
    plt.subplots_adjust(top=.96, bottom=0.2, right=.98, left=0.09)
    ax.spines['right'].set_visible(False)
    ax.spines['top'].set_visible(False)
    ax.set_yticks(range(0, 350, 50))
    ax.margins(0.05)
    ax.set_ylim(0, 300)
    ax.set_xticks(list(range(0, 120, 20)) + [110])
    ax.set_xlim(0, 110)

    # Plot each series
    plt.plot(x_timecosts, timecosts, label='EUSolver', marker='*', color="#66a61e")
    plt.plot(x_both, y_both, label='Nosdaq', marker='o', color="#377eb8")

    plt.xticks(fontsize=16)
    plt.yticks(fontsize=16)
    plt.grid(linestyle="--", linewidth=0.3)
    # Add labels and title
    plt.xlabel('#Solved benchmarks', fontsize=20)
    plt.ylabel('Time (s)', fontsize=20)

    # Add a legend
    handles, labels = plt.gca().get_legend_handles_labels()
    plt.legend(handles[::-1], labels[::-1], loc="upper left", fontsize=16, ncol=2)

    plt.savefig("fig-baseline.pdf", transparent=True)
    plt.savefig("fig-baseline.png")
    # Display the plot
    plt.show()

BENCHMARK_COUNT = 110
BASE_BID = [x for x in range(1, 1 + BENCHMARK_COUNT)]
def collection_size_exp():
    size_impact_data = load_size_impact_data()
    if len(size_impact_data) == 0:
        return
    data_dict = {}
    agg_data_dict = {}
    for bid, time, correct in size_impact_data:
        size = bid % 1000 + 1
        b_bid = bid // 1000
        if b_bid not in data_dict:
            data_dict[b_bid] = [(size, time, correct)]
        else:
            data_dict[b_bid].append((size, time, correct))

        if size not in agg_data_dict:
            agg_data_dict[size] = [(time, correct, bid)]
        else:
            agg_data_dict[size].append((time, correct, bid))

    # Time part
    xs = sorted(agg_data_dict.keys())
    ys_avg = [sum([y[0] for y in agg_data_dict[x]]) / len(agg_data_dict[x]) for x in xs]
    ys_med = [statistics.median([y[0] for y in agg_data_dict[x]]) for x in xs]
    fig = plt.figure(figsize=(10, 3))
    ax = fig.add_subplot(111)
    plt.subplots_adjust(top=.96, bottom=0.2, right=.98, left=0.09)
    ax.spines['right'].set_visible(False)
    ax.spines['top'].set_visible(False)
    ax.margins(0.05)
    ax.set_yticks(range(0, 31, 5))
    ax.set_ylim(0, 30)
    ax.set_xlim(0, 11)
    plt.plot(xs, ys_avg, label='Average', marker='*', color="#66a61e")
    plt.plot(xs, ys_med, label='Median', marker='o', color="#377eb8")
    plt.xticks(fontsize=16)
    plt.yticks(fontsize=16)
    plt.grid(linestyle="--", linewidth=0.3)
    # Add labels and title
    plt.xlabel('#Collection size', fontsize=20)
    plt.ylabel('Time (s)', fontsize=20)

    # Add a legend
    handles, labels = plt.gca().get_legend_handles_labels()
    plt.legend(handles[::-1], labels[::-1], loc="center right", fontsize=16, ncol=2)

    plt.savefig("fig-collection_size-impact-time.pdf", transparent=True)
    plt.savefig("fig-collection_size-impact-time.png")
    # Display the plot
    plt.show()


    # Rate part
    ys_plausible_rate = [100 * len(agg_data_dict[x]) / BENCHMARK_COUNT for x in xs]
    ys_desired_rate = [100 * sum([y[1] for y in agg_data_dict[x]]) / len(agg_data_dict[x]) for x in xs]

    fig = plt.figure(figsize=(10, 3))
    ax = fig.add_subplot(111)
    plt.subplots_adjust(top=.96, bottom=0.2, right=.98, left=0.09)
    ax.spines['right'].set_visible(False)
    ax.spines['top'].set_visible(False)
    ax.margins(0.05)
    ax.set_yticks(range(50, 101, 10))
    ax.set_ylim(50, 100)
    ax.set_xlim(0, 11)


    # Plot each series
    plt.plot(xs, ys_plausible_rate, label='Plausible', marker='*', color="#66a61e")
    plt.plot(xs, ys_desired_rate, label='Desired', marker='o', color="#377eb8")

    plt.xticks(fontsize=16)
    plt.yticks(fontsize=16)
    plt.grid(linestyle="--", linewidth=0.3)
    # Add labels and title
    plt.xlabel('#Collection size', fontsize=20)
    plt.ylabel('Rate (%)', fontsize=20)

    # Add a legend
    handles, labels = plt.gca().get_legend_handles_labels()
    plt.legend(handles[::-1], labels[::-1], loc="lower right", fontsize=16, ncol=2)

    plt.savefig("fig-collection_size-impact-rate.pdf", transparent=True)
    plt.savefig("fig-collection_size-impact-rate.png")
    # Display the plot
    plt.show()




if __name__ == "__main__":
    ablation_study()
    baseline()
    collection_size_exp()
