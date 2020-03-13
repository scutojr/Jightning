package clightning.apis.response;

import lombok.Data;

@Data
public class CommandUsage {
    private String command;
    private String category;
    private String description;
    private String verbose;
}
