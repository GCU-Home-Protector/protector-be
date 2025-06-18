package com.gachon.home_protector.global.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestClient;

//@Configuration
public class RestClientConfig {

    private static final int CONNECTION_TIMEOUT_LIMIT = 5000; // 5초
    private static final int PROCESS_TIMEOUT_LIMIT = 10000; // 10초

//    @Bean
    public RestClient restClient(RestClient.Builder builder) {
//        SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
//        requestFactory.setConnectTimeout(CONNECTION_TIMEOUT_LIMIT);
//        requestFactory.setReadTimeout(PROCESS_TIMEOUT_LIMIT);

        return builder
//                .requestFactory(requestFactory)
                .build();
    }
}
