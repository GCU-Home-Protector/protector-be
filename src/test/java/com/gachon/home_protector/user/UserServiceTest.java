package com.gachon.home_protector.user;

import com.gachon.home_protector.IntegrationTestSupport;
import com.gachon.home_protector.security.userdetails.RestUserDetails;
import com.gachon.home_protector.token.repository.IdentificationTokenRepository;
import com.gachon.home_protector.token.token.IdentificationToken;
import com.gachon.home_protector.user.dto.identification.UpdateIdentificationServiceRequest;
import com.gachon.home_protector.user.dto.join.RestUserJoinResponse;
import com.gachon.home_protector.user.dto.join.UserJoinServiceRequest;
import com.gachon.home_protector.user.dto.login.RestUserLoginResponse;
import com.gachon.home_protector.user.exception.DuplicatePasswordException;
import com.gachon.home_protector.user.exception.DuplicateUserIdException;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class UserServiceTest extends IntegrationTestSupport {

    @Autowired
    UserService userService;

    @Autowired
    UserRepository userRepository;

    @Autowired
    IdentificationTokenRepository identificationTokenRepository;

    @AfterEach
    void tearDown() {
        userRepository.deleteAllInBatch();
        identificationTokenRepository.deleteAll();
    }

    @DisplayName("회원가입할 수 있다.")
    @Test
    void joinRestUser() {
        // given
        String userId = "userId";
        String password = "password";
        UserJoinServiceRequest loginRequest = UserJoinServiceRequest.of(userId, password);

        // when
        RestUserJoinResponse restUserJoinResponse = userService.joinRestUser(loginRequest);

        // then
        assertThat(restUserJoinResponse)
                .extracting("userId", "role", "loginUserType")
                .containsExactlyInAnyOrder(userId, "ROLE_USER", LoginUserType.REST_LOGIN_USER);
    }

    @DisplayName("중복된 id를 사용하는 유저가 있다면 회원가입할 수 없다.")
    @Test
    void joinRestUser_DUPLICATE_USERID() {
        // given
        String userId = "userId";
        String password = "password";
        String role = "ROLE_USER";
        userRepository.save(User.createRestLoginUser(userId, password, role));

        String duplicateUserId = "userId";
        String newUserpassword = "password";
        UserJoinServiceRequest loginRequest = UserJoinServiceRequest.of(duplicateUserId, newUserpassword);

        // when // then
        assertThatThrownBy(() -> userService.joinRestUser(loginRequest))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("이미 중복된 ID가 사용중입니다! 다른 ID를 사용해주세요!");
    }


    @DisplayName("사용자 신원 정보를 변경할 수 있다.")
    @Test
    void updateIdentification() {
        // given
        String userId = "userId";
        String password = "password";
        String role = "ROLE_USER";
        User user = userRepository.save(User.createRestLoginUser(userId, password, role));

        String newUserId = "newUserId";
        String newPassword = "newPassword";
        UpdateIdentificationServiceRequest request = UpdateIdentificationServiceRequest.of(newUserId, newPassword);

        Long id = user.getId();
        RestUserLoginResponse response = new RestUserLoginResponse(id, userId, password, role);
        RestUserDetails restUserDetails = new RestUserDetails(response);

        String uuid = "uuid";
        identificationTokenRepository.save(IdentificationToken.createIdentificationToken(uuid));

        // when
        String result = userService.updateIdentification(restUserDetails, uuid, request);

        // then
        assertThat(result).isEqualTo("갱신에 성공했습니다!");

        Optional<User> resultUser = userRepository.findById(id);
        assertThat(resultUser).isPresent();
        assertThat(resultUser.get())
                .extracting("userId", "password")
                .containsExactlyInAnyOrder(newUserId, newPassword);
    }

    @DisplayName("토큰이 redis에 없을 경우에는 신원 정보를 변경할 수 없다.")
    @Test
    void updateIdentification_INVALID_TOKEN() {
        // given
        String userId = "userId";
        String password = "password";
        String role = "ROLE_USER";
        User user = userRepository.save(User.createRestLoginUser(userId, password, role));

        String newUserId = userId;
        String newPassword = "newPassword";
        UpdateIdentificationServiceRequest request = UpdateIdentificationServiceRequest.of(newUserId, newPassword);

        Long id = user.getId();
        RestUserLoginResponse response = new RestUserLoginResponse(id, userId, password, role);
        RestUserDetails restUserDetails = new RestUserDetails(response);

        String uuid = "uuid";


        // when // then
        assertThatThrownBy(() -> userService.updateIdentification(restUserDetails, uuid, request))
                .isInstanceOf(InvalidIdentificationTokenException.class)
                .hasMessage("토큰 값이 존재하지 않습니다!");
    }

    @DisplayName("이미 사용중인 ID로는 userId를 변경할 수 없다.")
    @Test
    void updateIdentification_DUPLICATE_USERID() {
        // given
        String userId = "userId";
        String password = "password";
        String role = "ROLE_USER";
        User user = userRepository.save(User.createRestLoginUser(userId, password, role));

        String newUserId = userId;
        String newPassword = "newPassword";
        UpdateIdentificationServiceRequest request = UpdateIdentificationServiceRequest.of(newUserId, newPassword);

        Long id = user.getId();
        RestUserLoginResponse response = new RestUserLoginResponse(id, userId, password, role);
        RestUserDetails restUserDetails = new RestUserDetails(response);

        String uuid = "uuid";
        identificationTokenRepository.save(IdentificationToken.createIdentificationToken(uuid));

        // when // then
        assertThatThrownBy(() -> userService.updateIdentification(restUserDetails, uuid, request))
                .isInstanceOf(DuplicateUserIdException.class)
                .hasMessage("이 ID는 이미 사용중입니다! 새로운 ID를 입력해주세요!");
    }

    @DisplayName("기존 비밀번호와 같은 비밀번호로는 변경할 수 없다.")
    @Test
    void updateIdentification_DUPLICATE_PASSWORD() {
        // given
        String userId = "userId";
        String password = "password";
        String role = "ROLE_USER";
        User user = userRepository.save(User.createRestLoginUser(userId, password, role));

        String newUserId = "newUserId";
        String newPassword = password;
        UpdateIdentificationServiceRequest request = UpdateIdentificationServiceRequest.of(newUserId, newPassword);

        Long id = user.getId();
        RestUserLoginResponse response = new RestUserLoginResponse(id, userId, password, role);
        RestUserDetails restUserDetails = new RestUserDetails(response);

        String uuid = "uuid";
        identificationTokenRepository.save(IdentificationToken.createIdentificationToken(uuid));

        // when // then
        assertThatThrownBy(() -> userService.updateIdentification(restUserDetails, uuid, request))
                .isInstanceOf(DuplicatePasswordException.class)
                .hasMessage("다른 비밀번호를 사용해주세요!");
    }

    @DisplayName("동일 토큰으로 신원정보 2회 변경 시나리오")
    @TestFactory
    Collection<DynamicTest> updateIdentification_DUPLICATE_CHANGE_SCENARIO() {
        String userId = "userId";
        String password = "password";
        String role = "ROLE_USER";
        User user = userRepository.save(User.createRestLoginUser(userId, password, role));

        String newUserId = "newUserId";
        String newPassword = "newPassword";
        UpdateIdentificationServiceRequest request = UpdateIdentificationServiceRequest.of(newUserId, newPassword);

        Long id = user.getId();
        RestUserLoginResponse response = new RestUserLoginResponse(id, userId, password, role);
        RestUserDetails restUserDetails = new RestUserDetails(response);

        String uuid = "uuid";
        identificationTokenRepository.save(IdentificationToken.createIdentificationToken(uuid));

        return List.of(
                DynamicTest.dynamicTest("유저의 신원정보를 변경할 수 있다.", () -> {

                    // when
                    String result = userService.updateIdentification(restUserDetails, uuid, request);

                    // then
                    assertThat(result).isEqualTo("갱신에 성공했습니다!");

                    Optional<User> resultUser = userRepository.findById(id);
                    assertThat(resultUser).isPresent();
                    assertThat(resultUser.get())
                            .extracting("userId", "password")
                            .containsExactlyInAnyOrder(newUserId, newPassword);

                }),
                DynamicTest.dynamicTest("이미 사용된 토큰은 신원정보 변경에 다시 활용될 수 없다.", () -> {

                    // when // then
                    assertThatThrownBy(() -> userService.updateIdentification(restUserDetails, uuid, request))
                            .isInstanceOf(InvalidIdentificationTokenException.class)
                            .hasMessage("토큰 값이 존재하지 않습니다!");
                })
        );
    }
}