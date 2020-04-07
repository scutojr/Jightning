package clightning.apis;

/**
 * State of the channel
 */
public enum ChannelState {
    OPENINGD,
    CHANNELD_AWAITING_LOCKIN,
    CHANNELD_NORMAL,
    CHANNELD_SHUTTING_DOWN,
    CLOSINGD_SIGEXCHANGE,
    CLOSINGD_COMPLETE,
    FUNDING_SPEND_SEEN,
    ONCHAIN,
    AWAITING_UNILATERAL;
}
