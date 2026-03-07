package ctts.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ProjectManagerDashboardResponse {

    private long totalProjects;
    private long completedProjects;
    private long totalClients;
    private long totalDevelopers;
}
