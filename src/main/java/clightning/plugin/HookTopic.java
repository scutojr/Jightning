package clightning.plugin;

public enum HookTopic {
    peer_connected,
    db_write,
    invoice_payment,
    openchannel,
    htlc_accepted,
    rpc_command,
    custommsg
}
