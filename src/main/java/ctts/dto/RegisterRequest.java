package ctts.dto;

import ctts.entity.Role;
import lombok.Data;

@Data
public class RegisterRequest {
    private String name;
    private String ph;
    private String email;
    private String password;
    private Role role;
}
