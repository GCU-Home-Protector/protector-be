package com.gachon.home_protector.domain.user.dto.identification;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class UpdateIdentificationServiceRequest {

    private String userId;

    private String password;

    @Builder
    private UpdateIdentificationServiceRequest(String userId, String password) {
        this.userId = userId;
        this.password = password;
    }

    public static UpdateIdentificationServiceRequest of(String userId, String password) {
        return UpdateIdentificationServiceRequest.builder()
                .userId(userId)
                .password(password)
                .build();
    }
}
