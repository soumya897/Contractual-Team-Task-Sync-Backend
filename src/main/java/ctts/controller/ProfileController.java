package ctts.controller;


import ctts.dto.ProfileResponse;
import ctts.dto.ProfileUpdateRequest;
import ctts.service.ProfileService;
import ctts.dto.ChangePasswordRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class ProfileController {

    private final ProfileService profileService;

    @GetMapping("/admin/profile")
    public ProfileResponse adminProfile() {
        return profileService.getProfile();
    }

    @GetMapping("/developer/profile")
    public ProfileResponse developerProfile() {
        return profileService.getProfile();
    }

    @GetMapping("/client/profile")
    public ProfileResponse clientProfile() {
        return profileService.getProfile();
    }

    @PutMapping("/profile/update")
    public ProfileResponse updateProfile(@RequestBody ProfileUpdateRequest request) {
        return profileService.updateProfile(request.getName(), request.getPhone());
    }

    @PutMapping("/profile/change-password")
    public ResponseEntity<String> changePassword(@RequestBody ChangePasswordRequest request) {
        try {
            String result = profileService.changePassword(request.getOldPassword(), request.getNewPassword());
            return ResponseEntity.ok(result);
        } catch (RuntimeException e) {
            // Returns a 400 Bad Request if the old password was wrong
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

}
