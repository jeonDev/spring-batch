package com.service.batch.database;

import com.service.batch.database.domain.DatabaseData;
import jakarta.persistence.EntityManagerFactory;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.EnableJdbcJobRepository;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.core.job.Job;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.Step;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.infrastructure.item.ItemProcessor;
import org.springframework.batch.infrastructure.item.ItemReader;
import org.springframework.batch.infrastructure.item.ItemWriter;
import org.springframework.batch.infrastructure.item.database.Order;
import org.springframework.batch.infrastructure.item.database.builder.JdbcPagingItemReaderBuilder;
import org.springframework.batch.infrastructure.item.database.builder.JpaItemWriterBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;
import java.util.Map;

@Configuration
@EnableBatchProcessing
@EnableJdbcJobRepository(dataSourceRef = "batchDataSource", transactionManagerRef = "batchTransactionManager")
public class JdbcDatabaseJobConfig {

    private final JobRepository jobRepository;
    private final EntityManagerFactory entityManagerFactory;
    private final PlatformTransactionManager transactionManager;
    private final DataSource dataSource;

    public JdbcDatabaseJobConfig(JobRepository jobRepository,
                                 EntityManagerFactory entityManagerFactory,
                                 PlatformTransactionManager transactionManager,
                                 DataSource dataSource
    ) {
        this.jobRepository = jobRepository;
        this.entityManagerFactory = entityManagerFactory;
        this.transactionManager = transactionManager;
        this.dataSource = dataSource;
    }

    @Bean
    public Job jdbcDatabaseJob() throws Exception {
        return new JobBuilder("jdbcDatabaseJob", jobRepository)
                .start(jdbcDatabaseStep())
                .build();
    }

    @Bean
    @JobScope
    public Step jdbcDatabaseStep() throws Exception {
        return new StepBuilder("jdbcDatabaseStep", jobRepository)
                .<DatabaseData, DatabaseData>chunk(10)
                .transactionManager(transactionManager)
                .reader(itemReader())
                .processor(itemProcessor())
                .writer(itemWriter())
                .build();
    }

    private ItemReader<DatabaseData> itemReader() throws Exception {
        return new JdbcPagingItemReaderBuilder<DatabaseData>()
                .dataSource(dataSource)
                .name("jdbcDatabaseStepItemReader")
                .selectClause("id, DATA_A, DATA_B")
                .fromClause("FROM DATABASE_DATA")
                .whereClause("WHERE 1=1")
                .sortKeys(Map.of("id", Order.DESCENDING))
                .rowMapper((rs, rowNum) ->
                        DatabaseData.builder()
                                .id(rs.getLong(1))
                                .dataA(rs.getString(2))
                                .dataB(rs.getString(3))
                                .build()
                )
                .build();
    }

    private ItemProcessor<DatabaseData, DatabaseData> itemProcessor() {
        return item -> {
            item.updateDataA();

            return item;
        };
    }

    private ItemWriter<DatabaseData> itemWriter() {
        return new JpaItemWriterBuilder<DatabaseData>()
                .entityManagerFactory(entityManagerFactory)
                .build();
    }
}
