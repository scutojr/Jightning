#!/usr/bin/env python

import os
import json
import os.path as op
from collections import defaultdict


CWD = op.abspath(op.dirname(__file__))

cmds = defaultdict(list)


def is_matched(f):
    return not op.isdir(f) and f.endswith('.json')


def build(params, response):
    return {
        'params': params,
        'response': response
    }


def _extract_params(kv_str):
    params = {}
    if not kv_str:
        return params
    kvs = kv_str.split(',')
    for kv in kvs:
        k, v = kv.split('=')
        params[k] = v
    return params


def transform(src_file):
    file_name = op.basename(src_file)
    file_name = file_name.replace('.json', '')
    i = file_name.find('(')
    if i > 0:
        method = file_name[:i]
        params = _extract_params(file_name[i+1:-1])
    else:
        method = file_name
        params = {}
    with open(src_file, 'r') as src:
        response = src.read()
        try:
            response = json.loads(response)
        except ValueError as e:
            pass
    return method, build(params, response)


def main(src_dir, dest_dir):
    for f in os.listdir(src_dir):
        if is_matched(f):
            method, data = transform(op.join(src_dir, f))
            cmds[method].append(data)
    for method, datas in cmds.iteritems():
	    with open(op.join(dest_dir, method + '.json'), 'w') as sink:
		json.dump(datas, sink, indent=4)


if __name__ == '__main__':
    src = op.join(CWD, 'test_data')
    dest = op.join(CWD, 'test_data/target')
    main(src, dest)

