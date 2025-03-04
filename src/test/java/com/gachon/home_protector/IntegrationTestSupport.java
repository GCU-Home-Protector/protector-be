package com.gachon.home_protector;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.utility.DockerImageName;

@ActiveProfiles("test")
@SpringBootTest
public abstract class IntegrationTestSupport {

    private static final String MYSQL_VERSION = "mysql:latest";
    private static final String REDIS_VERSION = "redis:latest";

    private static final MySQLContainer<?> mySQL;
    private static final GenericContainer Redis;

    public static final int REDIS_PORT = 6379;

    static {
        mySQL = new MySQLContainer<>(MYSQL_VERSION);
        Redis = new GenericContainer(DockerImageName.parse(REDIS_VERSION))
                .withExposedPorts(REDIS_PORT)
                .withReuse(true);

        mySQL.start();
        Redis.start();
    }

    @DynamicPropertySource
    public static void dynamicConfiguration(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", mySQL::getJdbcUrl);
        registry.add("spring.datasource.username", mySQL::getUsername);
        registry.add("spring.datasource.password", mySQL::getPassword);

        registry.add("spring.data.redis.host", Redis::getHost);
        registry.add("spring.data.redis.port", () -> String.valueOf(Redis.getMappedPort(REDIS_PORT)));
    }
}


