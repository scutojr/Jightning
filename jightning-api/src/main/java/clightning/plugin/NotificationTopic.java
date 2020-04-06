package clightning.plugin;

/**
 * Event topic
 */
public enum NotificationTopic {
    warning,
    connect,
    disconnect,
    forward_event,
    channel_opened,
    invoice_payment,
    sendpay_success,
    sendpay_failure
}
