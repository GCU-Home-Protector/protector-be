package com.gachon.home_protector.user;

import com.gachon.home_protector.user.dto.UserJoinRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/user")
public class UserController {

    private final UserService userService;

    @PostMapping
    public String join(@RequestBody UserJoinRequest request) {
        return userService.join(request.toServiceRequest());
    }

    @GetMapping
    public List<User> getAllUsers() {
        return userService.getAllUsers();
    }
}
