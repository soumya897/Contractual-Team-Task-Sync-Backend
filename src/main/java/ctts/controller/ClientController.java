package ctts.controller;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/client")
public class ClientController {

    @GetMapping("/test")
    public String clientTest() {
        return "Client access granted ✅";
    }
}
