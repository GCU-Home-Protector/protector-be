package com.gachon.home_protector.user;

import com.gachon.home_protector.user.dto.join.RestUserJoinResponse;
import com.gachon.home_protector.user.dto.join.UserJoinServiceRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {

    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;

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
}
