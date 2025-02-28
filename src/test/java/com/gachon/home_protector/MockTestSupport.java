package com.gachon.home_protector;

import com.gachon.home_protector.security.provider.RestAuthenticationProvider;
import com.gachon.home_protector.security.userdetails.RestUserDetailsService;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

@ExtendWith(MockitoExtension.class)
public class MockTestSupport {

    // RestAuthenticationProvider 테스트 위해 주입
    @Mock
    protected RestUserDetailsService restUserDetailsService;

    @Mock
    protected PasswordEncoder passwordEncoder;

    @InjectMocks
    protected RestAuthenticationProvider restAuthenticationProvider;
}
