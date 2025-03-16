package com.gachon.home_protector.security.handler;

import com.gachon.home_protector.MockTestSupport;
import com.gachon.home_protector.security.token.token.RefreshToken;
import com.gachon.home_protector.security.token.token.RestAuthenticationToken;
import com.gachon.home_protector.security.userdetails.RestUserDetails;
import com.gachon.home_protector.user.dto.RestUserLoginResponse;
import jakarta.servlet.ServletException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import java.io.IOException;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.*;

class RestAuthenticationSuccessHandlerTest extends MockTestSupport {

    @DisplayName("인증이 성공할 경우, at와 rt를 담아 반환한다.")
    @Test
    void onAuthenticationSuccess() throws ServletException, IOException {
        // given
        Long id = 1L;
        String userId = "userId";
        String password = "password";
        String role = "ROLE";

        String accessToken = "accessToken";

        String refreshTokenUuid = UUID.randomUUID().toString();
        RefreshToken refreshToken = RefreshToken.createRefreshToken(id, refreshTokenUuid);

        RestUserLoginResponse restUserLoginResponse = new RestUserLoginResponse(id, userId, password, role);
        RestUserDetails userDetails = new RestUserDetails(restUserLoginResponse);

        RestAuthenticationToken restAuthenticationToken =
                RestAuthenticationToken.createAuthenticatedToken(userDetails.getAuthorities(), userDetails);


        given(jwtUtil.createAccessToken(eq(id), eq(userId), eq(role), any())).willReturn(accessToken);

        // when
        restAuthenticationSuccessHandler.onAuthenticationSuccess(httpServletRequest, httpServletResponse, restAuthenticationToken);

        // then
        verify(jwtUtil).createAccessToken(eq(id), eq(userId), eq(role), any());
        verify(refreshTokenRepository).save(any(RefreshToken.class));

        verify(httpServletResponse).setHeader(eq("Authorization"), eq(accessToken));
//        verify(httpServletResponse).addCookie(argThat(c -> c.getName().equals("refresh") && c.getValue().equals(refreshTokenUuid)));
        verify(httpServletResponse).setStatus(HttpStatus.OK.value());


    }

}