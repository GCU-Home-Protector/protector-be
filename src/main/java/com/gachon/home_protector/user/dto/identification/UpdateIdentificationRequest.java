package com.gachon.home_protector.user.dto.identification;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class UpdateIdentificationRequest {

    @NotBlank(message = "새로운 사용자 ID는 필수입니다!")
    private String userId;

    @NotBlank(message = "새로운 사용자 비밀번호는 필수입니다!")
    private String password;

    @Builder
    private UpdateIdentificationRequest(String userId, String password) {
        this.userId = userId;
        this.password = password;
    }

    public static UpdateIdentificationRequest of (String userId, String password) {
        return UpdateIdentificationRequest.builder()
                .userId(userId)
                .password(password)
                .build();
    }

    public UpdateIdentificationServiceRequest toServiceRequest() {
        return UpdateIdentificationServiceRequest.of(userId, password);
    }
}
