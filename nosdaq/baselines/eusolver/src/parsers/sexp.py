# sexpParser.py
#
# Demonstration of the pyparsing module, implementing a simple S-expression
# parser.
#
# Updates:
#  November, 2011 - fixed errors in precedence of alternatives in simpleString;
#      fixed exception raised in verifyLen to properly signal the input string 
#      and exception location so that markInputline works correctly; fixed 
#      definition of decimal to accept a single '0' and optional leading '-'
#      sign; updated tests to improve parser coverage
#
# Copyright 2007-2011, by Paul McGuire
#


# The current file is a modified version by Garvit Juniwal (garvitjuniwal@eecs.berkeley.edu)
# The changes are mostly related to the peculiarities of the SyGuS format S-expressions.
"""
BNF reference: http://theory.lcs.mit.edu/~rivest/sexp.txt

<sexp>      :: <string> | <list>
<string>    :: <display>? <simple-string> ;
<simple-string> :: <raw> | <token> | <base-64> | <hexadecimal> | 
                   <quoted-string> ;
<display>   :: "[" <simple-string> "]" ;
<raw>       :: <decimal> ":" <bytes> ;
<decimal>   :: <decimal-digit>+ ;
        -- decimal numbers should have no unnecessary leading zeros
<bytes>     -- any string of bytes, of the indicated length
<token>     :: <tokenchar>+ ;
<base-64>   :: <decimal>? "|" ( <base-64-char> | <whitespace> )* "|" ;
<hexadecimal>   :: "#" ( <hex-digit> | <white-space> )* "#" ;
<quoted-string> :: <decimal>? <quoted-string-body>  
<quoted-string-body> :: "\"" <bytes> "\""
<list>      :: "(" ( <sexp> | <whitespace> )* ")" ;
<whitespace>    :: <whitespace-char>* ;
<token-char>    :: <alpha> | <decimal-digit> | <simple-punc> ;
<alpha>         :: <upper-case> | <lower-case> | <digit> ;
<lower-case>    :: "a" | ... | "z" ;
<upper-case>    :: "A" | ... | "Z" ;
<decimal-digit> :: "0" | ... | "9" ;
<hex-digit>     :: <decimal-digit> | "A" | ... | "F" | "a" | ... | "f" ;
<simple-punc>   :: "-" | "." | "/" | "_" | ":" | "*" | "+" | "=" ;
<whitespace-char> :: " " | "\t" | "\r" | "\n" ;
<base-64-char>  :: <alpha> | <decimal-digit> | "+" | "/" | "=" ;
<null>          :: "" ;
"""

from base64 import b64decode

from pyparsing import \
    Suppress, \
    Regex, \
    Word, \
    hexnums, \
    printables, \
    Group, \
    alphanums, \
    Optional, \
    OneOrMore, \
    dblQuotedString, \
    Forward, \
    ZeroOrMore, \
    ParseFatalException


def verifyLen(s, l, t):
    t = t[0]
    if t.len is not None:
        t1len = len(t[1])
        if t1len != t.len:
            raise ParseFatalException(s, l, \
                                      "invalid data of length %d, expected %s" % (t1len, t.len))
    return t[1]


def stripDoubleQuotes(x):
    if x[0] == '"' and x[-1] == '"':
        return x[1:-1]
    else:
        raise NotImplementedError


import json
from frozendict import frozendict
import re
import datetime

NULL = "null"

def frozenDoc(doc):
    if isinstance(doc, dict):
        for k, v in doc.items():
            doc[k] = frozenDoc(v)
        return frozendict(doc)
    elif isinstance(doc, list):
        doc = [frozenDoc(d) for d in doc]
        return tuple(doc)
    elif isinstance(doc, str) and re.match(DATE_REGEX, doc) is not None:
        data_info = re.findall(NUM_REGEX, doc)
        data_info = list(map(int, data_info))
        return datetime.datetime(*data_info)
    elif doc is None:
        return NULL
    else:
        return doc


def handDoc(x):
    x = json.loads(x)
    return frozenDoc(x)


# define punctuation literals
LPAR, RPAR, LBRK, RBRK, LBRC, RBRC, VBAR = map(Suppress, "()[]{}|")

decimal = Regex(r'-?0|-?[0-9]\d*').setParseAction(lambda t: ('Int', int(t[0])))
hexadecimal = ("#x" + Word(hexnums)) \
    .setParseAction(lambda t: (['BitVec', ('Int', 4 * len(t[1]))], int("".join(t[1:]), 16)))
bytes = Word(printables)
raw = Group(decimal("len") + Suppress(":") + bytes).setParseAction(verifyLen)
token = Word(alphanums + "-./_:*+=")
base64_ = Group(Optional(decimal | hexadecimal, default=None)("len") + VBAR
                + OneOrMore(Word(alphanums + "+/=")).setParseAction(lambda t: b64decode("".join(t)))
                + VBAR).setParseAction(verifyLen)

qString = Group(Optional(decimal, default=None)("len") +
                dblQuotedString.setParseAction(lambda s: ('String', stripDoubleQuotes(s[0])))).setParseAction(verifyLen)
simpleString = base64_ | raw | decimal | token | hexadecimal | qString

# extended definitions

real = Regex(r"[+-]?\d+\.\d*([eE][+-]?\d+)?").setParseAction(lambda tokens: float(tokens[0]))
token = Word(alphanums + "-./_:*+=!<>").setParseAction(lambda t: ('Bool', 'true') if t[0] == 'true' else \
    ('Bool', 'false') if t[0] == 'false' else t)
# doc_ = Regex(r'\{(?:[^{}]|(?R))*\}|\[(?:[^\[\]]|(?R))*\]').setParseAction(lambda tokens: ("DOC", handDoc(tokens[0])))
# doc_ = Regex(r'^<DOC>.*</DOC>').setParseAction(lambda tokens: ("DOC", handDoc(tokens[0])))
doc_ = Regex(r'\{"input":.*,"output":.*,"constants":.*,"databases":\{.*?\}\}').setParseAction(
    lambda tokens: ("Doc", handDoc(tokens[0])))
DATE_REGEX = r"^(\d{4}-\d{2}-\d{2}T\d{2}:\d{2}:\d{2}(\.\d+)?Z)$"
NUM_REGEX = r"\d+"

simpleString = real | base64_ | raw | decimal | token | hexadecimal | qString | doc_

display = LBRK + simpleString + RBRK
string_ = Optional(display) + simpleString

sexp = Forward()
sexpList = Group(LPAR + ZeroOrMore(sexp) + RPAR)
sexp << (string_ | sexpList)

######### Test data ###########
test00 = """(snicker "abc" (#03# |YWJj|))"""
test01 = """(certificate
 (issuer
  (name
   (public-key
    rsa-with-md5
    (e 15 |NFGq/E3wh9f4rJIQVXhS|)
    (n |d738/4ghP9rFZ0gAIYZ5q9y6iskDJwASi5rEQpEQq8ZyMZeIZzIAR2I5iGE=|))
   aid-committee))
 (subject
  (ref
   (public-key
    rsa-with-md5
    (e |NFGq/E3wh9f4rJIQVXhS|)
    (n |d738/4ghP9rFZ0gAIYZ5q9y6iskDJwASi5rEQpEQq8ZyMZeIZzIAR2I5iGE=|))
   tom
   mother))
 (not-before "1997-01-01_09:00:00")
 (not-after "1998-01-01_09:00:00")
 (tag
  (spend (account "12345678") (* numeric range "1" "1000"))))
"""
test02 = """(lambda (x) (* x x))"""
test03 = """(def length
   (lambda (x)
      (cond
         ((not x) 0)
         (   t   (+ 1 (length (cdr x))))
      )
   )
)
"""
test04 = """(2:XX "abc" (#03# |YWJj|))"""
test05 = """(if (is (window_name) "XMMS") (set_workspace 2))"""
test06 = """(if
  (and
    (is (application_name) "Firefox")
    (or
      (contains (window_name) "Enter name of file to save to")
      (contains (window_name) "Save As")
      (contains (window_name) "Save Image")
      ()
    )
  )
  (geometry "+140+122")
)
"""
test07 = """(defun factorial (x)
   (if (zerop x) 1
       (* x (factorial (- x 1)))))
       """
test51 = """(2:XX "abc" (#30# |YWJj|))"""
# test51error = """(3:XX "abc" (#30# |YWJj|))"""

test52 = """ 
    (and 
      (or (> uid 1000) 
          (!= gid 20) 
      ) 
      (> quota 5.0e+03) 
    ) 
    """
test53 = """
((set-logic BV)

(define-fun hd01 ((x (BitVec 32))) (BitVec 32) (bvand x (bvsub x #x00000001)))

(synth-fun f ((x (BitVec 32))) (BitVec 32)
    ((Start (BitVec 32) ((bvand Start Start)
                         (bvsub Start Start)
                         x
                         #x00000001))))

(declare-var x (BitVec 32))
(constraint (= (hd01 x) (f x)))
(check-synth)
)
"""

test54 = """
(hex false)
"""
# # Run tests
# t = None
# #alltests = [ locals()[t] for t in sorted(locals()) if t.startswith("test") ]
# alltests = [test53, test54]

# for t in alltests:
#     print '-'*50
#     print t
#     try:
#         sexpr = sexp.parseString(t, parseAll=True)
#         pprint.pprint(sexpr.asList())
#     except ParseFatalException, pfe:
#         print "Error:", pfe.msg
#         print pfe.markInputline('^')
#     print
