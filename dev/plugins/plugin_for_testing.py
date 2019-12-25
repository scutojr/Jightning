import sys
import json


def options():
    pass


def rpcmethods():
    pass


def subscriptions():
    pass


def hooks():
    pass


def dict_add_or_skip_on_none(dictionary, key, value):
    if value:
        dictionary[key] = value
    return dictionary


def getmanifest():
    res = {
        'dynamic': True
    }

    dict_add_or_skip_on_none(res, 'options', options())
    dict_add_or_skip_on_none(res, 'rpcmethods', rpcmethods())
    dict_add_or_skip_on_none(res, 'subscriptions', subscriptions())
    dict_add_or_skip_on_none(res, 'hooks', hooks())

    return json.dump(res, sys.stdout)


def init(feed_back):
    pass


class Plugin:
    def start():
        pass


if __name__ == '__main__':
    plugin = Plugin()
    plugin.start()
