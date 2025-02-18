package com.gachon.home_protector.user;

import com.gachon.home_protector.user.dto.UserJoinServiceRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {

    private final UserRepository userRepository;

    @Transactional
    public String join(UserJoinServiceRequest request) {
        User savedUser = userRepository.save(request.toUser());
        return savedUser.getUserId();
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }
}
