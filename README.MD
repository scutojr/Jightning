# Jightning: a c-lightning client implemented in Java

* [Getting Started](#getting-started)
* [Development](#development)

// current version???

# Getting Started

This project is a [c-lightning](https://github.com/ElementsProject/lightning) client implemented
in Java. It implements all the c-lightning command and provides an convenient way to develop plugin 
that run as a standalone process and can be integrated into c-lightning daemon to handle event as
well as provide extra command exposed by c-lightning command line.

This client communicates with c-lightning daemon via unix domain socket. Thus, this client must run
on the same machine as the c-lightning daemon.

- c-lightning client
- lightning daemon management
- plugin

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

```java

```

**lightningd** 

# Development

## Project Hierarchy
## Prepare Environment
## Running Test