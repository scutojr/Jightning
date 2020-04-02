#!/usr/bin/env python

import os
import sys
import json
import time
import os.path as op
from threading import Thread
from argparse import ArgumentParser, Action
from subprocess import Popen, PIPE, call, check_output, check_call


ROOT_DIR = '/root/project'

COMPILE = 1 << 0
START_LIGHTNING_NETWORK = 1 << 1
START_PROXY = 1 << 2
RUN_TEST = 1 << 3
CLEAN = 1 << 4

PHASES = (START_LIGHTNING_NETWORK & 0) | COMPILE | START_PROXY | RUN_TEST | CLEAN


_is_test_finished = False


class Proxy(Thread):
    def __init__(self, name, cmd, root_dir=ROOT_DIR):
        super(Proxy, self).__init__()
        self.name = name
        self.cmd = cmd
        self.root_dir = root_dir
        self.proc = None

    def run(self):
        log('start proxy:', self.cmd)
        self.proc = Popen(self.cmd, stdout=PIPE, shell=True)
        src = self.proc.stdout

        log_file = op.join('jightning-api', 'target', 'proxy-%s.log' % self.name)
        with open(log_file, 'a') as sink:
            while True:
                line = src.readline(1024)
                if not line:
                    break
                sink.write(line)
                sink.flush()
        if not _is_test_finished:
            log('ERROR: proxy exit before test is finished!')

    def stop(self):
        self.stop_proxy(self.name)

    @staticmethod
    def stop_proxy(node_name):
        cmd = 'docker exec %s ps -ef|grep lnj.LightningTestingServer|awk "{print $2}"' % node_name
        pids = [int(pid) for pid in check_output(cmd, shell=True).split()]
        pids and call('docker exec %s kill -9 %d' % (node_name, pids[-1]))


def _command(cmd, stdout=sys.stdout, stderr=sys.stderr):
    return call(cmd, stdout=stdout, stderr=stderr, shell=True)


def log(*args):
    msg = ['@']
    msg.extend(args)
    print ' '.join(msg)


def goto_root_dir():
    cwd = op.dirname(op.abspath(__file__))
    os.chdir(op.join(cwd, '..'))


def set_up():
    if PHASES & START_LIGHTNING_NETWORK == START_LIGHTNING_NETWORK:
        log('start the lightning network')
    if PHASES & COMPILE == COMPILE:
        cmd = 'mvn test-compile'
        log('INFO: compiling the code by', cmd)
        _command(cmd)


def tear_down():
    if PHASES & CLEAN != CLEAN:
        return
    stop_proxys()


def start_proxys():
    if PHASES & START_PROXY != START_PROXY:
        return
    proxys = []
    cmd_proxy = 'mvn exec:java -Dexec.mainClass=lnj.LightningTestingServer -Dexec.classpathScope=test'
    for ln in ln_node_list():
        cmd = 'docker exec -w %s/%s %s %s' % (ROOT_DIR, 'jightning-api', ln, cmd_proxy)
        proxys.append(Proxy(ln, cmd))
    for p in proxys:
        p.daemon = True
        p.start()


def stop_proxys():
    log('close all the proxy inside lightning network')
    for node in ln_node_list():
        log('stopping proxy', node)
        Proxy.stop_proxy(node)
        log('proxy %s is stopped' % node)


def run_test(props):
    global _is_test_finished

    if PHASES & RUN_TEST != RUN_TEST:
        return

    cmd = ' '.join(['mvn', 'test'] + ['-D' + p for p in props])
    log('run test:', cmd)
    node = ln_node_list()[0]
    _command('docker exec -w %s %s %s' % (ROOT_DIR, node, cmd))
    _is_test_finished = True


def create_ln_network():
    log('create lightning network')
    script = op.join('dev', 'start_lightning_network.py')
    _command(script)


def close_ln_network():
    log('close the lightning network')
    nodes = ln_node_list()
    for node in nodes:
        _command('docker stop ' + node)
    for node in nodes:
        _command('docker rm ' + node)


def ln_node_list():
    f = op.join('jightning-api', 'src', 'test', 'resources', 'nodeIdToHost.json')
    try:
        with open(f) as src:
            nodes = json.load(src)
            return nodes.values()
    except:
        log('Error:', 'failed to open file ', f, ',the lightning network may not yet created!')
        exit(1)


def handle_test(props):
    try:
        set_up()
        start_proxys()
        time.sleep(20)
        run_test(props)
    finally:
        tear_down()


def handle_network(to_close):
    if to_restart:
        close_ln_network()
        create_ln_network()
    elif to_close:
        close_ln_network()


def handle_clean(target_dir, network, proxy):
    if target_dir:
        cmd = 'mvn clean'
        log('clean the target directory:', cmd)
        _command(cmd)
    if network:
        close_ln_network()
    if proxy:
        stop_proxys()


def parse_args():
    parser = ArgumentParser()
    subparsers = parser.add_subparsers(dest='command')

    parser_test = subparsers.add_parser('test', description='subcommand for test')
    parser_network = subparsers.add_parser('network',
            description='subcommand for network management, restart the lightning network by default')
    parser_clean = subparsers.add_parser('clean', description='subcommand for clean environment')

    parser_test.add_argument('-D', dest='props', action='append',
            help='specify the maven property such as -Dtest=xx.yy#zz')

    parser_network.add_argument('-c', dest='to_close', action='store_true',
            help='close the lightning network environment')

    parser_clean.add_argument('-t --target', dest='target_dir', action='store_true',
            help='clean the maven target directory')
    parser_clean.add_argument('-n --network', dest='network', action='store_true',
            help='clean the lightning network environment')
    parser_clean.add_argument('-p --proxy', dest='proxy', action='store_true',
            help='clean the proxy running inside the lightning network')
    
    return parser.parse_args()


def main(args):
    goto_root_dir()
    command = args.command

    if command == 'test':
        handle_test(args.props)
    elif command == 'network':
        handle_network(args.to_close, args.to_restart)
    elif command == 'clean':
        handle_clean(args.target_dir, args.network, args.proxy)


if __name__ == '__main__':
    args = parse_args()
    main(args)
