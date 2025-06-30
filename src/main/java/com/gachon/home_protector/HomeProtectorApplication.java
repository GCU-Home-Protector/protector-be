package com.gachon.home_protector;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class HomeProtectorApplication {

	public static void main(String[] args) {
		try {
			SpringApplication.run(HomeProtectorApplication.class, args);
		} catch (Exception e) {
			e.printStackTrace(); // 콘솔에 전체 예외 스택트레이스 출력
			// 또는 로그로 남기기
			// log.error("Spring Boot failed to start", e);
		}
	}
}
