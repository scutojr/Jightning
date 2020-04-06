package clightning.plugin;

/**
 * hook event type
 */
public enum HookTopic {
    peer_connected,
    db_write,
    invoice_payment,
    openchannel,
    htlc_accepted,
}
