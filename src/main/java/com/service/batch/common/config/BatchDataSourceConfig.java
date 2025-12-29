package com.service.batch.common.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;

@Configuration
@ConditionalOnProperty(prefix = "batch.datasource", name = "driver-class-name")
public class BatchDataSourceConfig {

    @Bean
    @ConfigurationProperties(prefix = "batch.datasource")
    public DataSource batchDataSource() {
        return DataSourceBuilder.create().build();
    }

    @Bean
    public PlatformTransactionManager batchTransactionManager() {
        return new DataSourceTransactionManager(batchDataSource());
    }
}
