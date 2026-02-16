package ctts.controller;

import ctts.dto.ProfileResponse;
import ctts.service.ProfileService;
import lombok.RequiredArgsConstructor;
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
    public ProfileResponse updateProfile(@RequestParam String name,
                                         @RequestParam String phone) {
        return profileService.updateProfile(name, phone);
    }

}
