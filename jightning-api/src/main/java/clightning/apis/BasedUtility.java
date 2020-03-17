package clightning.apis;

import clightning.apis.optional.LogLevel;
import clightning.apis.response.*;

import java.io.IOException;

public interface BasedUtility {
    /**
     * check command_to_check
     * Don't run {command_to_check}, just verify parameters.
     */
    void check();

    /**
     * https://lightning.readthedocs.io/lightning-checkmessage.7.html
     * <p>
     * checkmessage message zbase [pubkey]
     * Verify a digital signature {zbase} of {message} signed with {pubkey}
     */
    CheckMessageResult checkMessage(String message, String zbase);

    CheckMessageResult checkMessage(String message, String zbase, String pubKey);

    /**
     * getinfo
     * Show information about this node
     */
    LightningDaemonInfo getInfo();

    /**
     * getlog [level]
     * Show logs, with optional log {level} (info|unusual|debug|io)
     */
    LogResult getLog();

    LogResult getLog(LogLevel level);

    /**
     * help [command]
     * List available commands, or give verbose help on one {command}.
     */
    CommandUsage[] help();

    CommandUsage help(String command);

    /**
     * listconfigs [config]
     * List all configuration options, or with [config], just that one.
     */
    Configuration listConfigs();

    /**
     * listfunds
     * Show available funds from the internal wallet
     */
    Funds listFunds();

    /**
     * https://lightning.readthedocs.io/lightning-signmessage.7.html
     * <p>
     * signmessage message
     * Create a digital signature of {message}
     */
    SignResult signMessage(String message);
}
