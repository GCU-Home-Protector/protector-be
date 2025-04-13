package com.gachon.home_protector.user.annotation.valid_identification_token;

import com.gachon.home_protector.user.exception.EmptyIdentificationHeaderException;
import com.gachon.home_protector.user.exception.NullIdentificationHeaderException;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.MethodParameter;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

public class ValidIdentificationTokenArgumentResolver implements HandlerMethodArgumentResolver {

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        boolean isTokenType = String.class.isAssignableFrom(parameter.getParameterType());
        boolean isIdentificationToken = parameter.hasParameterAnnotation(ValidateIdentificationToken.class);
        return isTokenType && isIdentificationToken;
    }

    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer, NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {

        String identificationToken = webRequest.getHeader("Protector-Identification");

        // 401
        if (identificationToken == null) throw new NullIdentificationHeaderException("header의 값이 존재하지 않습니다!");

        // 500
        if (StringUtils.isBlank(identificationToken)) throw new EmptyIdentificationHeaderException("이전 페이지에서 비밀번호를 다시 입력해주세요!");

        return identificationToken;
    }
}
