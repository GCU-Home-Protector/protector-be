package com.gachon.home_protector.user;

import com.gachon.home_protector.api.ApiResponse;
import com.gachon.home_protector.user.dto.UserJoinRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/user")
public class UserController {

    private final UserService userService;

    @PostMapping
    public ApiResponse<RestUserJoinResponse> joinRestUser(@Valid @RequestBody UserJoinRequest request) {
        return ApiResponse.success(userService.joinRestUser(request.toServiceRequest()));
    }

    @GetMapping
    public List<User> getAllUsers() {
        return userService.getAllUsers();
    }
}
