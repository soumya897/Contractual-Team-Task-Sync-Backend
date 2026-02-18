package ctts.entity;
import ctts.entity.ProjectStatus;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;


import java.util.List;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Project {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    private String description;

    @Enumerated(EnumType.STRING)
    private ProjectStatus status;  // ONGOING / COMPLETED


    // 👤 Assigned Client
    @ManyToOne
    @JoinColumn(name = "client_id")
    private User client;

    // 👨‍💻 Assigned Developers
    @ManyToMany
    @JoinTable(
            name = "project_developers",
            joinColumns = @JoinColumn(name = "project_id"),
            inverseJoinColumns = @JoinColumn(name = "developer_id")
    )
    private List<User> developers;

    // 👨‍💼 Created By Admin
    @ManyToOne
    @JoinColumn(name = "admin_id")
    private User createdBy;

    // 🔥 ADD THIS BELOW
    @OneToMany(mappedBy = "project", cascade = CascadeType.ALL)
    @JsonIgnore
    private List<Task> tasks;
}
