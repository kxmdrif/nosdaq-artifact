# -*- coding:utf-8 -*-
import os.path

import sys
from lark import Lark, Transformer

grammar = r"""
?queries : query+
query: head "(" stages ")" ";"?
head : var ("." var)+
stages : value

value: empty_dict | dict
      | dicts
      | list | empty_list
      | string
      | var
      | number
      | "true"             -> true
      | "false"            -> false
      | "null"             -> null
      | var "(" arg ("," arg)* ")" -> func

list : "[" [value ("," value)*] ","? "]"
empty_list : "[" "]"
dict : "{" [pair ("," pair)*] ","? "}"
empty_dict : "{" "}"
pair : (string | var | dvar) ":" value
dicts : dict ("," dict)*
arg : number | string | var
var : ("_" | "." | LETTER)+
dvar: "$" var
string : ESCAPED_STRING
number : SIGNED_NUMBER

%import common.LETTER
%import common.ESCAPED_STRING
%import common.SIGNED_NUMBER
%import common.WS
%ignore WS
"""


class QueryTransformer(Transformer):
    def string(self, s):
        (s,) = s
        return s[1:-1]

    def number(self, n):
        (n,) = n
        return float(n)

    def LETTER(self, l):
        return str(l)

    def var(self, var):
        return ''.join(var)

    def dvar(self, var):
        return f"${var[0]}"

    def func(self, args):
        return {args[0]: args[1:]}

    def arg(self, arg):
        return arg[0]

    def dicts(self, dicts):
        return dicts

    def value(self, value):
        return value[0]

    def stages(self, stages):
        return stages[0]

    def query(self, query):
        head, stages = query
        return {head: stages}

    def queries(self, queries):
        return queries

    def head(self, head):
        return '.'.join(head)

    list = list
    pair = tuple
    dict = dict

    null = lambda self, _: None
    true = lambda self, _: True
    false = lambda self, _: False

    empty_list = lambda self, _: []
    empty_dict = lambda self, _: {}


class Parser:
    def __init__(self):
        self.parser = Lark(grammar, start='queries', lexer='basic')
        self.transformer = QueryTransformer()

    def parse(self, query):
        ast = self.parser.parse(query)
        ast = self.transformer.transform(ast)
        return ast


if __name__ == '__main__':
    parser = Parser()
    # ast = parser.parse(
    #     """
    #     db.collection.aggregate([{$group:{"_id":null,"count":{$sum:1}"averageQuantity":{$avg:"quantity"}}}])
    #     """.strip()
    # )
    # print(ast)
    for i in range(1, 111):
        if not os.path.exists(f"../../result/Nosdaq/programs/benchmark{i}.txt"):
            continue
        with open(f"../../result/Nosdaq/programs/benchmark{i}.txt", 'r') as f:
            q = f.readlines()[1].strip().replace('\n', '').replace('\t', '').replace(' ', '').replace('\'', '\"')
            try:
                ast = parser.parse(q)
            except:
                print(f"ERROR benchmark{i}")
