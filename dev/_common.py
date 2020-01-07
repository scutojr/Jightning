import sys
import json
import subprocess


LN_NODE_NAMES = ['node1', 'node2', 'node3']


class Container:
    ID, IMAGE, COMMAND, CREATED, STATUS, PORTS, NAMES = range(7)
    def __init__(self, fields):
        self.id = fields[self.ID]
        self.image = fields[self.IMAGE]
        self.command = fields[self.COMMAND]
        self.created = fields[self.CREATED]
        self.status = fields[self.STATUS]
        self.ports = fields[self.PORTS]
        self.names = fields[self.NAMES]


def _is_ln_node(container):
    return any([
        n in container.names
        for n in LN_NODE_NAMES
    ])


def execute(cmd):
    try:
        output = subprocess.check_output(cmd, shell=True)
    except subprocess.CalledProcessError as e:
        print e.output
        raise e
    else:
        return output


def docker_exec(container_id, cmd):
    cmd = 'docker container exec %s %s' % (container_id, cmd)
    return execute(cmd)


def docker_containers():
    msg = execute('docker ps -a')
    lines = msg.split('\n')
    line_headers = lines[0]
    pos, indexs = 0, []
    headers = line_headers.split()
    headers[:2] = ['CONTAINER ID']
    for header in headers:
        end = pos + len(header)
        while end < len(line_headers) and line_headers[end] == ' ':
            end += 1
        if header == headers[-1]:
            indexs.append((pos, sys.maxint))
        else:
            indexs.append((pos, end))
        pos = end

    containers = []
    for line in lines[1:]:
        if not line.strip():
            continue
        fields = [line[start:end].strip() for start, end in indexs]
        containers.append(Container(fields))
    return containers


def get_ln_addr(container):
    msg = docker_exec(container.id, 'lightning-cli newaddr')
    addr = json.loads(msg)
    return addr['address']


def get_ln_id(container):
    cmd = 'lightning-cli getinfo'
    msg = docker_exec(container.id, cmd)
    return json.loads(msg)['id']


def get_ln_containers():
    containers = docker_containers()
    return [c for c in containers if _is_ln_node(c)]


def generate_blocks(container, cnt):
    msg = docker_exec(container.id, 'bitcoin-cli -regtest getnewaddress')
    docker_exec(
        container.id,
        'bitcoin-cli -regtest generatetoaddress %s %s' % (
            cnt, msg.strip()
        )
    )


def send_bitcoin(container, addr, amount):
    msg = docker_exec(container.id, 'bitcoin-cli -regtest sendtoaddress %s %s' % (addr, amount))


def confirm_all(operator, cnt_block=101):
    generate_blocks(operator, cnt_block)

