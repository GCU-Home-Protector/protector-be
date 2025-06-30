package com.gachon.home_protector.domain.user.dto.identification;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class IdentificationServiceRequest {

    private String password;

    @Builder
    private IdentificationServiceRequest(String password) {
        this.password = password;
    }


    public static IdentificationServiceRequest of(String password) {
        return IdentificationServiceRequest.builder()
                .password(password)
                .build();
    }
}
