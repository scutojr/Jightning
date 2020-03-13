#!/usr/bin/env python3

import sys
import socket
from threading import Thread, RLock


ENCODING = 'utf-8'


class Logger:
    def __init__(self):
        self.lock = RLock()

    def log_write(self, msg):
        with self.lock:
            with open('/tmp/plugin_write.log', 'ab') as sink:
                sink.write(msg)

    def log_read(self, msg):
        with self.lock:
            with open('/tmp/plugin_read.log', 'ab') as sink:
                sink.write(msg)

    def log_error(self, msg):
        with self.lock:
            with open('/tmp/plugin_error.log', 'ab') as sink:
                sink.write(msg)


class TcpTarget:
    def __init__(self, host, port):
        self.sock = socket.create_connection((host, port))

    def read(self, buffer):
        return self.sock.recv_into(buffer)

    def write(self, data):
        self.sock.sendall(data)

    def close(self):
        self.sock.close()


class StdIODelegator:
    def __init__(self, target):
        self.target = target
        self.is_running = False
        self.reader = self._create_reader()
        self.writer = self._create_writer()
        self.logger = Logger()

    def _create_reader(self):
        def reader():
            size = 10
            stdin = sys.stdin.buffer
            while self.is_running:
                data = stdin.readline(size)
                if data:
                    self.logger.log_read(data)
                    self.target.write(data)
                else:
                    self.stop()
        def wrapper():
            try:
                reader()
            except Exception as e:
                self.logger.log_error(str(e))
        return Thread(None, wrapper)

    def _create_writer(self):
        def writer():
            buf = bytearray(1024)
            stdout = sys.stdout.buffer
            while self.is_running:
                count = self.target.read(buf)
                if count > 0:
                    to_write = memoryview(buf)[:count]
                    self.logger.log_write(to_write)
                    stdout.write(to_write)
                    stdout.flush()
                else:
                    self.stop()
        def wrapper():
            try:
                writer()
            except Exception as e:
                self.logger.log_error(str(e))
        return Thread(None, wrapper)

    def start(self):
        reader, writer = self.reader, self.writer
        reader.daemon = True
        writer.daemon = True
        self.is_running = True
        reader.start()
        writer.start()
        writer.join()

    def stop(self):
        self.is_running = False
        self.target.close()


def main():
    host, port = 'localhost', 33557
    target = TcpTarget(host, port)
    delegator = StdIODelegator(target)
    delegator.start()


if __name__ == '__main__':
    main()
