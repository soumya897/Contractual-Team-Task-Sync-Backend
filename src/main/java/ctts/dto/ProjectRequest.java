package ctts.dto;

import lombok.Data;

import java.util.List;

@Data
public class ProjectRequest {

    private String title;
    private String description;
    private String status;

    private Long clientId;
    private List<Long> developerIds;
}
