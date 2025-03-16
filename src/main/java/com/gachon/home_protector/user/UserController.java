package com.gachon.home_protector.user;

import com.gachon.home_protector.api.SuccessResponse;
import com.gachon.home_protector.user.dto.join.UserJoinRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/user")
public class UserController {

    private final UserService userService;

    @PostMapping("/join")
    public SuccessResponse<RestUserJoinResponse> joinRestUser(@Valid @RequestBody UserJoinRequest request) {
        return SuccessResponse.success(userService.joinRestUser(request.toServiceRequest()));
    }

    @GetMapping
    public List<User> getAllUsers() {
        return userService.getAllUsers();
    }
}
