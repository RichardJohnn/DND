from langgen import *
import json
import sys

def loadSamplesFromFile(path):
    with open(path) as f:
        return f.readlines()

def generateNames(samples, n):
    language = Language.language_from_samples(samples)
    return json.dumps(
            [{'name': language.name()} for i in range(n)])

samples = sys.argv[2:]
n       = int(sys.argv[1])
print generateNames(samples, n)
