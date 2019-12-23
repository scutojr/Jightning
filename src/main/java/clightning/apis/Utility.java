package clightning.apis;

public interface Utility {
    /**
     * check command_to_check
     *     Don't run {command_to_check}, just verify parameters.
     */
    void check();

    /**
     * checkmessage message zbase [pubkey]
     *     Verify a digital signature {zbase} of {message} signed with {pubkey}
     */
    void checkMessage();

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
    void listFunds();

    /**
     * signmessage message
     *     Create a digital signature of {message}
     */
    void signMessage();

    /**
     * stop
     *     Shut down the lightningd process
     */
    void stop();
}
