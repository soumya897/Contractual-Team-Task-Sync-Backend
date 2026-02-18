package ctts.dto;

import ctts.entity.ProjectStatus;
import lombok.Data;

import java.util.List;

@Data
public class ProjectRequest {

    private String title;
    private String description;

    private ProjectStatus status;

    private Long clientId;
    private List<Long> developerIds;
}
