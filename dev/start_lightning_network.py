#!/usr/bin/env python

import time
import json
import os.path as op

from _common import *

CWD = op.dirname(op.abspath(__file__))


def get_ln_addrs(containers):
    return [get_ln_addr(c) for c in containers]


def top_up(ln_containers, amount=50):
    ln_addrs = get_ln_addrs(ln_containers)

    operator = ln_containers[0]
    generate_blocks(operator, 110) # 500 btc
    for addr in ln_addrs:
        send_bitcoin(operator, addr, amount)


def create_channel(ln_containers, fund_satoshi=15*1000*1000):
    assert fund_satoshi <= 16777215, 'fund capacity can not exceed %s satoshi' % fund_satoshi
    for i, c in enumerate(ln_containers):
        next = (i + 1) % len(ln_containers)
        if next + 1 == i:
            break
        peer = ln_containers[next]
        ln_id = get_ln_id(peer)
        docker_exec(c.id, 'lightning-cli connect %s@%s' % (ln_id, peer.names))
        docker_exec(c.id, 'lightning-cli fundchannel %s %s ' % (ln_id, fund_satoshi))


def start_ln_network():
    is_new_node = False
    ln_containers = get_ln_containers()
    if ln_containers:
        for c in ln_containers:
            if 'Exited' in c.status:
                execute('docker container start ' + c.id)
    else:
        cmd = 'docker-compose.exe -f %s up -d' % op.join(CWD, 'docker', 'docker-compose.yml')
        execute(cmd)
        is_new_node = True

    return is_new_node, get_ln_containers()


def generate_node_host_mapping(ln_containers):
    node_id_to_host = {}
    for c in ln_containers:
        node_id_to_host[get_ln_id(c)] = c.names
    path = '../src/test/resources/nodeIdToHost.json'.split('/')
    with open(op.join(CWD, *path), 'w') as sink:
        json.dump(node_id_to_host, sink, indent=4)
        sink.flush()


def main():
    is_new_node, ln_containers = start_ln_network()
    if is_new_node:
        operator = ln_containers[0]
        time.sleep(30)
        top_up(ln_containers)
        confirm_all(operator)
        time.sleep(30)
        create_channel(ln_containers)
        confirm_all(ln_containers[0])
        confirm_all(ln_containers[0], 1)
        time.sleep(30)
    generate_node_host_mapping(ln_containers)


if __name__ == '__main__':
    main()
