package com.gachon.home_protector.user;

import com.gachon.home_protector.security.userdetails.RestUserDetails;
import com.gachon.home_protector.token.repository.IdentificationTokenRepository;
import com.gachon.home_protector.token.token.IdentificationToken;
import com.gachon.home_protector.user.dto.identification.IdentificationServiceRequest;
import com.gachon.home_protector.user.dto.join.RestUserJoinResponse;
import com.gachon.home_protector.user.dto.join.UserJoinServiceRequest;
import com.gachon.home_protector.user.exception.UserNotFoundException;
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




    public List<User> getAllUsers() {
        return userRepository.findAll();
    }




    public void identify(IdentificationServiceRequest serviceRequest, RestUserDetails userDetails, HttpServletResponse httpResponse) {
        String rawPassword = serviceRequest.getPassword();
        Long userId = userDetails.getId();

        User user = userRepository.findById(userId).orElseThrow(() ->
                new UserNotFoundException("해당 유저가 존재하지 않습니다!")
        );

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
}
