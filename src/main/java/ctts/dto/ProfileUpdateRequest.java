package ctts.dto;

import lombok.Data;

@Data
public class ProfileUpdateRequest {
    private String name;
    private String phone; // This matches the "phone" key we send from React
}