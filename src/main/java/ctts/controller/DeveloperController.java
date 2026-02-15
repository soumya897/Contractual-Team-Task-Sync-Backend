package ctts.controller;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/developer")
public class DeveloperController {

    @GetMapping("/test")
    public String developerTest() {
        return "Developer access granted ✅";
    }
}
