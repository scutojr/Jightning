package clightning.apis;

import clightning.apis.response.CheckMessageResult;
import clightning.apis.response.Funds;
import clightning.apis.response.SignResult;

import java.io.IOException;

public interface Utility {
    /**
     * check command_to_check
     *     Don't run {command_to_check}, just verify parameters.
     */
    void check();

    /**
     *
     * https://lightning.readthedocs.io/lightning-checkmessage.7.html
     *
     * checkmessage message zbase [pubkey]
     *     Verify a digital signature {zbase} of {message} signed with {pubkey}
     */
    CheckMessageResult checkMessage(String message, String zbase) throws IOException;

    CheckMessageResult checkMessage(String message, String zbase, String pubKey) throws IOException;

    /**
     * getinfo
     *     Show information about this node
     */
    void getInfo();

    /**
     * getlog [level]
     *     Show logs, with optional log {level} (info|unusual|debug|io)
     */
    void getLog();

    /**
     * help [command]
     *     List available commands, or give verbose help on one {command}.
     */
    void help();

    /**
     * listconfigs [config]
     *     List all configuration options, or with [config], just that one.
     */
    void listConfigs();

    /**
     * listfunds
     *     Show available funds from the internal wallet
     */
    Funds listFunds() throws IOException;

    /**
     * https://lightning.readthedocs.io/lightning-signmessage.7.html
     *
     * signmessage message
     *     Create a digital signature of {message}
     */
    SignResult signMessage(String message) throws IOException;

    /**
     * stop
     *     Shut down the lightningd process
     */
    void stop();
}
