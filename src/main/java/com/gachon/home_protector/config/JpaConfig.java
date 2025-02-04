package com.gachon.home_protector.config;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.JpaVendorAdapter;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;
import java.util.HashMap;

@Configuration
@EnableTransactionManagement
public class JpaConfig {

    @Value("${jpa.hibernate.ddl-auto}")
    String ddlAuto;

    @Value("${spring.jpa.database-platform}")
    String dbPlatform;

    @Bean
    public LocalContainerEntityManagerFactoryBean entityManagerFactory(
            @Qualifier("dataSource") DataSource dataSource) { // dataSource Bean 주입

        LocalContainerEntityManagerFactoryBean entityManagerFactory
                = new LocalContainerEntityManagerFactoryBean();

        entityManagerFactory.setDataSource(dataSource); //datasource 설정
        entityManagerFactory.setPackagesToScan("com.gachon.home_protector"); // home_protector 아래 모든 entity 대상으로
        entityManagerFactory.setJpaVendorAdapter(jpaVendorAdapter()); // JPA 벤더 어댑터 설정
        entityManagerFactory.setPersistenceUnitName("entityManager"); // 엔티티들의 영속성을 관리하는 단위

        HashMap<String, Object> prop = new HashMap<>();
        prop.put("hibernate.hbm2ddl.auto", ddlAuto);
        entityManagerFactory.setJpaPropertyMap(prop);
        
        return entityManagerFactory;
    }

    private JpaVendorAdapter jpaVendorAdapter() { // JPA 벤더 어댑터를 생성하는 메서드
        HibernateJpaVendorAdapter hibernateJpaVendorAdapter = new HibernateJpaVendorAdapter();
        hibernateJpaVendorAdapter.setDatabasePlatform(dbPlatform);
        return hibernateJpaVendorAdapter;
    }

    @Bean // 트랜잭션 관리자를 생성하는 빈 생성
    public PlatformTransactionManager transactionManager(
            @Qualifier("entityManagerFactory")
            LocalContainerEntityManagerFactoryBean entityManagerFactory
    ) {
        JpaTransactionManager jpaTransactionManager = new JpaTransactionManager();
        jpaTransactionManager.setEntityManagerFactory(entityManagerFactory.getObject());
        return jpaTransactionManager;
    }
}
