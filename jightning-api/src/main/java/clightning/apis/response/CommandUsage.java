package clightning.apis.response;

import lombok.Data;

/**
 * Response of {@link clightning.apis.BasedUtility#help}
 */
@Data
public class CommandUsage {
    private String command;
    private String category;
    private String description;
    private String verbose;
}
