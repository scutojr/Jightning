# Jightning: a c-lightning client implemented in Java

* [Getting Started](#getting-started)
* [Development](#development)

// current version???

// doc?

# Getting Started

This project is a [c-lightning](https://github.com/ElementsProject/lightning) client implemented
in Java. It implements all the c-lightning command and provides an convenient way to develop plugin 
that run as a standalone process and can be integrated into c-lightning daemon to handle event as
well as provide extra command exposed by c-lightning command line.

This client communicates with c-lightning daemon via unix domain socket. Thus, this client must run
on the same machine as the c-lightning daemon.

## Installation

**TODO**

## Create an client

Create an client from lightning daemon object, run on testnet by default:
```java
package examples;

import clightning.LightningDaemon;
import clightning.Lnd;
import clightning.apis.LightningClient;
import clightning.apis.LightningClientImpl;

public class SimpleClient {
    public static void main(String[] args) {
        Lnd lnd = new LightningDaemon();
        LightningClient client = new LightningClientImpl(lnd);
        LightningDaemonInfo info = client.getInfo();
    }
}
```

Create an client via `LightningAppKit`
```java
package examples;

import clightning.LightningAppKit;
import clightning.Network;
import clightning.apis.LightningClient;

public class SimpleClient {

    public static void main(String[] args) {
        LightningAppKit appKit = new LightningAppKit(Network.regtest);
        LightningClient client = appKit.client();
        LightningDaemonInfo info = client.getInfo();
    }
}
```

## Manage c-lightning daemon

Creating an `LightningDaemon` object does not start an **lightningd**. Successful creation of
`LightningDaemon` relies on unix domain socket path **lightningd** listens on.`LightningDaemon`
obtains this path via command line `lightning-cli listconfigs` and therefore **lightningd** must
start before creation of `LightningDaemon`. Startup of **lightningd** can be delegated to `LightningDaemon`
by passing `tryStart` of true to `LightningDaemon(Network network, boolean tryStart)`.

Create lightning daemon object on regtest and start **lightningd**:
```java
    LightningDaemon lnd = new LightningDaemon(Network.regtest, true);
    lnd.startAsync();
    lnd.awaitRunning();
```

Create lightning daemon on regtest and start **lightningd**, add an monitor to handle shutdown event:
```java
    LightningAppKit appKit = new LightningAppKit(Network.regtest, true);
    appKit.addMonitor(new LightningAppKit.LndMonitor() {
        public void onShutDown(LightningDaemon lnd) {
            System.out.println("lightningd is shutdown");
        }
    });
    appKit.startAsync();
    appKit.awaitRunning();
    LightningDaemon lnd = appKit.lightningDaemon();
```

## Create an plugin

Plugin is run as a standalone process and communicates with **lightningd** via standard input/output.
It provides means to extend **lightningd** command and handles **lightningd** events.

To create a plugin, you need to extend `clightning.plugin.Plugin` and methods annotated with `Command`, `Hook`
and `Subscribe` change the capability of the plugin:
- Method annotated with `Command` will be mapped to command called by **lightning-cli**
- Method annotated with `Subscribe` will handle event from **lightningd** and return nothing
- Method annotated with `Hook` will handle event synchronously and **lightningd** relies on its
return value to take further action


Suppose we package the following code as simple-plugin.jar under /tmp:
```java
package examples;

import clightning.plugin.*;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.io.IOException;

public class SimplePlugin extends Plugin {

    protected Object initialize(JsonNode options, JsonNode configuration) {
        return "arbitrary data";
    }

    @Command(name = "example_cm1")
    public String cmd1(int p1, int p2, @DefaultTo("123") int p3) {
        return String.format("Calling example_cmd1 with %d %d %d", p1, p2, p3);
    }

    @Command
    public String cmd2(String p1, String p2) {
        return String.format("Calling cmd2 with %d %d", p1, p2);
    }

    @Subscribe(NotificationTopic.channel_opened)
    public void receiveNotification(String pp1) {

    }

    @Hook(HookTopic.db_write)
    public JsonNode handleHook(int data_version, JsonNode writes) {
        ObjectNode response = mapper.createObjectNode();
        response.put("result", "continue");
        return response;
    }

    public static void main(String[] args) throws IOException {
        SimplePlugin plugin = new SimplePlugin();
        plugin.addOption("option1", 100, "this is option1");
        plugin.addOption("option2", "default value", "this is option2");
        plugin.run(true);
    }
}
```

Then we write a bash script `/tmp/simple_plugin` to start the plugin:
```shell script
#/usr/bin/env bash
java -jar simple-plugin.jar
```

Start the plugin by lightning command:
```shell script
lightning-cli plugin start /tmp/simple_plugin
```

Now we can call the command handled by our plugin:
```shell script
[root@node1 tmp]# lightning-cli example_cm1 11 22
...
[root@node1 tmp]# lightning-cli example_cm1 11 22 33
...
[root@node1 tmp]# lightning-cli cmd2 11 22
...
```

# Development

This project is developed at my leisure and it would be great if it helps you solve the problems.
I know it would be a lot of improvement in terms of code readability, new feature, stability and
even performance and you are welcomed to contribute code to this project to make it better for any
purpose.

## Project Hierarchy


```text
|--clightning
|--|--apis
|--|--|--optional
|--|--|--response
|--|--plugin
```

## Prepare Environment

This project rely on **lightningd** to run on the same machine, we has created an environment for this via docker:
- run `./dev/docker/build-btc-image.sh` to create an image for bitcoin
- run `./dev/docker/build-ln-image.sh` to create an image for c-lightning

Create a lightning network of 2 node including **node1** and **node2**:
- `./dev/start_lightning_network.py`
- alternatively, restart the lightning network by `./dev/test_helper.py network`

Now we get a network of node1 and node2:
```shell script
$ docker ps -a
CONTAINER ID        IMAGE               COMMAND                  CREATED             STATUS                    PORTS                    NAMES
922a767211c5        lightning:v0.7.3    "bash -c 'sleep 2 &&…"   6 days ago          Up 6 days                 0.0.0.0:5005->5005/tcp   node1
21cb0ad97af6        lightning:v0.7.3    "bash -c 'bitcoind -…"   6 days ago          Up 6 days                 0.0.0.0:5006->5005/tcp   node2
```

Attach to a lightning node and do whatever you want:
```shell script
$ docker exec -it node1 bash
```

## About Test

Test can be classified into two group: unit test and integration test. Unit test run based on the test
data created by the user and stored into data files or hardcoded. Unit test does not rely on **lightnind**
to finish the work,Thus, it's lightweight and quick to verify the code. On the contrary, in order to ensure
the code can run in the production environment, our code must pass the integration test and during the test,
it will call commands of local **lightnind** and remote **lightningd**.

### Run test

Attach to the lightning node and run test directly:
```shell script
$ docker exec -it node1 bash
[root@node1 ~]# cd project/
[root@node1 project]# mvn test
...
[root@node1 project]# mvn test -Dtest=lnj.unit.TestLightningDaemon
...

[root@node1 project]# mvn test -Dmaven.surefire.debug -Dtest=lnj.unit.TestLightningDaemon
...
```

Some of the test need to execute command of local **lightnind** and remote **lightnind**
via a RPC proxy, `dev/test_helper.py` will prepare those requirement before you run the test.

`test_helper.py` can help you do following things:
- destroy and start the lightning network of two node: `test_helper.py network`
- destroy the lightning network: `test_helper.py network -c`
- run the test with lightning node proxy startup: `test_helper.py test`
- clean the environment including target directory, lightning network and proxy process: `test_helper.py clean`

Be default, out test lightning network contains two node including node1 and node2, running 

Run test via `test_helper.py`:
```shell script
$ ./dev/test_helper.py test
...
$ ./dev/test_helper.py test -Dtest=lnj.unit.TestLightningDaemon
...
$ ./dev/test_helper.py test -Dmaven.surefire.debug -Dtest=lnj.integration.TestBasedPayment
...
```
