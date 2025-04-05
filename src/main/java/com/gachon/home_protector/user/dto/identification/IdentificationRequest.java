package com.gachon.home_protector.user.dto.identification;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class IdentificationRequest {

    @NotBlank(message = "비밀번호는 필수입니다!")
    private String password;

    @Builder
    private IdentificationRequest(String password) {
        this.password = password;
    }

    public static IdentificationRequest of (String pasword) {
        return IdentificationRequest.builder()
                .password(pasword)
                .build();
    }

    public IdentificationServiceRequest toServiceRequest() {
        return IdentificationServiceRequest.of(password);
    }
}
