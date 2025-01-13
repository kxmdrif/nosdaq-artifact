# -*- coding:utf-8 -*-

import os

DIR = os.path.dirname(__file__)

from scripts.stats.parser import Parser

parser = Parser()


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

