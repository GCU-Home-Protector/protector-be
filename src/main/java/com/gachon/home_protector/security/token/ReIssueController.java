package com.gachon.home_protector.security.token;

import com.gachon.home_protector.api.SuccessResponse;
import com.gachon.home_protector.security.token.exception.TokenNotFoundException;
import io.micrometer.common.util.StringUtils;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class ReIssueController {

    private final RefreshTokenService refreshTokenService;

    /**
     * rt, at가 header 및 cookie에 존재하는 지 확인
     *
     * @param request
     * @param response
     * @return
     */
    @PostMapping("/reissue")
    public SuccessResponse<Object> reissueToken(HttpServletRequest request, HttpServletResponse response) {
       if (isTokenNotExist(request)) {
           throw new TokenNotFoundException("토큰이 존재하지 않습니다!");
       }
       refreshTokenService.reIssueAccessAndRefreshToken(request, response);
       return SuccessResponse.success("토큰 재발급에 성공했습니다!");
    }

    private boolean isTokenNotExist(HttpServletRequest request) {

        if (StringUtils.isEmpty(request.getHeader("Authorization"))) return true;

        String refresh = null;
        Cookie[] cookies = request.getCookies();
        for (Cookie cookie : cookies) {
            if (cookie.getName().equals("refresh")) {
                refresh = cookie.getValue();
            }
        }

        return refresh == null;
    }
}
