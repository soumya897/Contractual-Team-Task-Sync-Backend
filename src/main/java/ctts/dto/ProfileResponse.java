package ctts.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ProfileResponse {

    private String name;
    private String email;
    private String ph;
    private String role;

    private Long totalProjects;
    private Long totalDevelopers;
    private Long totalClients;

    private Long completedProjects;
    private Long ongoingProjects;
}
