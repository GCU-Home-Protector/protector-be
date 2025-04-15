package com.gachon.home_protector.user.annotation.valid_identification_token;

import com.gachon.home_protector.user.exception.EmptyIdentificationHeaderException;
import com.gachon.home_protector.user.exception.NullIdentificationHeaderException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.MethodParameter;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

@Slf4j
public class ValidIdentificationTokenArgumentResolver implements HandlerMethodArgumentResolver {

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        boolean isTokenType = String.class.isAssignableFrom(parameter.getParameterType());
        boolean isIdentificationToken = parameter.hasParameterAnnotation(ValidateIdentificationToken.class);
        return isTokenType && isIdentificationToken;
    }

    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer, NativeWebRequest webRequest, WebDataBinderFactory binderFactory) {
        String identificationToken = webRequest.getHeader("Protector-Identification");
        checkidentificationHeaderIsBlank(identificationToken);
        return identificationToken;
    }

    private static void checkidentificationHeaderIsBlank(String identificationToken) {
        // 401, custom header 자체가 없을 경우
        if (identificationHeaderNotExists(identificationToken)) {
            log.warn("header 없이 요청이 들어왔습니다!");
            throw new NullIdentificationHeaderException("header의 값이 존재하지 않습니다!");
        }

        // 500, custom header는 존재하지만 그 값이 비어 있는 경우
        if (identificationHeaderIsEmpty(identificationToken)) {
            log.error("header에 토큰 값이 비어 있습니다!");
            throw new EmptyIdentificationHeaderException("이전 페이지에서 비밀번호를 다시 입력해주세요!");
        }
    }

    private static boolean identificationHeaderIsEmpty(String identificationToken) {
        return StringUtils.isBlank(identificationToken);
    }

    private static boolean identificationHeaderNotExists(String identificationToken) {
        return identificationToken == null;
    }
}
