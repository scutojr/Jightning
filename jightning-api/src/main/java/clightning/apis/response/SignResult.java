package clightning.apis.response;

import lombok.Data;

/**
 * Response of {@link clightning.apis.BasedUtility#signMessage}
 */
@Data
public class SignResult {
    private String signature;
    private String recid;
    private String zbase;
}
