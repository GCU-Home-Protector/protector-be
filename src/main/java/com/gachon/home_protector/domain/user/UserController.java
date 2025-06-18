package com.gachon.home_protector.domain.user;

import com.gachon.home_protector.domain.common.SuccessResponse;
import com.gachon.home_protector.domain.security.userdetails.RestUserDetails;
import com.gachon.home_protector.domain.user.annotation.valid_identification_token.ValidateIdentificationToken;
import com.gachon.home_protector.domain.user.dto.identification.IdentificationRequest;
import com.gachon.home_protector.domain.user.dto.identification.UpdateIdentificationRequest;
import com.gachon.home_protector.domain.user.dto.join.RestUserJoinResponse;
import com.gachon.home_protector.domain.user.dto.join.UserJoinRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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

    @PostMapping("/identification")
    public SuccessResponse<String> identification(@AuthenticationPrincipal RestUserDetails userDetails,
                                                  @Valid @RequestBody IdentificationRequest request,
                                                  HttpServletResponse response) {
        userService.identify(request.toServiceRequest(), userDetails, response);
        return SuccessResponse.success("비밀번호 인증에 성공하셨습니다!");
    }

    @PatchMapping("/update-identification")
    public SuccessResponse<String> updateIdentification(@AuthenticationPrincipal RestUserDetails userDetails,
                                                        @ValidateIdentificationToken String identificationToken,
                                                        @Valid @RequestBody UpdateIdentificationRequest request) {

        return SuccessResponse.success(userService.updateIdentification(userDetails, identificationToken, request.toServiceRequest()));
    }

    @GetMapping
    public List<User> getAllUsers() {
        return userService.getAllUsers();
    }
}
