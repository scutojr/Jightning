package clightning.apis.response;

import lombok.Data;

@Data
public class SignResult {
    private String signature;
    private String recid;
    private String zbase;
}
