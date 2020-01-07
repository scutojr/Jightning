#!/usr/bin/env python

import time
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
    confirm_all(operator)


def create_channel(ln_containers, fund_satoshi=15*1000*1000):
    assert fund_satoshi <= 16777215, 'fund capacity can not exceed %s satoshi' % fund_satoshi
    for i, c in enumerate(ln_containers):
        next = (i + 1) % len(ln_containers)
        peer = ln_containers[next]
        ln_id = get_ln_id(peer)
        docker_exec(c.id, 'lightning-cli connect %s@%s' % (ln_id, peer.names))
        docker_exec(c.id, 'lightning-cli fundchannel %s %s ' % (ln_id, fund_satoshi))


def start_ln_network():
    ln_containers = get_ln_containers()
    if ln_containers:
        for c in ln_containers:
            if 'Exited' in c.status:
                execute('docker container start ' + c.id)
        return False, ln_containers
    else:
        cmd = 'docker-compose.exe -f %s up -d' % op.join(CWD, 'docker', 'docker-compose.yml')
        execute(cmd)
        return True, get_ln_containers()


def main():
    is_new_node, ln_containers = start_ln_network()
    confirm_all(ln_containers[0])
    exit(0)
    if is_new_node:
        time.sleep(30)
        top_up(ln_containers)
        time.sleep(30)
        create_channel(ln_containers)
        confirm_all(operator)


if __name__ == '__main__':
    main()
