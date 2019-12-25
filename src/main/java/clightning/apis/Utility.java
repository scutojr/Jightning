package clightning.apis;

import clightning.apis.optional.LogLevel;
import clightning.apis.response.*;

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
    LightningDaemonInfo getInfo() throws IOException;

    /**
     * getlog [level]
     *     Show logs, with optional log {level} (info|unusual|debug|io)
     */
    LogResult getLog() throws IOException;

    LogResult getLog(LogLevel level) throws IOException;

    /**
     * help [command]
     *     List available commands, or give verbose help on one {command}.
     */
    CommandUsage[] help() throws IOException;
    CommandUsage help(String command) throws IOException;

    /**
     * listconfigs [config]
     *     List all configuration options, or with [config], just that one.
     */
    Configuration listConfigs() throws IOException;

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
}
