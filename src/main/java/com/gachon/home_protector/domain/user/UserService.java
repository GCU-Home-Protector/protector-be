package com.gachon.home_protector.domain.user;

import com.gachon.home_protector.domain.security.userdetails.RestUserDetails;
import com.gachon.home_protector.domain.token.repository.IdentificationTokenRepository;
import com.gachon.home_protector.domain.token.token.IdentificationToken;
import com.gachon.home_protector.domain.user.dto.identification.IdentificationServiceRequest;
import com.gachon.home_protector.domain.user.dto.identification.UpdateIdentificationServiceRequest;
import com.gachon.home_protector.domain.user.dto.join.RestUserJoinResponse;
import com.gachon.home_protector.domain.user.dto.join.UserJoinServiceRequest;
import com.gachon.home_protector.domain.user.exception.DuplicatePasswordException;
import com.gachon.home_protector.domain.user.exception.DuplicateUserIdException;
import com.gachon.home_protector.domain.user.exception.InvalidIdentificationTokenException;
import com.gachon.home_protector.domain.user.exception.UserNotFoundException;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {

    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final IdentificationTokenRepository identificationTokenRepository;

    @Transactional
    public RestUserJoinResponse joinRestUser(UserJoinServiceRequest loginRequest) {

        if (Boolean.TRUE.equals(userRepository.existsByUserId(loginRequest.getUserId()))) {
            throw new IllegalArgumentException("이미 중복된 ID가 사용중입니다! 다른 ID를 사용해주세요!");
        }

        User restUser = makeUserBasedOn(loginRequest);
        User savedUser = userRepository.save(restUser);
        return savedUser.toRestUserJoinResponse();
    }

    private User makeUserBasedOn(UserJoinServiceRequest request) {
        User restUser = request.toRestUser();
        String encodedPassword = passwordEncoder.encode(restUser.getPassword());
        restUser.encryptPassword(encodedPassword);
        return restUser;
    }


    public void identify(IdentificationServiceRequest serviceRequest, RestUserDetails userDetails, HttpServletResponse httpResponse) {
        String rawPassword = serviceRequest.getPassword();
        Long userId = userDetails.getId();

        User user = findUserBy(userId);

        if (isInvalidPassword(rawPassword, user.getPassword())) {
            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다!");
        }

        setIdentificationTokenTo(httpResponse);
    }

    private void setIdentificationTokenTo(HttpServletResponse response) {
        String uuid = UUID.randomUUID().toString();
        IdentificationToken identificationToken = IdentificationToken.createIdentificationToken(uuid);
        identificationTokenRepository.save(identificationToken);
        response.setHeader("Protector-Identification", uuid);
    }

    private boolean isInvalidPassword(String rawPassword, String encodedPassword) {
        return !passwordEncoder.matches(rawPassword, encodedPassword);
    }

    @Transactional
    public String updateIdentification(RestUserDetails userDetails, String identificationToken, UpdateIdentificationServiceRequest serviceRequest) {

        // 1. 토큰 확인
        validateIdentificationToken(identificationToken);

        // 2. 정보 추출
        Long userId = userDetails.getId();
        String newUserId = serviceRequest.getUserId();
        String newPassword = serviceRequest.getPassword();

        // 3. 기존 userId, PW와 중복되는지 확인
        checkIfNewUserIdDuplicate(newUserId);
        User user = findUserBy(userId);
        checkIfNewPasswordDuplicate(passwordEncoder, user.getPassword(), newPassword);

        // 4. 사용된 identification token 제거
        removeAlreadyUsedIdentificationToken(identificationToken);

        // 5. 기존 유저 정보 업데이트
        user.updateIdentification(newUserId, passwordEncoder.encode(newPassword));
        userRepository.save(user);

        return "갱신에 성공했습니다!";
    }

    private void removeAlreadyUsedIdentificationToken(String identificationToken) {
        identificationTokenRepository.deleteById(identificationToken);
    }

    private static void checkIfNewPasswordDuplicate(PasswordEncoder passwordEncoder, String password, String newPassword) {

        if (passwordEncoder.matches(newPassword, password)) {
            throw new DuplicatePasswordException("다른 비밀번호를 사용해주세요!");
        }
    }

    private void checkIfNewUserIdDuplicate(String newUserId) {
        if (userRepository.existsByUserId(newUserId)) {
            throw new DuplicateUserIdException("이 ID는 이미 사용중입니다! 새로운 ID를 입력해주세요!");
        }
    }

    private void validateIdentificationToken(String identificationToken) {
        if (identificationTokenNotExists(identificationToken)) {
            throw new InvalidIdentificationTokenException("토큰 값이 존재하지 않습니다!");
        }
    }

    private User findUserBy(Long userId) {
        return userRepository.findById(userId).orElseThrow(() ->
                new UserNotFoundException("해당 유저가 존재하지 않습니다!")
        );
    }

    private boolean identificationTokenNotExists(String identificationToken) {
        return !identificationTokenRepository.existsById(identificationToken);
    }


    public List<User> getAllUsers() {
        return userRepository.findAll();
    }
}
