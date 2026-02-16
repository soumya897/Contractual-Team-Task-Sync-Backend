package ctts.dto;

import lombok.Data;

@Data
public class TaskRequest {

    private String title;
    private String description;

    private Long projectId;
    private Long developerId;

    private Boolean completed;   // for update
}
